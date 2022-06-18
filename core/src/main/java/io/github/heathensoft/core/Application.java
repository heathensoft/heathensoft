package io.github.heathensoft.core;

/**
 * @author Frederik Dahl
 * 15/06/2022
 */


public interface Application {
    
    
    /**
     * On starting the application. Initialize scenes, renderers and load assets.
     * @throws Exception catching initialization exceptions, for safe termination.
     */
    void onStart() throws Exception;
    
    /**
     * The input method is used for collecting and querying input from user.
     * Called right before update(). Will not update if window is minimized.
     * @param delta = 1 / UPS (updates per second). It is a fixed interval.
     *              Set in the Engine. This is useful for stability. Not to be confused
     *              with the render delta.
     */
    void input(float delta);
    
    /**
     * The update method is where the application logic is performed.
     * Will keep updating even if window is minimized.
     * @param delta = 1 / UPS (updates per second). It is a fixed interval.
     *              Set in the Engine. This is useful for stability. Not to be confused
     *              with the render delta.
     */
    void update(float delta);
    
    /**
     * The rendering is happening separately from than that of
     * the input and update methods. (Look up "fixed time intervals")
     * Will not update if window is minimized.
     * @param alpha alpha = accumulator / delta, where the accumulator
     *              is a value from 0 to delta. Think of alpha as how far
     *              you are into another update. This can be used to project
     *              positions or animation frames by interpolating:
     *              state = current * alpha + previous * ( 1.0 - alpha );
     *              Where current and previous states are updated
     *              in the application update() method.
     * @param delta the actual frame time. Time in seconds between frames.
     *              This is useful for real-time shader calculations.
     *              Not to be confused with the fixed update delta.
     */
    void render(float alpha, float delta);
    
    void onResize(int width, int height);
    
    /**
     * When the window is signalled to close, after finishing the
     * current run-cycle, this method is called. Happening
     * before the window is terminated by the main-thread.
     * Use this to stop threads and explicitly free up memory
     */
    void onExit();
    
    
}
