package io.github.heathensoft.core.window.callbacks;

import org.lwjgl.glfw.GLFWWindowIconifyCallback;

/**
 * @author Frederik Dahl
 * 16/06/2022
 */


public class WindowIconifyCallback extends GLFWWindowIconifyCallback {
    
    private boolean iconified;
    
    @Override
    public void invoke(long window, boolean iconified) {
        this.iconified = iconified;
    }
    
    public boolean isMinimized() {
        return iconified;
    }
}
