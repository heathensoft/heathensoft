package io.github.heathensoft.core.window.callbacks;

import org.lwjgl.glfw.GLFWWindowPosCallback;

/**
 * @author Frederik Dahl
 * 21/10/2021
 */


public class WindowPositionCallback extends GLFWWindowPosCallback {
    
    private int x, y;
    
    public WindowPositionCallback() {
        this.x = 0;
        this.y = 0;
    }
    
    @Override
    public void invoke(long window, int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }
}
