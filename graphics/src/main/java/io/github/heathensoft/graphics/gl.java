package io.github.heathensoft.graphics;

import io.github.heathensoft.core.Engine;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;
import static org.lwjgl.opengl.GL40.glBlendFunci;

/**
 * @author Frederik Dahl
 * 22/06/2022
 */


public class gl {
    
    public static final Blending blend = new Blending();
    public static final FrameBuffers framebuffer = new FrameBuffers();
    
    
    public static final class FrameBuffers {
        
        private final Color DEFAULT_COLOR = new Color(0,0,0,1);
        private int DEFAULT_CLEAR_MASK = GL_COLOR_BUFFER_BIT;
        private Framebuffer readBuffer = null;
        private Framebuffer drawBuffer = null;
        
        private FrameBuffers() {}
        
        /**
         * Set Default framebuffer clear-mask
         * (Framebuffer provided by GLFW)
         * @param mask the mask. i.e. color, depth, stencil.
         */
        public void defaultClearMask(int mask) {
            DEFAULT_CLEAR_MASK = mask;
        }
        
        /**
         * Clear current draw-buffer
         * with draw-buffer's clear - color/mask
         */
        public void clear() {
            int clearMask;
            Color clearColor;
            if (drawBuffer == null) {
                clearColor = DEFAULT_COLOR;
                clearMask = DEFAULT_CLEAR_MASK;
            } else {
                clearColor = drawBuffer.clearColor;
                clearMask = drawBuffer.clearMask;
            }
            float r = clearColor.r;
            float g = clearColor.g;
            float b = clearColor.b;
            float a = clearColor.a;
            glClearColor(r,g,b,a);
            glClear(clearMask);
        }
    
        /**
         * Clear single drawbuffer
         * @param index 1 - 15
         * @param value the color value
         */
        public void clearColorInt(int index, IntBuffer value) {
            glClearBufferiv(GL_COLOR,index,value);
        }
    
        /**
         * Clear single drawbuffer
         * @param index 1 - 15
         * @param value the color value
         */
        public void clearColorUInt(int index, IntBuffer value) {
            glClearBufferuiv(GL_COLOR,index,value);
        }
    
        /**
         * Clear single drawbuffer
         * @param index 1 - 15
         * @param value the color value
         */
        public void clearColorFloat(int index, FloatBuffer value) {
            glClearBufferfv(GL_COLOR,index,value);
        }
        
        public Framebuffer drawBuffer() {
            return drawBuffer;
        }
        
        public Framebuffer readBuffer() {
            return readBuffer;
        }
        
        public void bindDefault() {
            bind(null);
        }
        
        public void bind(Framebuffer buffer) {
            int bufferID = buffer == null ? 0 : buffer.id;
            int readBufferID = readBuffer == null ? 0 : readBuffer.id;
            int drawBufferID = drawBuffer == null ? 0 : drawBuffer.id;
            if (bufferID != readBufferID || bufferID != drawBufferID) {
                readBuffer = buffer; drawBuffer = buffer;
                glBindFramebuffer(GL_FRAMEBUFFER, bufferID);
            }
        }
        
        public void bindRead(Framebuffer buffer) {
            int bufferID = buffer == null ? 0 : buffer.id;
            int readBufferID = readBuffer == null ? 0 : readBuffer.id;
            if (bufferID != readBufferID) {
                readBuffer = buffer;
                glBindFramebuffer(GL_READ_FRAMEBUFFER, bufferID);
            }
        }
        
        public void bindDraw(Framebuffer buffer) {
            int bufferID = buffer == null ? 0 : buffer.id;
            int drawBufferID = drawBuffer == null ? 0 : drawBuffer.id;
            if (bufferID != drawBufferID) {
                drawBuffer = buffer;
                glBindFramebuffer(GL_DRAW_FRAMEBUFFER, bufferID);
            }
        }
        
        public void viewport() {
            if (drawBuffer == null) {
                Engine.get().window().viewport().reset();
            } else Engine.get().window().viewport().set(drawBuffer.width,drawBuffer.height);
        }
        
        public void readBuffer(int readBuffer) {
            glReadBuffer(readBuffer);
        }
        
        public void drawBuffer(int drawBuffer) {
            glDrawBuffer(drawBuffer);
        }
        
        public void clearColor() {
            Color clearColor = drawBuffer == null ? DEFAULT_COLOR : drawBuffer.clearColor;
            float r = clearColor.r;
            float g = clearColor.g;
            float b = clearColor.b;
            float a = clearColor.a;
            glClearColor(r,g,b,a);
        }
        
        public void drawBuffers(int ...drawBuffers) {
            if (drawBuffers != null) {
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer intBuff = stack.mallocInt(drawBuffers.length);
                    for (int buffer : drawBuffers) {
                        intBuff.put(buffer);
                    } glDrawBuffers(intBuff.flip());
                }
            }
        }
        
        public void checkStatus() throws Exception {
            int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            if (status != GL_FRAMEBUFFER_COMPLETE) {
                String message;
                switch (status) {
                    case GL_FRAMEBUFFER_UNDEFINED:
                        message = ": Framebuffer undefined"; break;
                    case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                        message = ": Incomplete attachment"; break;
                    case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                        message = ": Missing attachment"; break;
                    case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                        message = ": Incomplete draw buffer"; break;
                    case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                        message = ": Incomplete read buffer"; break;
                    case GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE:
                        message = ": Attachment object type"; break;
                    case GL_FRAMEBUFFER_UNSUPPORTED:
                        message = ": Framebuffer unsupported"; break;
                    case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
                        message = ": Incomplete multi-sample"; break;
                    case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS:
                        message = ": Incomplete layer targets"; break;
                    default: message = ": Unknown error";
                } throw new Exception("Incomplete framebuffer " + status + message);
            }
        }
        
        void onDispose(Framebuffer frameBuffer) {
            if (frameBuffer == readBuffer) bindRead(null);
            if (frameBuffer == drawBuffer) bindDraw(null);
        }
    }
    
    public static final class Blending {
        
        private boolean enabled;
        private boolean separate;
        
        private int src_rgb = GL_ONE;
        private int dst_rgb = GL_ZERO;
        private int src_alpha = GL_ONE;
        private int dst_alpha = GL_ZERO;
        private int blend_equ = GL_FUNC_ADD;
        
        
        private Blending() {}
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public boolean isSeparate() {
            return separate;
        }
        
        public void enable() {
            if (!enabled) {
                glEnable(GL_BLEND);
                enabled = true;
            }
        }
        
        public void disable() {
            if (enabled) {
                glDisable(GL_BLEND);
                enabled = false;
            }
        }
        
        public void function(int src, int dst) {
            if (src_rgb == src && dst_rgb == dst &! separate) return;
            glBlendFunc(src,dst);
            src_rgb = src_alpha = src;
            dst_rgb = dst_alpha = dst;
            separate = false;
        }
        
        public void equation(int equ) {
            if (blend_equ == equ) return;
            glBlendEquation(equ);
            blend_equ = equ;
        }
        
        public void funcSeparate(int src_rgb, int dst_rgb, int src_alpha, int dst_alpha) {
            if (this.src_rgb == src_rgb && this.dst_rgb == dst_rgb)
                if (this.src_alpha == src_alpha && this.dst_alpha == dst_alpha) return;
            glBlendFuncSeparate(src_rgb,dst_rgb,src_alpha,dst_alpha);
            this.src_rgb = src_rgb;
            this.dst_rgb = dst_rgb;
            this.src_alpha = src_alpha;
            this.dst_alpha = dst_alpha;
            separate = true;
        }
    }
}
