package io.github.heathensoft.core.window.callbacks;

import io.github.heathensoft.storage.primitive.IntQueue;
import io.github.heathensoft.storage.primitive.iterators.IntReader;
import org.lwjgl.glfw.GLFWCharCallback;

/**
 * Only ASCII valid characters are queued.
 *
 * @author Frederik Dahl
 * 21/10/2021
 */


public class CharPressCallback extends GLFWCharCallback {
    
    private final IntQueue queue = new IntQueue();
    private boolean ignore = true;
    
    @Override
    public void invoke(long window, int codepoint) {
        if (ignore || ((codepoint & 0x7F) != codepoint)) return;
        synchronized (this) {
            if (queue.size() == 16)
                queue.dequeue();
            queue.enqueue(codepoint);
        }
    }
    
    public synchronized void collect(IntReader collector) {
        queue.dequeueAll(collector);
    }
    
    public synchronized void ignore(boolean ignore) {
        this.ignore = ignore;
    }
    
    public synchronized void clear() {
        queue.clear();
    }
    
}
