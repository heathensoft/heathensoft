package io.github.heathensoft.graphics.utility;

import io.github.heathensoft.common.Disposable;
import io.github.heathensoft.graphics.ShaderProgram;
import io.github.heathensoft.graphics.gl;
import io.github.heathensoft.graphics.texture.Texture2D;

/**
 * @author Frederik Dahl
 * 02/07/2022
 */


public class ScreenRenderer implements Disposable {
    
    private final ShaderProgram shaderProgram;
    private final ScreenQuad screenQuad;
    
    
    public ScreenRenderer() throws Exception {
        screenQuad = new ScreenQuad();
        shaderProgram = new ShaderProgram(vs_src(),fs_src());
        shaderProgram.use();
        shaderProgram.createUniform("u_sampler");
    }
    
    public void render(Texture2D colorAttachemnet) {
        gl.framebuffer.bindDraw(null);
        gl.framebuffer.viewport();
        gl.framebuffer.clear();
        gl.blend.disable();
        shaderProgram.use();
        shaderProgram.setUniform1i("u_sampler",0);
        colorAttachemnet.bind(0);
        screenQuad.render();
    }
    
    @Override
    public void dispose() {
        Disposable.dispose(screenQuad,shaderProgram);
    }
    
    private String vs_src() {
        return "#version 440\n" +
                       "\n" +
                       "layout (location=0) in vec4 a_pos;\n" +
                       "layout (location=1) in vec2 a_uv;\n" +
                       "\n" +
                       "\n" +
                       "out VS_OUT {\n" +
                       "    vec2 uv;\n" +
                       "} fs;\n" +
                       "\n" +
                       "void main() {\n" +
                       "\n" +
                       "    gl_Position = a_pos;\n" +
                       "    fs.uv = a_uv;\n" +
                       "}";
    }
    
    private String fs_src() {
        return "#version 440\n" +
                       "\n" +
                       "out vec4 f_color;\n" +
                       "\n" +
                       "in VS_OUT {\n" +
                       "    vec2 uv;\n" +
                       "} vs;\n" +
                       "\n" +
                       "uniform sampler2D u_sampler;\n" +
                       "\n" +
                       "void main() {\n" +
                       "\n" +
                       "    f_color = texture(u_sampler,vs.uv);\n" +
                       "}\n";
    }
}
