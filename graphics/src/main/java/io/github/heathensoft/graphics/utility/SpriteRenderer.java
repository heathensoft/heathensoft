package io.github.heathensoft.graphics.utility;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.graphics.*;
import io.github.heathensoft.graphics.texture.Texture;
import io.github.heathensoft.storage.generic.Container;
import io.github.heathensoft.storage.generic.ReaderIDX;
import org.joml.Matrix4f;

/**
 * Multiple renderers can share the same SpriteBatch
 *
 * @author Frederik Dahl
 * 02/07/2022
 */


public abstract class SpriteRenderer<F extends Framebuffer> {
    
    private static SpriteRenderer<?> activeRenderer;
    private static final String U_SAMPLER = "u_sampler";
    private static final String U_COMBINED = "u_combined";
    
    // Disposed Externally
    protected Container<Texture> textures;
    protected ShaderProgram shaderProgram;
    protected SpriteBatch spriteBatch;
    protected F frameBuffer;
    private boolean initialized;
    
    
    public void initialize(SpriteBatch spriteBatch, ShaderProgram shaderProgram, F frameBuffer, Texture ... textures) {
        if (!initialized) {
            this.shaderProgram = shaderProgram;
            this.spriteBatch = spriteBatch;
            this.frameBuffer = frameBuffer;
            this.textures = new Container<>(textures.length);
            for (Texture texture : textures) {
                this.textures.add(texture);
            } this.shaderProgram.use();
            createUniformsInternal();
            initialized = true;
        }
    }
    
    protected void createUniforms(ShaderProgram shaderProgram) { }
    
    protected abstract void prepareFramebuffer(F frameBuffer);
    
    protected void uploadUniforms(ShaderProgram shaderProgram) { }
    
    protected void onRendererEnd(F frameBuffer, ShaderProgram shaderProgram) { }
    
    public void begin(Matrix4f projView) {
        Assert.isTrue(initialized, "Renderer not initialized");
        Assert.isNull(activeRenderer, "Another renderer is currently active");
        activeRenderer = this;
        prepareFramebuffer(frameBuffer);
        shaderProgram.use();
        uploadUniformsInternal(projView);
        spriteBatch.setShader(shaderProgram);
        spriteBatch.begin();
    }
    
    /**
     * To draw TextureRegions direct, use batch().draw()
     * @param sprite the sprite
     */
    public void draw(Sprite sprite) {
        spriteBatch.draw(sprite);
    }
    
    public void end() {
        Assert.isTrue(activeRenderer == this,
                "Renderer is not active");
        activeRenderer = null;
        spriteBatch.end();
        onRendererEnd(frameBuffer,shaderProgram);
    }
    
    private void createUniformsInternal() {
        shaderProgram.createUniform(U_COMBINED);
        textures.read(samplerCreate);
        createUniforms(shaderProgram);
    }
    
    private void uploadUniformsInternal(Matrix4f projView) {
        shaderProgram.setUniform(U_COMBINED,projView);
        textures.read(samplerUpload);
        uploadUniforms(shaderProgram);
    }
    
    public SpriteBatch batch() {
        return spriteBatch;
    }
    
    public F frameBuffer() {
        return frameBuffer;
    }
    
    public boolean isActive() {
        return activeRenderer == this;
    }
    
    public void setSampler(Texture texture, int sampler) {
        Assert.notNull(texture);
        textures.set(sampler,texture);
    }
    
    private final ReaderIDX<Texture> samplerCreate = new ReaderIDX<>() {
        @Override
        public void next(Texture texture, int slot) {
            shaderProgram.createUniform(U_SAMPLER + slot);
        }
    };
    
    private final ReaderIDX<Texture> samplerUpload = new ReaderIDX<>() {
        @Override
        public void next(Texture texture, int slot) {
            shaderProgram.setUniform1i(U_SAMPLER + slot, slot);
            texture.bind(slot);
        }
    };
}
