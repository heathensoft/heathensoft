package io.github.heathensoft.tilemap.terrain;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.common.Disposable;
import io.github.heathensoft.graphics.texture.Texture2D;
import io.github.heathensoft.math.MathLib;
import io.github.heathensoft.storage.primitive.IntStack;
import io.github.heathensoft.storage.primitive.ShortArray2D;
import io.github.heathensoft.storage.primitive.WriteFunction;
import io.github.heathensoft.tilemap.TerrainType;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4i;
import org.joml.primitives.Rectanglei;
import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_4_4_4_4;

/**
 * @author Frederik Dahl
 * 27/06/2022
 */


public class TerrainCanvas implements Disposable {
    
    private TerrainEditCallback callback;
    private TerrainType terrainType;
    private final Vector2i tmp;
    private final Vector2i stroke_min;
    private final Vector2i stroke_max;
    private final ShortArray2D canvas;
    private final Texture2D texture;
    private final IntStack points;
    private final boolean[][] mask;
    private final int rows;
    private final int cols;
    private float brushRadius;
    private float sampleDelta;
    private boolean modified;
    private boolean clearMode;
    
    public TerrainCanvas(ShortArray2D canvas) {
        this.canvas = canvas;
        terrainType = TerrainType.PRIMARY;
        texture = new Texture2D();
        texture.bind();
        texture.nearest();
        texture.clampToEdge();
        texture.RGBA4(canvas);
        rows = texture.height();
        cols = texture.width();
        brushRadius = 1f;
        sampleDelta = 1f;
        callback = callbackInternal;
        points = new IntStack(Math.max(64,(rows * cols)/8));
        tmp = new Vector2i();
        stroke_max = new Vector2i(0,0);
        stroke_min = new Vector2i(cols-1,rows-1);
        mask = new boolean[rows][cols];
    }
    
    public TerrainCanvas(int width, int height) {
        terrainType = TerrainType.PRIMARY;
        texture = new Texture2D();
        texture.bind();
        texture.nearest();
        texture.clampToEdge();
        texture.RGBA4((ShortBuffer) null,width,height);
        rows = texture.height();
        cols = texture.width();
        brushRadius = 1f;
        sampleDelta = 1f;
        callback = callbackInternal;
        points = new IntStack(Math.max(64,(rows * cols)/8));
        tmp = new Vector2i();
        stroke_max = new Vector2i(0,0);
        stroke_min = new Vector2i(cols-1,rows-1);
        mask = new boolean[rows][cols];
        canvas = new ShortArray2D(rows,cols);
    }
    
    private void flush() {
        final int br = (int) brushRadius;
        final int minX = Math.max(stroke_min.x - br,0);
        final int minY = Math.max(stroke_min.y - br,0);
        final int maxX = Math.min(stroke_max.x + br,cols-1);
        final int maxY = Math.min(stroke_max.y + br,rows-1);
        BrushStroke stroke = new BrushStroke(minX,minY,maxX,maxY);
        ShortBuffer buffer = stroke.buffer();
        if (clearMode) {
            if (terrainType == TerrainType.PRIMARY) {
                canvas.setWriteFunction(WriteFunction.AND);
            } else canvas.setWriteFunction(WriteFunction.CLR);
        } else {
            canvas.setWriteFunction(WriteFunction.SET);
        }
        
        short rgba4 = terrainType.rgba4;
        for (int r = minY; r <= maxY; r++) {
            for (int c = minX; c <= maxX; c++) {
                if (mask[r][c]) {
                    short oldMask = canvas.get(c,r);
                    canvas.write(rgba4,c,r);
                    short newMask = canvas.get(c,r);
                    if (newMask != oldMask)
                        callback.onEdit(r,c,oldMask,newMask);
                    mask[r][c] = false;
                }buffer.put(canvas.get(c,r));}
        } stroke_min.set(cols-1,rows-1);
        stroke_max.set(0,0);
        buffer.flip();
        upload(stroke);
        stroke.dispose();
        if (points.capacity() > rows * cols)
            points.fit(Math.max(64,(rows * cols)/8));
        modified = true;
    }
    
    private void upload(BrushStroke stroke) {
        Assert.notNull(stroke);
        texture.bind();
        ShortBuffer buffer = stroke.buffer();
        int minX = stroke.area().x;
        int minY = stroke.area().y;
        int maxX = stroke.area().z;
        int maxY = stroke.area().w;
        int w = 1 + maxX - minX;
        int h = 1 + maxY - minY;
        glTexSubImage2D(GL_TEXTURE_2D,0,minX,minY,w,h,GL_RGBA,GL_UNSIGNED_SHORT_4_4_4_4,buffer);
    }
    
    
    public void drawPoint(Vector2f p) {
        Assert.notNull(p);
        int x = Math.round(p.x);
        int y = Math.round(p.y);
        drawPoint(x,y);
    }
    
    public void drawPoint(int px, int py) {
        drawPointInternal(px, py);
        flush();
    }
    
    public void drawLine(Vector2f p1, Vector2f p2) {
        Assert.notNull(p1,p2);
        int x1 = Math.round(p1.x);
        int y1 = Math.round(p1.y);
        int x2 = Math.round(p2.x);
        int y2 = Math.round(p2.y);
        drawLine(x1,y1,x2,y2);
    }
    
    
    public void drawLine(int x1, int y1, int x2, int y2) {
        points.clear();
        Vector2f P1 = MathLib.vec2(x1,y1);
        Vector2f P2 = MathLib.vec2(x2,y2);
        Vector2f dir = MathLib.vec2();
        P2.sub(P1,dir).normalize();
        final float l = P1.distance(P2);
        final int samples = (int)(l / sampleDelta) + 1;
        Vector2f sample = MathLib.vec2();
        int px = x2;
        int py = y2;
        for (int i = 0; i < samples; i++) {
            sample.set(dir).mul(i * sampleDelta);
            sample.add(P1);
            px = Math.round(sample.x);
            py = Math.round(sample.y);
            points.push(px);
            points.push(py);
        } if (!points.isEmpty()) {
            if (px != x2 || py != y2) {
                points.push(x2);
                points.push(y2);
            } while (!points.isEmpty()) {
                px = points.pop();
                py = points.pop();
                drawPointInternal(px,py);
            }
        } flush();
    }
    
    public void drawRectangle(int x1, int y1, int x2, int y2) {
        int minX, minY, maxX, maxY;
        if (x1 > x2) { minX = x2; maxX = x1;
        } else { minX = x1; maxX = x2;
        } if (y1 > y2) { minY = y2; maxY = y1;
        } else { minY = y1; maxY = y2;}
        final int limX = cols - 1;
        final int limY = rows - 1;
        Rectanglei rect1 = new Rectanglei(0,0,cols-1,rows-1);
        Rectanglei rect2 = new Rectanglei(minX,minY,maxX,maxY);
        if (rect1.intersectsRectangle(rect2)) {
            minX = Math.max(0,minX);
            minY = Math.max(0,minY);
            maxX = Math.min(cols-1,maxX);
            maxY = Math.min(rows-1,maxY);
            tmp.set(maxX,maxY);
            stroke_max.max(tmp);
            tmp.set(minX,minY);
            stroke_min.min(tmp);
            for (int r = minY; r <= maxY; r++) {
                for (int c = minX; c <= maxX; c++) {
                    mask[r][c] = true;}
            } float tmp = brushRadius;
            brushRadius = 0;
            flush();
            brushRadius = tmp;
        }
    }
    
    public void drawPoints(IntStack points) {
        Assert.notNull(points);
        if (points.isEmpty()) return;
        if ((points.size() & 0x01) != 1) {
            int px = points.pop();
            int py = points.pop();
            drawPointInternal(px,py);
            int prev_px;
            int prev_py;
            while (!points.isEmpty()) {
                prev_px = px;
                prev_py = py;
                px = points.pop();
                py = points.pop();
                if (px == prev_px && py == prev_py) continue;
                drawPointInternal(px,py);
            }
            flush();
        }
    }
    
    
    private void drawPointInternal(int px, int py) {
        final int limX = cols - 1;
        final int limY = rows - 1;
        if (px < 0 || px > limX || py < 0 || py > limY) return;
        tmp.set(px,py);
        stroke_max.max(tmp);
        stroke_min.min(tmp);
        final float br = brushRadius;
        final int x1 = max(0,px - (int) br);
        final int y1 = max(0,py - (int) br);
        final int x2 = min(limX,px + (int) br);
        final int y2 = min(limY,py + (int) br);
        float a, b, k;
        float d2 = br * br;
        for (int r = y1; r <= y2; r++) {
            for (int c = x1; c <= x2; c++) {
                a = r - py;
                b = c - px;
                k = a * a + b * b;
                if (d2 >= k) mask[r][c] = true;
            }
        }
    }
    
    public boolean isClearMode() {
        return clearMode;
    }
    
    public void setClearMode(boolean clearMode) {
        this.clearMode = clearMode;
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    public Texture2D texture() {
        return texture;
    }
    
    public void setCallback(TerrainEditCallback callback) {
        this.callback = callback == null ? callbackInternal : callback;
    }
    
    public void setBrushRadius(float brushRadius) {
        this.brushRadius = brushRadius;
    }
    
    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }
    
    public void setSampleDelta(float sampleDelta) {
        this.sampleDelta = sampleDelta;
    }
    
    private final TerrainEditCallback callbackInternal = (row, col, oldMask, newMask) -> {
        /* ------------ */
    };
    
    @Override
    public void dispose() {
        Disposable.dispose(texture);
    }
    
    
    private static final class BrushStroke implements Disposable {
        
        private final ShortBuffer buffer;
        private final Vector4i area;
        
        
        BrushStroke(Vector4i area) {
            Assert.notNull(area);
            int cols = 1 + Math.abs(area.z - area.x);
            int rows = 1 + Math.abs(area.w - area.y);
            this.buffer = MemoryUtil.memAllocShort(cols*rows);
            this.area = area;
        }
        
        BrushStroke(int minX, int minY, int maxX, int maxY) {
            this(new Vector4i(minX,minY,maxX,maxY));
        }
        
        Vector4i area() {
            return area;
        }
        
        ShortBuffer buffer() {
            return buffer;
        }
        
        
        @Override
        public void dispose() {
            MemoryUtil.memFree(buffer);
        }
    }
}
