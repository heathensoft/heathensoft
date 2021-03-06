package io.github.heathensoft.graphics.utility;

import io.github.heathensoft.common.Disposable;
import io.github.heathensoft.graphics.BufferObject;
import io.github.heathensoft.graphics.VAO;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 * @author Frederik Dahl
 * 21/04/2022
 */


public class ScreenQuad implements Disposable {
    
    private final VAO vao;
    private final BufferObject indexBuffer;
    private final BufferObject vertexBuffer;
    
    public ScreenQuad() {
    
        vao = new VAO();
        indexBuffer = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        vertexBuffer = new BufferObject(GL_ARRAY_BUFFER,GL_STATIC_DRAW);
        
        float[] vertices = {
                // position     // texCoord
                 1.0f,-1.0f,     1.0f, 0.0f, // Bottom right 0
                -1.0f, 1.0f,     0.0f, 1.0f, // Top left     1
                 1.0f, 1.0f ,    1.0f, 1.0f, // Top right    2
                -1.0f,-1.0f,     0.0f, 0.0f, // Bottom left  3
        };
        
        short[] indices = {
                2, 1, 0, // Top right triangle
                0, 1, 3  // bottom left triangle
        };
        vao.bind();
        indexBuffer.bind();
        indexBuffer.bufferData(indices);
        vertexBuffer.bind();
        vertexBuffer.bufferData(vertices);
        int posPointer = 0;
        int texPointer = 2 * Float.BYTES;
        int stride = 4 * Float.BYTES;
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, posPointer);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, texPointer);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    public void render() {
        vao.bind();
        glDrawElements(GL_TRIANGLES,6,GL_UNSIGNED_SHORT,0);
    }
    
    
    
    @Override
    public void dispose() {
        VAO.unbind();
        Disposable.dispose(indexBuffer);
        Disposable.dispose(vertexBuffer);
        Disposable.dispose(vao);
    }
}
