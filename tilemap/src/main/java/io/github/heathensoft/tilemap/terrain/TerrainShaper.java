package io.github.heathensoft.tilemap.terrain;

import io.github.heathensoft.common.Disposable;
import io.github.heathensoft.core.Engine;
import io.github.heathensoft.graphics.Framebuffer;
import io.github.heathensoft.graphics.ShaderProgram;
import io.github.heathensoft.graphics.gl;
import io.github.heathensoft.graphics.resources.ResourceLoader;
import io.github.heathensoft.graphics.texture.Texture2D;
import io.github.heathensoft.graphics.utility.ScreenQuad;
import io.github.heathensoft.tilemap.MapSize;
import org.joml.Vector2f;

import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Frederik Dahl
 * 27/06/2022
 */


public class TerrainShaper implements Disposable {
    
    private final BlendMapRenderer blendMapRenderer;
    private final TerrainCanvas canvas;
    private final BlendmapFBO blendmapFBO;
    
    public TerrainShaper(MapSize mapSize) throws Exception {
        this(mapSize.value,mapSize.value);
    }
    
    public TerrainShaper(int terrainWidth, int terrainHeight) throws Exception {
        blendMapRenderer = new BlendMapRenderer(terrainWidth,terrainHeight);
        canvas = new TerrainCanvas(terrainWidth,terrainHeight);
        blendmapFBO = new BlendmapFBO(terrainWidth,terrainHeight);
    }
    
    public Texture2D blendMap() {
        return blendmapFBO.get();
    }
    
    public TerrainCanvas canvas() {
        return canvas;
    }
    
    public void setEditListener(TerrainEditCallback listener) {
        canvas.setCallback(listener);
    }
    
    public void render(ScreenQuad screenQuad) { // updates the blendmap
        if (canvas.isModified()) {
            borrowViewport();
            blendMapRenderer.render(canvas.texture(), blendmapFBO, screenQuad);
            returnViewport();
            canvas.setModified(false);
        }
    }
    
    @Override
    public void dispose() {
        Disposable.dispose(blendmapFBO,canvas, blendMapRenderer);
    }
    
    private void borrowViewport() {
        Engine.get().window().viewport().set(blendmapFBO.width(), blendmapFBO.height());
    }
    
    private void returnViewport() {
        Engine.get().window().viewport().reset();
    }
    
    
    private static final class BlendMapRenderer implements Disposable {
        
        
        private static final String VS_PATH = "terrain_edit_vert.glsl";
        private static final String FS_PATH = "terrain_edit_frag.glsl";
        private static final String COLOR_OUT_NAME = "f_color";
        private static final String UNIFORM_SAMPLER_2D = "u_sampler";
        private static final String UNIFORM_MIX_STATE = "u_mix";
        private static final String UNIFORM_TEX_SIZE_INV = "u_inc";
        
        private final ShaderProgram shaderProgram;
        
        BlendMapRenderer(int width, int height) throws Exception {
            ResourceLoader.ResourceUtility io = ResourceLoader.resource;
            shaderProgram = new ShaderProgram(io.toString(VS_PATH,this.getClass()),io.toString(FS_PATH,this.getClass()));
            shaderProgram.use();
            createUniforms(width, height);
        }
        
        void render(Texture2D canvas, BlendmapFBO blendmapFBO, ScreenQuad screenQuad) {
            shaderProgram.use();
            blendmapFBO.bindDraw();
            gl.framebuffer.clear();
            glBindFragDataLocation(shaderProgram.id(), blendmapFBO.layeredColorNumber(),COLOR_OUT_NAME);
            stackColors(screenQuad,canvas);
            glBindFragDataLocation(shaderProgram.id(), blendmapFBO.mixedColorNumber(),COLOR_OUT_NAME);
            mixColors(screenQuad, blendmapFBO.layeredColorTexture());
        }
        
        private void mixColors(ScreenQuad screenQuad, Texture2D samplerTexture) {
            shaderProgram.setUniform1i(UNIFORM_MIX_STATE,0);
            samplerTexture.bind(0);
            screenQuad.render();
        }
        
        private void stackColors(ScreenQuad screenQuad, Texture2D samplerTexture) {
            shaderProgram.setUniform1i(UNIFORM_MIX_STATE,1);
            samplerTexture.bind(0);
            screenQuad.render();
        }
        
        private void createUniforms(int width, int height) {
            Vector2f textureSizeINV = new Vector2f(1f/width,1f/height);
            shaderProgram.createUniform(UNIFORM_TEX_SIZE_INV);
            shaderProgram.createUniform(UNIFORM_SAMPLER_2D);
            shaderProgram.createUniform(UNIFORM_MIX_STATE);
            shaderProgram.setUniform(UNIFORM_TEX_SIZE_INV,textureSizeINV);
            shaderProgram.setUniform1i(UNIFORM_SAMPLER_2D,0);
        }
        
        @Override
        public void dispose() {
            Disposable.dispose(shaderProgram);
        }
    }
    
    private static final class BlendmapFBO extends Framebuffer {
        
        private final Texture2D mixed, layered;
        
        BlendmapFBO(int width, int height) throws Exception {
            super(width, height);
            bindDraw();
            layered = new Texture2D();
            mixed = new Texture2D();
            layered.bind();
            layered.nearest();
            layered.clampToEdge();
            layered.RGBA4((ShortBuffer) null,width,height);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, layered.target(), layered.id(), 0);
            mixed.bind();
            mixed.nearest();
            mixed.clampToEdge();
            mixed.RGBA4((ShortBuffer) null,width,height);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, mixed.target(), mixed.id(), 0);
            gl.framebuffer.readBuffer(GL_NONE);
            gl.framebuffer.drawBuffers(GL_COLOR_ATTACHMENT0,GL_COLOR_ATTACHMENT1);
            gl.framebuffer.checkStatus();
            gl.framebuffer.bindDefault();
        }
        
        int layeredColorNumber() {
            return GL_COLOR_ATTACHMENT0;
        }
        
        int mixedColorNumber() {
            return GL_COLOR_ATTACHMENT1;
        }
        
        Texture2D layeredColorTexture() {
            return layered;
        }
        
        Texture2D mixedColorTexture() {
            return mixed;
        }
        
        Texture2D get() {
            return mixed;
        }
        
        @Override
        protected void disposeInternal() {
            Disposable.dispose(layered,mixed);
        }
    }
}
