package io.github.heathensoft.graphics.utility;


import io.github.heathensoft.common.Disposable;
import io.github.heathensoft.graphics.BufferObject;
import io.github.heathensoft.graphics.VAO;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 * @author Frederik Dahl
 * 14/05/2022
 */


public class QuadDraw implements Disposable {
    
    private final VAO vao;
    private final BufferObject vbo;
    private final BufferObject ebo;
    private final FloatBuffer vertices;
    
    public QuadDraw() {
        vao = new VAO();
        vbo = new BufferObject(GL_ARRAY_BUFFER,GL_DYNAMIC_DRAW);
        ebo = new BufferObject(GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
        vertices = MemoryUtil.memAllocFloat(8);
        vao.bind();
        ebo.bind();
        ebo.bufferData(new short[] {2, 1, 0, 0, 1, 3});
        vbo.bind();
        vbo.bufferData((long) 8 * Float.BYTES);
        glVertexAttribPointer(0,2, GL_FLOAT,false,2 * Float.BYTES,0);
        glEnableVertexAttribArray(0);
    }
    
    public void draw(Vector2f br, Vector2f tl, Vector2f tr, Vector2f bl) {
        draw(br.x,br.y,tl.x,tl.y,tr.x,tr.y,bl.x,bl.y);
    }
    
    public void draw(float brX, float brY, float tlX, float tlY, float trX, float trY, float blX, float blY) {
        vao.bind();
        vbo.bufferSubData(vertices.put(brX).put(brY).put(tlX).put(tlY).put(trX).put(trY).put(blX).put(blY).flip(),0);
        glDrawElements(GL_TRIANGLES,6,GL_UNSIGNED_SHORT,0);
        vertices.clear();
    }
    
    @Override
    public void dispose() {
        if (vertices != null)
            MemoryUtil.memFree(vertices);
        VAO.unbind();
        Disposable.dispose(vbo);
        Disposable.dispose(ebo);
        Disposable.dispose(vao);
    }
}
