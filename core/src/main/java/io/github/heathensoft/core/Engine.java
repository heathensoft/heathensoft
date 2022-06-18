package io.github.heathensoft.core;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.common.Print;
import io.github.heathensoft.core.window.WinConfig;
import io.github.heathensoft.core.window.Window;
import io.github.heathensoft.core.window.processors.Keyboard;
import io.github.heathensoft.core.window.processors.Mouse;
import org.lwjgl.Version;

/**
 * @author Frederik Dahl
 * 15/06/2022
 */


public class Engine {
    
    private static Engine instance;
    private final Thread glContext;
    private final Time time;
    private final Object lock;
    private Application app;
    private Window window;
    private long contextThreadID = -1;
    private long mainThreadID = -1;
    private int targetUPS = 60;
    private int targetFPS = 60;
    private boolean capFPS = false;
    private boolean running = false;
    private boolean sleepOnSync = true;
    
    private Engine() {
        this.mainThreadID = currentThreadID();
        this.lock = new Object();
        this.time = new Time();
        this.glContext = new Thread(() -> {
            try { contextThreadID = Thread.currentThread().getId();
                Print.out("initializing window");
                window.initialize();
                Print.out("starting application");
                app.onStart();
                time.init();
                running = true;
                float alpha;
                float frameTime;
                float accumulator = 0f;
                float delta = 1f / targetUPS;
                Print.out("running...");
                while (running) {
                    frameTime = time.frameTime();
                    accumulator += frameTime;
                    while (accumulator >= delta) {
                        if (!window.isMinimized()) {
                            keyboard().collect();
                            // mouse collect
                            app.input(delta);
                        } app.update(delta);
                        time.incUpsCount();
                        accumulator -= delta;
                        synchronized (lock) {
                            if (running) { alpha = accumulator / delta;
                                if (!window.isMinimized()) {
                                    window.updateViewport(app);
                                    app.render(alpha,frameTime);
                                    window.swapBuffers();
                                }
                            }
                        }
                        time.incFpsCount();
                        time.update();
                        if (!window.vsyncEnabled()) {
                            if (capFPS) sync();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                synchronized (lock) {
                    if (running) {
                        exit();
                    }
                }
                Print.out("exiting application...");
                application().onExit();
                Print.out("clearing GL Capabilities");
                window.clearCapabilities();
                Print.out("freeing glfw callbacks");
                window.freeCallbacks();
            }
        },"GL_Context");
    }
    
    public static Engine get() {
        return instance == null ? (instance = new Engine()) : instance;
    }
    
    
    public void start(Application app, WinConfig winConfig) {
        if (get().currentThreadID() != get().mainThreadID())
            throw new IllegalStateException("Calling start on running engine");
        Assert.notNull(app,winConfig);
        String platform = System.getProperty("os.name") + ", " + System.getProperty("os.arch") + " Platform.";
        int numProcessors = Runtime.getRuntime().availableProcessors();
        int JREMemoryMb = (int)(Runtime.getRuntime().maxMemory() / 1000000L);
        String jre = System.getProperty("java.version");
        System.out.println("\nSYSTEM\n");
        System.out.println("---Running on: " + platform);
        System.out.println("---jre: " + jre);
        System.out.println("---Available processors: " + numProcessors);
        System.out.println("---Reserved memory: " + JREMemoryMb + " Mb");
        System.out.println("---LWJGL version: " + Version.getVersion() + "\n");
        try { this.app = app;
            Print.out("create new Window instance");
            window = new Window(winConfig);
            Print.out("starting GLContext (graphics thread)");
            glContext.start();
            while (!window.shouldClose()) {
                window.waitForEvents(0.05f);
                window.processRequests();
            } Print.out("stops polling input-events");
            synchronized (lock) { running = false; }
            Print.out("running = false");
            Print.out("hiding window");
            window.hide();
            Print.out("joining context...");
            glContext.join(0);
            Print.out("threads joined");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Print.out("terminating Window");
            if (window != null)
                window.terminate();
            Print.out("terminated");
        }
    }
    
    public void exit() {
        Window window = instance.window;
        Assert.notNull(window);
        Print.out("window signalled to close...");
        window.signalToClose();
    }
    
    public <T extends Application> T application(Class<T> clazz) {
        Assert.isClass(app,clazz);
        return clazz.cast(app);
    }
    
    public Application application() {
        return app;
    }
    
    public Window window() {
        return window;
    }
    
    public Keyboard keyboard() {
        return window.keyboard();
    }
    
    public Mouse mouse() {
        return window.mouse();
    }
    
    public Time time() {
        return time;
    }
    
    public long mainThreadID() {
        return mainThreadID;
    }
    
    public long contextThreadID() {
        return contextThreadID;
    }
    
    public synchronized long currentThreadID() {
        return Thread.currentThread().getId();
    }
    
    public void sleepOnSync(boolean enable) {
        sleepOnSync = enable;
    }
    
    public void capFPS(boolean enable) {
        capFPS = enable;
    }
    
    public void setTargetFPS(int fps) {
        targetFPS = Math.max(1,fps);
    }
    
    public void setTargetUPS(int ups) {
        targetUPS = Math.max(1,ups);
    }
    
    private void sync() {
        double lastFrame = time.lastFrame();
        double now = time.timeSeconds();
        float targetTime = 0.96f / targetFPS;
        while (now - lastFrame < targetTime) {
            if (sleepOnSync) { Thread.yield();
                try { Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } now = time.timeSeconds();
        }
    }
}
