package io.github.heathensoft.core.window.callbacks;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class WindowResizeCallback extends GLFWWindowSizeCallback {
    
    private int w;
    private int h;
    
    public WindowResizeCallback(int w, int h) {
        this.w = w;
        this.h = h;
    }
    
    @Override
    public void invoke(long window, int w, int h) {
        this.w = w;
        this.h = h;
    }
    
    public int height() {
        return h;
    }
    
    public int width() {
        return w;
    }
}
