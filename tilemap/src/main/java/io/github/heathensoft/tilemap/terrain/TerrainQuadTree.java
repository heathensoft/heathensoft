package io.github.heathensoft.tilemap.terrain;


import io.github.heathensoft.common.Utils;
import io.github.heathensoft.tilemap.MapSize;
import org.joml.primitives.Rectanglef;

/**
 * Quadtree for orthographic view culling. No LOD.
 * Query returns the coordinate and center-point of leaf-nodes in view.
 *
 * @author Frederik Dahl
 * 13/05/2022
 */


public class TerrainQuadTree {
 
    private final static QTItr QT_ITR = (x, y, cX, cY, s) -> {};
    private QTItr itr = QT_ITR;
    private final int size;
    private final int lim;
    private float x0 = 0;
    private float y0 = 0;
    
    public TerrainQuadTree(MapSize mapSize) {
        this(mapSize.log2, Utils.log2(32));
    }
    
    /**
     * @param pow2_tree scale of the quadTree, in power of two
     * @param pow2_leaf size of leaf, in power of two
     */
    public TerrainQuadTree(int pow2_tree, int pow2_leaf) {
        pow2_tree = Math.max(1,pow2_tree);
        pow2_leaf = Math.min(pow2_tree,pow2_leaf);
        int base = (int) Math.pow(2,pow2_leaf);
        this.size = (int) Math.pow(2,pow2_tree);
        this.lim = pow2_tree - Utils.log2(base);
    }
    
    public void setOffset(float xOff, float yOff) {
        this.x0 = xOff; this.y0 = yOff;
    }
    
    /**
     * The iterator will be called on queries
     * @param itr TerrainQT.Itr (iterator)
     */
    public void setIterator(QTItr itr) {
        this.itr = itr == null ? QT_ITR : itr;
    }
    
    /**
     * Query the tree given 2 points p1 and p2 forming a rectangle
     * @param p1X p1
     * @param p1Y p1
     * @param p2X p2
     * @param p2Y p2
     */
    public void query(float p1X, float p1Y, float p2X, float p2Y) {
        float minX, minY, maxX, maxY;
        if (p1X < p2X) { minX = p1X; maxX = p2X;
        } else { minX = p2X; maxX = p1X; }
        if (p1Y < p2Y) { minY = p1Y; maxY = p2Y;
        } else { minY = p2Y; maxY = p1Y; }
        query(x0,y0,size,minX,minY,maxX,maxY,0);
    }
    
    public void query(Rectanglef rect) {
        query(x0,y0,size,rect.minX,rect.minY,rect.maxX,rect.maxY,0);
    }
    
    private void query(float x0, float y0, float s, float cMinx, float cMinY, float cMaxX, float cMaxY, float d) {
        float nMaxX = x0 + s; float nMaxY = y0 + s;
        if (x0 < cMaxX && nMaxX > cMinx && nMaxY > cMinY && y0 < cMaxY) {
            float sH = s / 2; float cX = x0 + sH; float cY = y0 + sH;
            if (d++ < lim) {
                query(x0,y0,sH,cMinx,cMinY,cMaxX,cMaxY,d);
                query(cX,y0,sH,cMinx,cMinY,cMaxX,cMaxY,d);
                query(x0,cY,sH,cMinx,cMinY,cMaxX,cMaxY,d);
                query(cX,cY,sH,cMinx,cMinY,cMaxX,cMaxY,d);
                return; } itr.pass((int)(x0/s),(int)(y0/s),cX,cY,s);
        }
    }
    
    public interface QTItr {
        /**
         * @param x leaf x coord
         * @param y leaf y coord
         * @param cX leaf center x
         * @param cY leaf center y
         * @param s leaf size
         */
        void pass(int x, int y, float cX, float cY, float s);
    }
}
