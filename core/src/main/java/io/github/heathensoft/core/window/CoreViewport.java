package io.github.heathensoft.core.window;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.common.Print;
import io.github.heathensoft.core.Application;
import io.github.heathensoft.core.window.callbacks.FrameBufferCallback;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

/**
 * @author Frederik Dahl
 * 18/06/2022
 */


public class CoreViewport {
    
    private final ViewPortArea transientArea;
    private final ViewPortArea glViewportArea;
    private final float aspectRatio;
    private float heightINV;
    private float widthINV;
    private boolean modified;
    
    
    protected CoreViewport(int targetResWidth, int targetResHeight) {
        this.aspectRatio = (float) targetResWidth / targetResHeight;
        this.glViewportArea = new ViewPortArea(0,0,targetResWidth,targetResHeight);
        fit(targetResWidth,targetResHeight);
        this.transientArea = new ViewPortArea(glViewportArea);
    }
    
    protected void update(Application app, FrameBufferCallback callback) {
        Assert.notNull(callback);
        if (callback.event()) {
            fit(callback.width(),callback.height());
            app.onResize(width(),height());
            callback.reset();
        } if (modified) reset();
    }
    
    private void fit(int width, int height) {
        int aw = width;
        int ah = Math.round ((float)aw / aspectRatio);
        if (ah > height) {
            ah = height;
            aw = Math.round((float)ah * aspectRatio);
        }
        glViewportArea.x = Math.round(((float) width / 2f) - ((float)aw / 2f));
        glViewportArea.y = Math.round(((float) height / 2f) - ((float)ah / 2f));
        glViewportArea.w = aw;
        glViewportArea.h = ah;
        heightINV = 1f / ah;
        widthINV =  1f / aw;
        modified = true;
    }
    
    public void set(int x, int y, int width, int height) {
        ViewPortArea area = new ViewPortArea(x,y,width,height);
        if (!area.equals(transientArea)) {
            transientArea.set(area);
            transientArea.glViewport();
            modified = true;
        }
    }
    
    public void reset() {
        if (!glViewportArea.equals(transientArea)) {
            transientArea.set(glViewportArea);
            transientArea.glViewport();
        } modified = false;
    }
    
    public void set(int width, int height) {
        set(0,0,width,height);
    }
    
    public int x() {
        return glViewportArea.x;
    }
    
    public int y() {
        return glViewportArea.y;
    }
    
    public int width() {
        return glViewportArea.w;
    }
    
    public int height() {
        return glViewportArea.h;
    }
    
    public float widthINV() {
        return widthINV;
    }
    
    public float heightINV() {
        return heightINV;
    }
    
    public float aspectRatio() {
        return aspectRatio;
    }
    
    public void printViewport() {
        System.out.println(transientArea);
    }
    
    private final static class ViewPortArea {
        
        int x,y,w,h;
        
        ViewPortArea(int x, int y, int w, int h) {
            this.x = x; this.y = y;
            this.w = w; this.h = h;
        }
        
        ViewPortArea(ViewPortArea area) {
            this(area.x,area.y,area.w,area.h);
        }
        
        void set(ViewPortArea area) {
            this.x = area.x;
            this.y = area.y;
            this.w = area.w;
            this.h = area.h;
        }
        
        void glViewport() {
            GL11.glViewport(x,y,w,h);
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ViewPortArea values = (ViewPortArea) o;
            return x == values.x && y == values.y && w == values.w && h == values.h;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y, w, h);
        }
    
        @Override
        public String toString() {
            return "ViewPortArea{" +
                           "x=" + x +
                           ", y=" + y +
                           ", w=" + w +
                           ", h=" + h +
                           '}';
        }
    }
    
}
