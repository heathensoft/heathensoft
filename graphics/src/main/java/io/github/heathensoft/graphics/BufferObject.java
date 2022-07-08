package io.github.heathensoft.graphics;


import io.github.heathensoft.common.Disposable;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL30.glBindBufferRange;
import static org.lwjgl.opengl.GL32.glFenceSync;

/**
 * @author Frederik Dahl
 * 26/10/2021
 */


public class BufferObject implements Disposable {
    
    protected final int target;
    protected final int usage;
    protected final int id;
    
    public BufferObject(int target, int usage) {
        this.id = glGenBuffers();
        this.target = target;
        this.usage = usage;
    }
    
    public void bufferData(byte[] data) {
        ByteBuffer buffer = null;
        try { buffer = MemoryUtil.memAlloc(data.length);
            buffer.put(data).flip();
            bufferData(buffer);
        }finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
    }
    
    public void bufferData(short[] data) {
        ShortBuffer buffer = null;
        try { buffer = MemoryUtil.memAllocShort(data.length);
            buffer.put(data).flip();
            bufferData(buffer);
        }finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
    }
    
    public void bufferData(int[] data) {
        IntBuffer buffer = null;
        try { buffer = MemoryUtil.memAllocInt(data.length);
            buffer.put(data).flip();
            bufferData(buffer);
        }finally {
            if (buffer != null){
                MemoryUtil.memFree(buffer);
            }
        }
    }
    
    public void bufferData(float[] data) {
        FloatBuffer buffer = null;
        try { buffer = MemoryUtil.memAllocFloat(data.length);
            buffer.put(data).flip();
            bufferData(buffer);
        }finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
    }
    
    public void bufferData(long bytes) {
        glBufferData(target,bytes,usage);
    }
    
    public void bufferData(FloatBuffer data) {
        glBufferData(target,data,usage);
    }
    
    public void bufferData(IntBuffer data) {
        glBufferData(target,data,usage);
    }
    
    public void bufferData(ShortBuffer data) {
        glBufferData(target,data,usage);
    }
    
    public void bufferData(ByteBuffer data) {
        glBufferData(target,data,usage);
    }
    
    public void bufferSubData(FloatBuffer data, int offset) {
        glBufferSubData(target,offset,data);
    }
    
    public void bufferSubData(IntBuffer data, int offset) {
        glBufferSubData(target,offset,data);
    }
    
    public void bufferSubData(ShortBuffer data, int offset) {
        glBufferSubData(target,offset,data);
    }
    
    public void bufferSubData(ByteBuffer data, int offset) {
        glBufferSubData(target,offset,data);
    }
    
    public void bindBufferBase(int bindingPoint) {
        glBindBufferBase(target, bindingPoint,id);
    }
    
    public void bindBufferRange(int bindingPoint, long offset, long size) {
        glBindBufferRange(target, bindingPoint,id,offset,size);
    }
    
    public void bind() {
        glBindBuffer(target,id);
    }
    
    public static void bindZERO(int target) {
        glBindBuffer(target,0);
    }
    
    @Override
    public void dispose() {
        glDeleteBuffers(id);
    }
}
