package io.github.heathensoft.graphics;

import io.github.heathensoft.common.Disposable;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 04/04/2022
 */


public class VAO implements Disposable {
    
    
    private static int boundVAO;
    
    private final int id;
    
    public VAO() {
        id = glGenVertexArrays();
    }
    
    public void bind() {
        if (id == boundVAO) return;;
        glBindVertexArray(boundVAO = id);
    }
    
    public static void unbind() {
        if (0 == boundVAO) return;
        glBindVertexArray(boundVAO = 0);
    }
    
    public void delete() {
        glDeleteVertexArrays(id);
    }
    
    @Override
    public void dispose() {
        delete();
    }
}
