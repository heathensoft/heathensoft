package io.github.heathensoft.graphics;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.common.Disposable;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.GL_STREAM_READ;
import static org.lwjgl.opengl.GL21.GL_PIXEL_PACK_BUFFER;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL32.GL_SYNC_STATUS;

/**
 * @author Frederik Dahl
 * 06/07/2022
 */


public class PixelReaderR32UI implements Disposable {
    
    private final Framebuffer framebuffer;
    private final ByteBuffer pixelBuffer;
    private final IntBuffer syncBuffer;
    private final BufferObject pbo;
    private final int colorAttachemnt;
    private long syncObject;
    private int syncStatus;
    private int pixelID;
    
    
    public PixelReaderR32UI(Framebuffer framebuffer, int colorAttachemnt) {
        Assert.notNull(framebuffer,"Framebuffer cannot be null");
        this.colorAttachemnt = colorAttachemnt;
        this.framebuffer = framebuffer;
        this.syncStatus = GL_UNSIGNALED;
        this.syncObject = 0L;
        this.pixelID = 0;
        this.pixelBuffer = MemoryUtil.memAlloc(Integer.BYTES);
        this.syncBuffer = MemoryUtil.memAllocInt(1);
        int width = framebuffer.width();
        int height = framebuffer.height();
        this.pbo = new BufferObject(GL_PIXEL_PACK_BUFFER, GL_STREAM_READ);
        this.pbo.bind();
        this.pbo.bufferData((long) width * height * Integer.BYTES);
        BufferObject.bindZERO(GL_PIXEL_PACK_BUFFER);
    }
    
    public void updatePBO(float mouseViewportX, float mouseViewportY) {
        int x = (int)(mouseViewportX * framebuffer.width());
        int y = (int)(mouseViewportY * framebuffer.height());
        if (syncStatus == GL_SIGNALED) {
            syncStatus = GL_UNSIGNALED;
            glDeleteSync(syncObject);
            syncObject = 0L;
            pbo.bind();
            ByteBuffer pixel = glMapBufferRange(GL_PIXEL_PACK_BUFFER,0,Integer.BYTES,GL_MAP_READ_BIT,pixelBuffer);
            if (pixel != null) {
                pixelID = (pixel.get(0)) | (pixel.get(1) << 8) | (pixel.get(2) << 16) | (pixel.get(3) << 24);
                glUnmapBuffer(GL_PIXEL_PACK_BUFFER);
            }
            gl.framebuffer.bindRead(framebuffer);
            gl.framebuffer.readBuffer(colorAttachemnt);
            glReadPixels(x, y, 1,1, GL_RED_INTEGER, GL_UNSIGNED_INT,0);
            syncObject = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        } else {
            if (syncObject == 0L) {
                gl.framebuffer.bindRead(framebuffer);
                gl.framebuffer.readBuffer(colorAttachemnt);
                pbo.bind();
                glReadPixels(x, y, 1,1, GL_RED_INTEGER, GL_UNSIGNED_INT,0);
                syncObject = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
            } else {
                glGetSynciv(syncObject,GL_SYNC_STATUS,null,syncBuffer);
                syncStatus = syncBuffer.get(0);
            }
        }
    }
    
    public int pixelID() {
        return pixelID;
    }
    
    @Override
    public void dispose() {
        if (syncBuffer != null) MemoryUtil.memFree(syncBuffer);
        if (pixelBuffer != null) MemoryUtil.memFree(pixelBuffer);
        Disposable.dispose(pbo);
    }
}
