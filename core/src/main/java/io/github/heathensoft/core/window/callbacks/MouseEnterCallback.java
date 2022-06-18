package io.github.heathensoft.core.window.callbacks;

import org.lwjgl.glfw.GLFWCursorEnterCallback;

/**
 * @author Frederik Dahl
 * 29/10/2021
 */


public class MouseEnterCallback extends GLFWCursorEnterCallback {
    
    
    private boolean inWindow;
    
    @Override
    public void invoke(long window, boolean entered) {
        inWindow = entered;
    }
    
    public boolean isInWindow() {
        return inWindow;
    }
}
