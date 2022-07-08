package io.github.heathensoft.graphics.utility;

import io.github.heathensoft.common.Disposable;
import io.github.heathensoft.graphics.BufferObject;
import io.github.heathensoft.graphics.VAO;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 * @author Frederik Dahl
 * 14/05/2022
 *
 * glDisable(GL_POINT_SMOOTH);
 */


public class PointBatch implements Disposable {
    
    private int count;
    private final VAO vao;
    private final BufferObject vbo;
    private final FloatBuffer pixels;
    private final int size;
    
    
    public PointBatch(int batchSize) {
        size = batchSize;
        pixels = MemoryUtil.memAllocFloat(size * 2);
        vao = new VAO();
        vbo = new BufferObject(GL_ARRAY_BUFFER,GL_DYNAMIC_DRAW);
        vao.bind();
        vbo.bind();
        vbo.bufferData((long) size * 2 * Float.BYTES);
        glVertexAttribPointer(0,2,GL_FLOAT,false,2*Float.BYTES,0);
        glEnableVertexAttribArray(0);
    }
    
    public void draw(float x, float y) {
        if (count == size) flush();
        pixels.put(x).put(y);
        count++;
    }
    
    public void flush() {
        if (count > 0) {
            pixels.flip();
            vao.bind();
            vbo.bufferSubData(pixels,0);
            glDrawArrays(GL_POINTS,0,count);
            pixels.clear();
            count = 0;
        }
    }
    
    @Override
    public void dispose() {
        if (pixels != null)
            MemoryUtil.memFree(pixels);
        VAO.unbind();
        Disposable.dispose(vbo);
        Disposable.dispose(vao);
    }
}
