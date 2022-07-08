package io.github.heathensoft.core.window;


import io.github.heathensoft.common.Assert;
import io.github.heathensoft.common.OS;
import io.github.heathensoft.common.Print;
import io.github.heathensoft.core.Application;
import io.github.heathensoft.core.Engine;
import io.github.heathensoft.core.window.callbacks.*;
import io.github.heathensoft.core.window.processors.Keyboard;
import io.github.heathensoft.core.window.processors.Mouse;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Frederik Dahl
 * 15/06/2022
 */


public class Window implements VirtualWindow {
    
    private String title;
    private final CoreViewport viewport;
    private final Keyboard keyboard;
    private final Mouse mouse;
    private final WinRequestQueue requestQueue;
    private final CharPressCallback charPressCallback;
    private final FrameBufferCallback frameBufferCallback;
    private final KeyPressCallback keyPressCallback;
    private final MouseEnterCallback mouseEnterCallback;
    private final MouseHoverCallback mouseHoverCallback;
    private final MousePressCallback mousePressCallback;
    private final MouseScrollCallback mouseScrollCallback;
    private final WindowIconifyCallback windowIconifyCallback;
    private final WindowPositionCallback windowPositionCallback;
    private final WindowResizeCallback windowResizeCallback;
    private final GLFWErrorCallback errorCallback;
    
    private final long window;
    private final long monitor;
    private final int targetResWidth;
    private final int targetResHeight;
    private final boolean antialiasing;
    private boolean windowedMode;
    private boolean cursorEnabled;
    private boolean borderBoxed;
    private boolean vsync;
    

    public Window(WinConfig winConfig) throws Exception {
        Assert.isNull(Engine.get().window());
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit()) throw new Exception("Unable to initialize GLFW");
        
        cursorEnabled = true;
        vsync = winConfig.vsync();
        title = winConfig.title();
        borderBoxed = winConfig.borderBoxed();
        antialiasing = winConfig.antialiasing();
        windowedMode = winConfig.windowedMode();
        targetResWidth = winConfig.targetResolutionWidth();
        targetResHeight = winConfig.targetResolutionHeight();
        
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        //glfwWindowHint(GLFW_CLIENT_API,GLFW_OPENGL_API);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES,antialiasing  ? 4 : 0);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        if (OS.get() == OS.MAC) glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        
        monitor = glfwGetPrimaryMonitor();
        if (monitor == NULL) throw new Exception("Failed to locate monitor");
        GLFWVidMode vidMode = getVidMode();
        Print.out("monitor default resolution: " + vidMode.width() + ":" + vidMode.height());
        Print.out("monitor default refresh rate: " + vidMode.refreshRate() + " Hz");
        Print.out("creating the GLFW window...");
        Print.out("desired resolution: " + targetResWidth + " : " + targetResHeight);
        int resolutionWidth, resolutionHeight;
        
        if (windowedMode) {
            Print.out("creating windowed-mode window...");
            window = glfwCreateWindow(targetResWidth,targetResHeight,title,NULL,NULL);
        } else { Print.out("creating fullscreen window...");
            if (borderBoxed) {
                Print.out("using default monitor resolution (BorderBoxed)");
                resolutionWidth = vidMode.width();
                resolutionHeight = vidMode.height();
            } else {
                if (resolutionSupportedByMonitor(targetResWidth,targetResHeight)) {
                    Print.out("resolution supported by monitor");
                    resolutionWidth = targetResWidth;
                    resolutionHeight = targetResHeight;
                } else {
                    Print.out("resolution NOT supported by monitor");
                    Print.out("using default monitor resolution");
                    resolutionWidth = vidMode.width();
                    resolutionHeight = vidMode.height();
                }
            } Print.out("creating fullScreen window with resolution: " + resolutionWidth + ":" + resolutionHeight);
            window = glfwCreateWindow(resolutionWidth,resolutionHeight,title,monitor,NULL);
        } if ( window == NULL ) throw new Exception("Failed to create the GLFW engine.window");
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
    
            glfwGetFramebufferSize(window,w,h);
            viewport = new CoreViewport(w.get(0),h.get(0));
            Print.out("framebuffer size: " + w.get(0) + ":" + h.get(0));
            
            glfwGetWindowSize(window,w,h);
            windowResizeCallback = new WindowResizeCallback(w.get(0),h.get(0));
            Print.out("window size: " + w.get(0) + ":" + h.get(0));
            
            DoubleBuffer mX = stack.mallocDouble(1);
            DoubleBuffer mY = stack.mallocDouble(1);
            glfwGetCursorPos(window,mX,mY);
            mouseHoverCallback = new MouseHoverCallback(mX.get(0),mY.get(0),w.get(0),h.get(0));
        }
        requestQueue = new WinRequestQueue(Engine.get().mainThreadID());
        charPressCallback = new CharPressCallback();
        keyPressCallback = new KeyPressCallback();
        frameBufferCallback = new FrameBufferCallback();
        mouseEnterCallback = new MouseEnterCallback();
        mousePressCallback = new MousePressCallback();
        mouseScrollCallback = new MouseScrollCallback();
        windowIconifyCallback = new WindowIconifyCallback();
        windowPositionCallback = new WindowPositionCallback();
        glfwSetCharCallback(window,charPressCallback);
        glfwSetKeyCallback(window,keyPressCallback);
        glfwSetWindowSizeCallback(window,windowResizeCallback);
        glfwSetFramebufferSizeCallback(window,frameBufferCallback);
        glfwSetMouseButtonCallback(window,mousePressCallback);
        glfwSetCursorPosCallback(window,mouseHoverCallback);
        glfwSetCursorEnterCallback(window,mouseEnterCallback);
        glfwSetScrollCallback(window,mouseScrollCallback);
        glfwSetWindowIconifyCallback(window,windowIconifyCallback);
        glfwSetWindowPosCallback(window,windowPositionCallback);
        keyboard = new Keyboard(keyPressCallback,charPressCallback);
        mouse = new Mouse(this,
                mouseEnterCallback,
                mouseHoverCallback,
                mousePressCallback,
                mouseScrollCallback);
        if (windowedMode) centerWindow();
        glfwShowWindow(window);
    }
    
    
    
    @Override
    public void initialize() {
        Print.out("initializing...");
        glfwMakeContextCurrent(window);
        glfwSwapInterval(vsync ? 1 : 0);
        GL.createCapabilities();
    }
    
    @Override
    public void clearCapabilities() {
        long currentThread = Engine.get().currentThreadID();
        if (currentThread == Engine.get().contextThreadID()) {
            GL.setCapabilities(null);
        }
    }
    
    @Override
    public void freeCallbacks() {
        Callbacks.glfwFreeCallbacks(window);
    }
    
    @Override
    public void terminate() {
        long currentThread = Engine.get().currentThreadID();
        if (currentThread == Engine.get().mainThreadID()) {
            GLFWErrorCallback callback = glfwSetErrorCallback(null);
            if (callback != null) callback.free();
            glfwDestroyWindow(window);
            glfwTerminate();
        }
    }
    
    @Override
    public void fullscreenMonitor(int resWidth, int resHeight) {
        requestQueue.newRequest(() -> {
            if (resolutionSupportedByMonitor(resWidth,resHeight)) { borderBoxed = false;
                Print.out("resolution supported by monitor");
                if (windowedMode) { windowedMode = false;
                    glfwSetWindowMonitor(window,monitor,0,0, resWidth,resHeight,GLFW_DONT_CARE);
                } else glfwSetWindowSize(window,resWidth,resHeight);
            } else Print.out("resolution NOT supported by monitor");
        });
        
    }
    
    @Override
    public void fullscreenBorderBox() {
        requestQueue.newRequest(() -> { borderBoxed = true;
            GLFWVidMode vidMode = getVidMode();
            if (windowedMode) { windowedMode = false;
                glfwSetWindowMonitor(window,monitor,0,0, vidMode.width(),vidMode.height(),GLFW_DONT_CARE);
            } else glfwSetWindowSize(window,vidMode.width(),vidMode.height());
        });
    }
    
    @Override
    public void windowed() {
        requestQueue.newRequest(() -> {
            if (windowedMode) glfwSetWindowSize(window,targetResWidth,targetResHeight);
            else glfwSetWindowMonitor(window,NULL,0,0, targetResWidth,targetResHeight,GLFW_DONT_CARE);
            windowedMode = true;
            centerWindow();
        });
    }
    
    @Override
    public void waitForEvents(float seconds) {
        long currentThread = Engine.get().currentThreadID();
        if (currentThread == Engine.get().mainThreadID())
            glfwWaitEventsTimeout(seconds);
    }
    
    @Override
    public void processRequests() {
        requestQueue.handle();
    }
    
    @Override
    public void updateViewport(Application app) {
        viewport.update(app,frameBufferCallback);
    }
    
    @Override
    public void swapBuffers() {
        glfwSwapBuffers(window);
    }
    
    @Override
    public void signalToClose() {
        glfwSetWindowShouldClose(window,true);
    }
    
    @Override
    public void show() {
        requestQueue.newRequest(() -> glfwShowWindow(window));
    }
    
    @Override
    public void hide() {
        requestQueue.newRequest(() -> glfwHideWindow(window));
    }
    
    @Override
    public void focus() {
        requestQueue.newRequest(() -> glfwFocusWindow(window));
    }
    
    @Override
    public void maximize() {
        requestQueue.newRequest(() -> glfwMaximizeWindow(window));
    }
    
    @Override
    public void minimize() {
        requestQueue.newRequest(() -> glfwIconifyWindow(window));
    }
    
    @Override
    public void restore() {
        requestQueue.newRequest(() -> glfwRestoreWindow(window));
    }
    
    @Override
    public void setTitle(String title) {
        requestQueue.newRequest(() -> {
            glfwSetWindowTitle(window, title);
            this.title = title;
        });
    }
    
    @Override
    public void enableCursor(boolean enable) {
        requestQueue.newRequest(() -> {
            glfwSetInputMode(window, GLFW_CURSOR, enable ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
            cursorEnabled = enable;
        });
    }
    
    @Override
    public void centerCursor() {
        requestQueue.newRequest(() -> glfwSetCursorPos(window,windowWidth()/2d,windowHeight()/2d));
    }
    
    @Override
    public void enableVsync(boolean enable) {
        glfwSwapInterval(enable ? 1 : 0);
        vsync = enable;
    }
    
    @Override
    public long windowHandle() {
        return window;
    }
    
    @Override
    public long monitorHandle() {
        return monitor;
    }
    
    @Override
    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }
    
    @Override
    public boolean cursorEnabled() {
        return cursorEnabled;
    }
    
    @Override
    public boolean vsyncEnabled() {
        return vsync;
    }
    
    @Override
    public boolean isMinimized() {
        return windowIconifyCallback.isMinimized();
    }
    
    @Override
    public int targetResolutionWidth() {
        return targetResWidth;
    }
    
    @Override
    public int targetResolutionHeight() {
        return targetResHeight;
    }
    
    @Override
    public int windowWidth() {
        return windowResizeCallback.width();
    }
    
    @Override
    public int windowHeight() {
        return windowResizeCallback.height();
    }
    
    @Override
    public int windowPositionX() {
        return windowPositionCallback.y();
    }
    
    @Override
    public int windowPositionY() {
        return windowPositionCallback.x();
    }
    
    @Override
    public CoreViewport viewport() {
        return viewport;
    }
    
    @Override
    public WinConfig config() {
        return new WinConfig() {
            
            @Override
            public String title() {
                return title;
            }
    
            @Override
            public int targetResolutionWidth() {
                return targetResWidth;
            }
    
            @Override
            public int targetResolutionHeight() {
                return targetResHeight;
            }
    
            @Override
            public boolean borderBoxed() {
                return borderBoxed;
            }
    
            @Override
            public boolean windowedMode() {
                return windowedMode;
            }
    
            @Override
            public boolean antialiasing() {
                return antialiasing;
            }
    
            @Override
            public boolean vsync() {
                return vsync;
            }
        };
    }
    
    @Override
    public Keyboard keyboard() {
        return keyboard;
    }
    
    @Override
    public Mouse mouse() {
        return mouse;
    }
    
    private void centerWindow() {
        requestQueue.newRequest(() -> {
            if (windowedMode){
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer w = stack.mallocInt(1);
                    IntBuffer h = stack.mallocInt(1);
                    glfwGetWindowSize(window,w,h);
                    GLFWVidMode vidMode = getVidMode();
                    if (vidMode != null) {
                        int monitorResolutionWidth = vidMode.width();
                        int monitorResolutionHeight = vidMode.height();
                        glfwSetWindowPos(
                                window,
                                (monitorResolutionWidth - w.get(0)) / 2,
                                (monitorResolutionHeight - h.get(0)) / 2
                        );
                    }
                }
            }
        });
    }
    
    public boolean resolutionSupportedByMonitor(int resWidth, int resHeight) {
        ArrayList<VideoMode> videoModes = getVideoModes();
        for (VideoMode mode : videoModes) {
            int width = mode.getWidth();
            int height = mode.getHeight();
            System.out.println(width + "," + height);
            if (width == resWidth && height == resHeight)
                return true;
        }
        return false;
    }
    
    private ArrayList<VideoMode> getVideoModes() {
        ArrayList<VideoMode> videoModes = new ArrayList<>();
        GLFWVidMode.Buffer modes = glfwGetVideoModes(monitor);
        if (modes != null) {
            for (int i = 0; i < modes.capacity(); i++) {
                modes.position(i);
                int width = modes.width();
                int height = modes.height();
                int redBits = modes.redBits();
                int greenBits = modes.greenBits();
                int blueBits = modes.blueBits();
                int refreshRate = modes.refreshRate();
                videoModes.add(new VideoMode(width, height, redBits, greenBits, blueBits, refreshRate));
            }
        }
        return videoModes;
    }
    
    private GLFWVidMode getVidMode() {
        return glfwGetVideoMode(monitor);
    }
}
