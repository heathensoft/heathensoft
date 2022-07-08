package io.github.heathensoft.graphics;


import io.github.heathensoft.common.Disposable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 11/05/2022
 */


public abstract class Framebuffer implements Disposable {
    
    protected final int id;
    protected int width;
    protected int height;
    protected int clearMask = GL_COLOR_BUFFER_BIT;
    public Color clearColor = new Color(0.2f,0.2f,0.2f,1.0f);
    
    
    public Framebuffer(int width, int height) {
        this.id = glGenFramebuffers();
        this.width = width;
        this.height = height;
    }
    
    public void bindDraw() {
        gl.framebuffer.bindDraw(this);
    }
    
    public void bindRead() {
        gl.framebuffer.bindRead(this);
    }
    
    public void bind() {
        gl.framebuffer.bind(this);
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
        gl.framebuffer.onDispose(this);
        glDeleteFramebuffers(id);
        disposeInternal();
    }
    
    /** dispose textures (framebuffer deleted atp) */
    protected abstract void disposeInternal();
}
