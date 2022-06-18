package io.github.heathensoft.core.window.processors;

import io.github.heathensoft.core.window.callbacks.CharPressCallback;
import io.github.heathensoft.core.window.callbacks.KeyPressCallback;
import io.github.heathensoft.storage.primitive.iterators.IntReader;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;

/**
 *
 * https://learn.parallax.com/support/reference/ascii-table-0-127
 *
 * @author Frederik Dahl
 * 26/10/2021
 */


public class Keyboard {
    
    private boolean update;
    private final boolean[] keys;
    private final boolean[] pkeys;
    private final KeyPressCallback keyPressCallback;
    private final CharPressCallback charPressCallback;
    private KeyListener textProcessor;
    
    
    public Keyboard(KeyPressCallback keyPressCallback, CharPressCallback charPressCallback) {
        this.keys = new boolean[GLFW_KEY_LAST];
        this.pkeys = new boolean[GLFW_KEY_LAST];
        this.keyPressCallback = keyPressCallback;
        this.charPressCallback = charPressCallback;
        this.charPressCallback.clear();
    }
    
    private final IntReader keyCollector = new IntReader() {
        @Override
        public void next(int key) {
            if (key > 0) {
                keys[key] = true;
                if (textProcessor != null) {
                    if (key < 0x20 || key == 0x7F) {
                        textProcessor.npcPress(key);
                    }
                }
            }
            else {
                key = Math.abs(key);
                keys[key] = false;
                if (textProcessor != null) {
                    if (key < 0x20 || key == 0x7F) {
                        textProcessor.npcRelease(key);
                    }
                }
            }
        }
    };
    
    private final IntReader charCollector = new IntReader() {
        @Override
        public void next(int key) {
            if (textProcessor != null) {
                textProcessor.printable((byte)key);
            }
        }
    };
    
    public void collect() {
        if (update) {
            System.arraycopy(keys, 0, pkeys, 0, GLFW_KEY_LAST);
            update = false;
        } update = keyPressCallback.collect(keyCollector);
        charPressCallback.collect(charCollector);
    }
    
    public boolean pressed(int keycode) {
        if (keycode > GLFW_KEY_LAST) return false;
        return keys[keycode];
    }
    
    public boolean pressed(int keycode1, int keycode2) {
        return pressed(keycode1) && pressed(keycode2);
    }
    
    public boolean justPressed(int keycode) {
        if (keycode >= GLFW_KEY_LAST) return false;
        return keys[keycode] && !pkeys[keycode];
    }
    
    public boolean justPressed(int keycode, int mod) {
        return pressed(mod) && justPressed(keycode);
    }
    
    public boolean justReleased(int keycode) {
        return pkeys[keycode] && !keys[keycode];
    }
    
    public void setKeyListener(KeyListener listener) {  // this is correct
        if (textProcessor != null) {
            charPressCallback.ignore(true);
            charPressCallback.collect(charCollector);
        } textProcessor = listener;
        if (textProcessor != null)
            charPressCallback.ignore(false);
    }
}

