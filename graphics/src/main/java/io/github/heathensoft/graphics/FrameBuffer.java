package io.github.heathensoft.graphics;


import io.github.heathensoft.common.Disposable;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 11/05/2022
 */


public abstract class FrameBuffer implements Disposable {
    
    protected final int id;
    protected int width;
    protected int height;
    protected int clearMask = GL_COLOR_BUFFER_BIT;
    public Vector4f clearColor = new Vector4f(0.2f,0.2f,0.2f,1.0f);
    
    
    public FrameBuffer(int width, int height) {
        this.id = glGenFramebuffers();
        this.width = width;
        this.height = height;
    }
    
    public void resize(int width, int height) { }
    
    public void setClearMask(int mask) {
        clearMask = mask;
    }
    
    public void setClearColor(float r, float g, float b, float a) {
        clearColor.set(r,g,b,a);
    }
    
    public void setClearColor(float r, float g, float b) {
        clearColor.set(r,g,b,1);
    }
    
    public int id() {
        return id;
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    @Override
    public void dispose() {
        GLGraphics.get().framebuffer.onDispose(this);
        glDeleteFramebuffers(id);
        disposeInternal();
    }
    
    /** dispose textures (framebuffer deleted atp) */
    protected abstract void disposeInternal();
}
