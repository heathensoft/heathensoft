package io.github.heathensoft.core.window;

/**
 * Requests are the means for non-main-threads to call main-thread-only glfw functions.
 * Requests from main-thread are handled immediately, requests from other threads are queued.
 * Queued requests are handled by the main-thread.
 * Queued requests are queried every n - millis, determined by the engine.
 * Requests can contain other requests.
 *
 * @author Frederik Dahl
 * 17/06/2022
 */

@FunctionalInterface
interface WinRequest { // might extend this later
    
    void handle();
}
