package io.github.heathensoft.core.window;


import io.github.heathensoft.storage.generic.Queue;

/**
 * @author Frederik Dahl
 * 17/06/2022
 */


public class WinRequestQueue {
    
    private final Queue<WinRequest> requests;
    private final long glfwThread;
    
    WinRequestQueue(long glfwThread) {
        this.glfwThread = glfwThread;
        this.requests = new Queue<>(16);
    }
    
    protected synchronized void handle() {
        long current = Thread.currentThread().getId();
        if (current == glfwThread) {
            while (!requests.isEmpty()) {
                WinRequest request = requests.dequeue();
                request.handle();
            }
        }
    }
    
    protected synchronized void newRequest(WinRequest request) {
        if (request != null) {
            long current = Thread.currentThread().getId();
            if (current == glfwThread)
                request.handle();
            else requests.enqueue(request);
        }
    }
}
