package io.github.heathensoft.core.window.callbacks;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class FrameBufferCallback extends GLFWFramebufferSizeCallback {
    
    private boolean event;
    private int width;
    private int height;
    
    @Override
    public void invoke(long window, int width, int height) {
        if (width > 0 && height > 0) {
            this.height = height;
            this.width = width;
            this.event = true;
        }
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public boolean event() {
        return event;
    }
    
    public void reset() {
        event = false;
    }
}
