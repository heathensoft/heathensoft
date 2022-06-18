package io.github.heathensoft.core.window.callbacks;

import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 * The GLFW window coordinate system has origin in the top left corner,
 * y-axis pointing downwards.
 *
 * @author Frederik Dahl
 * 21/10/2021
 */


public class MouseHoverCallback extends GLFWCursorPosCallback {
    
    private double x, y;
    
    @Override
    public void invoke(long window, double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double x() {
        return x;
    }
    
    public double y() {
        return y;
    }
    
    
}
