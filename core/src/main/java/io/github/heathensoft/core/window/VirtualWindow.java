package io.github.heathensoft.core.window;

import io.github.heathensoft.core.Application;
import io.github.heathensoft.core.window.processors.Keyboard;
import io.github.heathensoft.core.window.processors.Mouse;

/**
 * @author Frederik Dahl
 * 17/06/2022
 */


public interface VirtualWindow {
    
    /**
     * OPENGL THREAD
     * Makes the OpenGL context of the Window current in the calling thread.
     * Creates a new GLCapabilities instance for that same context.
     */
    void initialize();
    
    /** OPENGL THREAD */
    void clearCapabilities();
    
    /** OPENGL THREAD */
    void freeCallbacks();
    
    /** MAIN THREAD */
    void terminate(); // free errorCallback here
    
    void fullscreenMonitor(int resWidth, int resHeight);
    
    void fullscreenBorderBox();
    
    void windowed();
    
    /** MAIN THREAD */
    void waitForEvents(float seconds);
    
    /** MAIN THREAD */
    void processRequests();
    
    void updateViewport(Application app);
    
    void swapBuffers();
    
    void signalToClose();
    
    void show();
    
    void hide();
    
    void focus();
    
    void maximize();
    
    void minimize();
    
    void restore();
    
    void setTitle(String title);
    
    void enableCursor(boolean enable);
    
    void centerCursor();
    
    void enableVsync(boolean enable);
    
    
    
    
    long windowHandle();
    
    long monitorHandle();
    
    boolean shouldClose();
    
    boolean cursorEnabled();
    
    boolean vsyncEnabled();
    
    boolean isMinimized();
    
    int targetResolutionWidth();
    
    int targetResolutionHeight();
    
    int windowWidth();
    
    int windowHeight();
    
    int windowPositionX();
    
    int windowPositionY();
    
    Viewport viewport();
    
    WinConfig config();
    
    Keyboard keyboard();
    
    Mouse mouse();
    
}
