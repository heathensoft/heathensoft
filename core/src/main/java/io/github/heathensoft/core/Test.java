package io.github.heathensoft.core;

import io.github.heathensoft.core.window.WinConfig;
import io.github.heathensoft.core.window.processors.Keyboard;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Frederik Dahl
 * 18/06/2022
 */


public class Test implements Application {
    
    @Override
    public void onStart() throws Exception {
    
    }
    
    @Override
    public void input(float delta) {
        Keyboard keyboard = Engine.get().keyboard();
        if (keyboard.justPressed(GLFW_KEY_ESCAPE)) {
            Engine.get().exit();
        }
        
    }
    
    @Override
    public void update(float delta) {
    
    }
    
    @Override
    public void render(float alpha, float delta) {
        glClearColor(1,1,1,1);
        glClear(GL_COLOR_BUFFER_BIT);
    }
    
    @Override
    public void onResize(int width, int height) {
        System.out.println("App resize: " + width + " " + height);
    }
    
    @Override
    public void onExit() {
    
    }
    
    public static void main(String[] args) {
        Engine.get().start(new Test(), new WinConfig() {
            
    
            @Override
            public boolean windowedMode() {
                return true;
            }
            
            
        });
    }
}
