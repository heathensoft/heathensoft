package io.github.heathensoft.core.window.processors;

import io.github.heathensoft.common.Utils;
import io.github.heathensoft.core.window.CoreViewport;
import io.github.heathensoft.core.window.Window;
import io.github.heathensoft.core.window.callbacks.MouseEnterCallback;
import io.github.heathensoft.core.window.callbacks.MouseHoverCallback;
import io.github.heathensoft.core.window.callbacks.MousePressCallback;
import io.github.heathensoft.core.window.callbacks.MouseScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Frederik Dahl
 * 16/06/2022
 */


public class Mouse {
    
    public static final int NUM_BUTTONS = 3;
    public static final int LEFT  = GLFW_MOUSE_BUTTON_LEFT;
    public static final int RIGHT = GLFW_MOUSE_BUTTON_RIGHT;
    public static final int WHEEL = GLFW_MOUSE_BUTTON_MIDDLE;
    private static final float DRAG_SENSITIVITY = 5.0f;
    private final float[] timer = new float[NUM_BUTTONS];
    private final boolean[] current = new boolean[NUM_BUTTONS];
    private final boolean[] previous = new boolean[NUM_BUTTONS];
    private final boolean[] dragging = new boolean[NUM_BUTTONS];
    private final Position[] dragOrigin = new Position[NUM_BUTTONS];
    private final MouseEnterCallback enterCallback; // not really any use for this
    private final MouseHoverCallback hoverCallback;
    private final MousePressCallback pressCallback;
    private final MouseScrollCallback scrollCallback;
    private final Position cViewport = new Position();
    private final Position pViewport = new Position();
    private final Position cScreen = new Position();
    private final Position pScreen = new Position();
    private final Position ndc = new Position();
    private final Position tmp1 = new Position();
    private final Position tmp2 = new Position();
    private final Position dt = new Position();
    private final Window window;
    
    public Mouse(Window window,
                 MouseEnterCallback enterCallback,
                 MouseHoverCallback hoverCallback,
                 MousePressCallback pressCallback,
                 MouseScrollCallback scrollCallback) {
        this.enterCallback = enterCallback;
        this.hoverCallback = hoverCallback;
        this.pressCallback = pressCallback;
        this.scrollCallback = scrollCallback;
        this.window = window;
        for (int b = 0; b < NUM_BUTTONS; b++) {
            dragOrigin[b] = new Position();
        } cScreen.set(pScreen.set(
                hoverCallback.x(),
                window.windowHeight() - hoverCallback.y()));
        CoreViewport viewport = window.viewport();
        final double x = (pScreen.x - viewport.x()) * viewport.widthINV();
        final double y = (pScreen.y - viewport.y()) * viewport.heightINV();
        pViewport.set( Math.min(1,Math.max(x,0)), Math.min(1,Math.max(y,0)));
        //pViewport.y /= viewport.aspectRatio();
        cViewport.set(pViewport);
        ndc.set(2 * cViewport.x - 1, 2 * cViewport.y - 1);
    }
    
    public void collect(float delta) {
        pScreen.set(cScreen);
        cScreen.set(
                hoverCallback.x(),
                window.windowHeight() - hoverCallback.y());
        cScreen.x = Math.min(window.windowWidth(),Math.max(cScreen.x,0));
        cScreen.y = Math.min(window.windowHeight(),Math.max(cScreen.y,0));
        CoreViewport viewport = window.viewport();
        
        final double x = (cScreen.x - viewport.x()) * viewport.widthINV();
        final double y = (cScreen.y - viewport.y()) * viewport.heightINV();
        pViewport.set(cViewport);
        cViewport.x = Math.min(1,Math.max(x,0));
        cViewport.y = Math.min(1,Math.max(y,0));
        //cViewport.y /= viewport.aspectRatio(); // todo: remember
        ndc.x = 2 * cViewport.x - 1;
        ndc.y = 2 * cViewport.y - 1;
        
        if (!pScreen.equals(cScreen,0.001d)) {
            tmp1.set(cViewport);
            tmp1.y /= viewport.aspectRatio();
            tmp2.set(pViewport);
            tmp2.y /= viewport.aspectRatio();
            dt.set(tmp1.sub(tmp2)); // todo: I think this is right now
            listener.hover(
                    (float) cViewport.x,
                    (float) cViewport.y,
                    (float) dt.x,
                    (float) dt.y,
                    (float) ndc.x,
                    (float) ndc.y);
        }else dt.zero();
    
        listener.position(
                (float) cViewport.x,
                (float) cViewport.y,
                (float) ndc.x,
                (float) ndc.y);
        
        for (int b = 0; b < NUM_BUTTONS; b++) {
            previous[b] = current[b];
            current[b] = pressCallback.isPressed(b);
            if (current[b]) {
                timer[b] += delta * DRAG_SENSITIVITY;
                if (!previous[b]) {
                    listener.click(b,
                            (float) cViewport.x,
                            (float) cViewport.y,
                            (float) ndc.x,
                            (float) ndc.y);
                    dragOrigin[b].set(cViewport);
                } else if (timer[b] > 1) {
                    if (!dragging[b]) {
                        dragging[b] = true;
                        listener.dragStart(b,
                                (float) cViewport.x,
                                (float) cViewport.y,
                                (float) ndc.x,
                                (float) ndc.y);
                    } else { tmp1.set(cViewport).sub(dragOrigin[b]);
                        listener.dragging(b,
                                (float) tmp1.x,
                                (float) tmp1.y,
                                (float) dt.x,
                                (float) dt.y);
                    }
                }
            } else if (dragging[b]) {
                dragging[b] = false;
                listener.dragRelease(b,
                        (float) cViewport.x,
                        (float) cViewport.y,
                        (float) ndc.x,
                        (float) ndc.y);
            }
        }
        int scroll = scrollCallback.collect();
        if (scroll != 0)
            listener.scroll(scroll,
                    (float) cViewport.x,
                    (float) cViewport.y,
                    (float) ndc.x,
                    (float) ndc.y);
    }
    
    public boolean isDragging(int button) {
        if (button >= NUM_BUTTONS || button < 0) return false;
        return dragging[button];
    }
    
    public boolean isPressed(int button) {
        if (button >= NUM_BUTTONS || button < 0) return false;
        return current[button];
    }
    
    public boolean justPressed(int button) {
        if (button >= NUM_BUTTONS || button < 0) return false;
        return current[button] && !previous[button];
    }
    
    public boolean justReleased(int button) {
        if (button >= NUM_BUTTONS || button < 0) return false;
        return !current[button] && previous[button];
    }
    
    public double screenX() {
        return cScreen.x;
    }
    
    public double screenY() {
        return cScreen.y;
    }
    
    public double viewportX() {
        return cViewport.x;
    }
    
    public double viewportY() {
        return cViewport.y;
    }
    
    public double viewportNDCx() {
        return ndc.x;
    }
    
    public double viewportNDCy() {
        return ndc.y;
    }
    
    public double screenNDCx() {
        return 2 * cScreen.x / window.windowWidth() - 1;
    }
    
    public double screenNDCy() {
        return 2 * cScreen.y / window.windowHeight() - 1;
    }
    
    public MouseListener listener() {
        return listener;
    }
    
    public void setListener(MouseListener listener) {
        if (listener == null) {
            this.listener.onDeactiveMouseListener();
            this.listener = DEFAULT_LISTENER;
        }
        else {
            if (this.listener != listener) {
                this.listener.onDeactiveMouseListener();
                this.listener = listener;
                this.listener.onActiveMouseListener();
            }
        }
        for (int b = 0; b < NUM_BUTTONS; b++) {
            dragOrigin[b].set(cViewport);
        }
    }
    private MouseListener listener = DEFAULT_LISTENER;
    
    private final static MouseListener DEFAULT_LISTENER = new MouseListener() {
    
    
        @Override
        public void position(float viewportX, float viewportY, float ndcX, float ndcY) {
        
        }
    
        @Override
        public void hover(float viewportX, float viewportY, float deltaX, float deltaY, float ndcX, float ndcY) {
        
        }
    
        @Override
        public void click(int button, float viewportX, float viewportY, float ndcX, float ndcY) {
        
        }
    
        @Override
        public void scroll(int value, float viewportX, float viewportY, float ndcX, float ndcY) {
        
        }
    
        @Override
        public void dragging(int button, float dragVectorX, float dragVectorY, float deltaX, float deltaY) {
        
        }
    
        @Override
        public void dragStart(int button, float viewportX, float viewportY, float ndcX, float ndcY) {
        
        }
    
        @Override
        public void dragRelease(int button, float viewportX, float viewportY, float ndcX, float ndcY) {
        
        }
    
        @Override
        public void onActiveMouseListener() {
        
        }
    
        @Override
        public void onDeactiveMouseListener() {
        
        }
    };
    
    private final static class Position {
        
        double x, y;
        
        public Position(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public Position(Position p) {
            this.x = p.x;
            this.y = p.y;
        }
    
        public Position() {
            this.x = 0d;
            this.y = 0d;
        }
        
        public Position set(Position p) {
            this.x = p.x;
            this.y = p.y;
            return this;
        }
        
        public Position set(double x, double y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        public Position sub(Position p) {
            this.x = x - p.x;
            this.y = y - p.y;
            return this;
        }
        
        public Position zero() {
            this.x = 0;
            this.y = 0;
            return this;
        }
    
        public boolean equals(Position p, double delta) {
            if (this == p) return true;
            if (p == null) return false;
            if (!Utils.equals(x, p.x, delta)) return false;
            return Utils.equals(y, p.y, delta);
        }
    
        @Override
        public String toString() {
            return "Position{" +
                           "x=" + x +
                           ", y=" + y +
                           '}';
        }
    }
    
}
