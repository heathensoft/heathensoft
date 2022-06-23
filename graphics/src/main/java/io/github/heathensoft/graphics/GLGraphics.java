package io.github.heathensoft.graphics;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.storage.generic.Container;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;

/**
 * @author Frederik Dahl
 * 22/06/2022
 */


public class GLGraphics {
    
    public Blending blend;
    public Textures texture;
    public FrameBuffers framebuffer;
    
    
    private static GLGraphics instance;
    
    public static GLGraphics get() {
        return instance == null ? (instance = new GLGraphics()) : instance;
    }
    
    private GLGraphics() {
        blend = new Blending();
        texture = new Textures();
        framebuffer = new FrameBuffers();
    }
    
    public static final class FrameBuffers {
        
        private final Vector4f DEFAULT_COLOR = new Vector4f(0.0f,0.0f,0.0f,1.0f);
        private int DEFAULT_CLEAR_MASK = GL_COLOR_BUFFER_BIT;
        private FrameBuffer readBuffer = null;
        private FrameBuffer drawBuffer = null;
        
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
            Vector4f clearColor;
            if (drawBuffer == null) {
                clearColor = DEFAULT_COLOR;
                clearMask = DEFAULT_CLEAR_MASK;
            } else {
                clearColor = drawBuffer.clearColor;
                clearMask = drawBuffer.clearMask;
            }
            float r = clearColor.x;
            float g = clearColor.y;
            float b = clearColor.z;
            float a = clearColor.w;
            glClearColor(r,g,b,a);
            glClear(clearMask);
        }
        
        public FrameBuffer drawBuffer() {
            return drawBuffer;
        }
        
        public FrameBuffer readBuffer() {
            return readBuffer;
        }
        
        
        public void bind(FrameBuffer buffer) {
            int bufferID = buffer == null ? 0 : buffer.id;
            int readBufferID = readBuffer == null ? 0 : readBuffer.id;
            int drawBufferID = drawBuffer == null ? 0 : drawBuffer.id;
            if (bufferID != readBufferID || bufferID != drawBufferID) {
                readBuffer = buffer; drawBuffer = buffer;
                glBindFramebuffer(GL_FRAMEBUFFER, bufferID);
            }
        }
        
        public void bindRead(FrameBuffer buffer) {
            int bufferID = buffer == null ? 0 : buffer.id;
            int readBufferID = readBuffer == null ? 0 : readBuffer.id;
            if (bufferID != readBufferID) {
                readBuffer = buffer;
                glBindFramebuffer(GL_READ_FRAMEBUFFER, bufferID);
            }
        }
        
        public void bindDraw(FrameBuffer buffer) {
            int bufferID = buffer == null ? 0 : buffer.id;
            int drawBufferID = drawBuffer == null ? 0 : drawBuffer.id;
            if (bufferID != drawBufferID) {
                drawBuffer = buffer;
                glBindFramebuffer(GL_DRAW_FRAMEBUFFER, bufferID);
            }
        }
        
        public void readBuffer(int readBuffer) {
            glReadBuffer(readBuffer);
        }
        
        public void drawBuffer(int drawBuffer) {
            glDrawBuffer(drawBuffer);
        }
        
        public void clearColor() {
            Vector4f clearColor = drawBuffer == null ? DEFAULT_COLOR : drawBuffer.clearColor;
            float r = clearColor.x;
            float g = clearColor.y;
            float b = clearColor.z;
            float a = clearColor.w;
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
        
        void onDispose(FrameBuffer frameBuffer) {
            if (frameBuffer == readBuffer) bindRead(null);
            if (frameBuffer == drawBuffer) bindDraw(null);
        }
    }
    
    public static final class Textures {
    
        public static final int MAX_TEXTURE_SLOTS = 48;
        public static final int NOT_BOUND = -1;
        private static final Container<Texture> TEXTURE_SLOTS = new Container<>(MAX_TEXTURE_SLOTS);
        private int Active_Slot = 0;
        
        private Textures() {}
        
        
        public void unbind(Texture texture) {
            Assert.notNull(texture);
            int slot = texture.slot;
            if (slot == NOT_BOUND) return;
            if (slot != Active_Slot) {
                glActiveTexture(GL_TEXTURE0 + slot);
                Active_Slot = slot;
            } glBindTexture(texture.target,0);
            TEXTURE_SLOTS.set(slot,null);
            texture.slot = NOT_BOUND;
        }
        
        public void bind(Texture texture, int textureSlot) {
            if (textureSlot > NOT_BOUND && textureSlot < MAX_TEXTURE_SLOTS) {
                int slot = texture.slot;
                if (slot == textureSlot) {
                    if (slot != Active_Slot) {
                        glActiveTexture(GL_TEXTURE0 + slot);
                        Active_Slot = slot;
                    } return;
                } Texture bound = TEXTURE_SLOTS.get(textureSlot);
                if (bound != null) {
                    bound.slot = NOT_BOUND;
                } if (slot != NOT_BOUND) {
                    TEXTURE_SLOTS.set(slot,null);
                    if (slot != Active_Slot) {
                        glActiveTexture(GL_TEXTURE0 + slot);
                        Active_Slot = slot;
                    } glBindTexture(texture.target,0);
                } texture.slot = textureSlot;
                TEXTURE_SLOTS.set(slot,texture);
                if (slot != Active_Slot) {
                    glActiveTexture(GL_TEXTURE0 + slot);
                    Active_Slot = slot;
                } glBindTexture(texture.target,texture.id);
            }
        }
        
        public void bind(Texture texture) {
            if (texture == null) {
                Texture bound = TEXTURE_SLOTS.get(Active_Slot);
                if (bound != null) {
                    bound.slot = NOT_BOUND;
                } TEXTURE_SLOTS.set(Active_Slot,null);
            } else {
                int slot = texture.slot;
                if (slot == NOT_BOUND) {
                    Texture bound = TEXTURE_SLOTS.get(0);
                    if (bound != null) {
                        bound.slot = NOT_BOUND;
                    } texture.slot = 0;
                    TEXTURE_SLOTS.set(slot,texture);
                    if (slot != Active_Slot) {
                        glActiveTexture(GL_TEXTURE0 + slot);
                        Active_Slot = slot;
                    } glBindTexture(texture.target,texture.id);
                } else {
                    if (slot != Active_Slot) {
                        glActiveTexture(GL_TEXTURE0 + slot);
                        Active_Slot = slot;
                    }
                }
            }
        }
    
        public void generateMipMap(float lodBias, float min, float max) {
            Texture active = TEXTURE_SLOTS.get(Active_Slot);
            Assert.notNull(active);
            int target = active.target;
            glGenerateMipmap(target);
            glTexParameterf(target,GL_TEXTURE_MIN_LOD,min);
            glTexParameterf(target,GL_TEXTURE_MAX_LOD,max);
            glTexParameterf(target,GL_TEXTURE_LOD_BIAS,lodBias);
        }
    
        public void generateMipMap(float lodBias) {
            generateMipMap(lodBias, -1000, 1000);
        }
    
        public void generateMipMap() {
            generateMipMap(0.0f);
        }
    
        public void filter(int filter) {
            filter(filter,filter);
        }
    
        public void filter(int minFilter, int magFilter) {
            Texture active = TEXTURE_SLOTS.get(Active_Slot);
            Assert.notNull(active);
            int target = active.target;
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
        }
    
        public void wrapS(int wrapS) {
            Texture active = TEXTURE_SLOTS.get(Active_Slot);
            Assert.notNull(active);
            int target = active.target;
            glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
        }
    
        public void wrapT(int wrapT) {
            Texture active = TEXTURE_SLOTS.get(Active_Slot);
            Assert.notNull(active);
            int target = active.target;
            glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
        }
    
        public void wrapR(int wrapR) {
            Texture active = TEXTURE_SLOTS.get(Active_Slot);
            Assert.notNull(active);
            int target = active.target;
            glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR);
        }
    
        public void wrapST(int wrapS, int wrapT) {
            Texture active = TEXTURE_SLOTS.get(Active_Slot);
            Assert.notNull(active);
            int target = active.target;
            glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
            glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
        }
    
        public void wrapSTR(int wrapS, int wrapT, int wrapR) {
            Texture active = TEXTURE_SLOTS.get(Active_Slot);
            Assert.notNull(active);
            int target = active.target;
            glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
            glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
            glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR);
        }
    
        public void wrapST(int wrap) {
            wrapST(wrap,wrap);
        }
    
        public void wrapSTR(int wrap) {
            wrapSTR(wrap,wrap,wrap);
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
