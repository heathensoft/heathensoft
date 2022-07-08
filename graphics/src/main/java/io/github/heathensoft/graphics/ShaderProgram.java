package io.github.heathensoft.graphics;

import io.github.heathensoft.common.Disposable;
import io.github.heathensoft.common.Print;
import org.joml.*;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.glUniform1uiv;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

/**
 * @author Frederik Dahl
 * 17/10/2021
 */


public class ShaderProgram implements Disposable {
    
    private final int programID;
    private static int currentID = GL_NONE;
    private final Map<String,Integer> uniforms;
    private final Map<String,Integer> blockIndices;
    
    
    public ShaderProgram(String vsSource, String fsSource) throws Exception {
        this();
        attach(vsSource, GL20C.GL_VERTEX_SHADER);
        attach(fsSource, GL20C.GL_FRAGMENT_SHADER);
        compile();
        link();
    }
    
    public ShaderProgram() throws Exception {
        programID = glCreateProgram();
        if (programID == GL_FALSE)
            throw new Exception("Could not create program");
        uniforms = new HashMap<>();
        blockIndices = new HashMap<>();
    }
    
    public void attach(String source, int type) throws Exception {
        int handle = glCreateShader(type);
        glShaderSource(handle,source);
        glAttachShader(programID, handle);
    }
    
    public void compile() throws Exception {
        final int[] count = {0};
        final int[] shaders = new int[16];
        glGetAttachedShaders(programID,count,shaders);
        try {
            for (int i = 0; i < count[0]; i++) {
                final int shader = shaders[i];
                glCompileShader(shader);
                int status = glGetShaderi(shader, GL_COMPILE_STATUS);
                if (status == GL_FALSE) {
                    throw new Exception(glGetShaderInfoLog(shader));
                }
            }
        }catch (Exception e) {
            disposeShaders();
            throw new Exception(e);
        }
    }
    
    public void link() throws Exception {
        int status = glGetProgrami(programID, GL_LINK_STATUS);
        int attachedCount = glGetProgrami(programID,GL_ATTACHED_SHADERS);
        if (status == GL_TRUE || attachedCount == 0) {
            Print.out("Program already linked OR no shaders attached");
            return;
        } try { glLinkProgram(programID);
            status = glGetProgrami(programID, GL_LINK_STATUS);
            if (status == GL_FALSE)
                throw new Exception("Failed to link shaders: \n"
                + glGetProgramInfoLog(programID));
        } catch (Exception e) {
            dispose();
            throw new Exception(e.getMessage());
        } disposeShaders();
        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) {
            System.err.println(glGetProgramInfoLog(programID,512));
        }
    }
    
    private void disposeShaders() {
        int[] count = {0};
        int[] shaders = new int[16];
        glGetAttachedShaders(programID,count,shaders);
        for (int i = 0; i < count[0]; i++) {
            final int shader = shaders[i];
            glDetachShader(programID,shader);
            glDeleteShader(shader);
        }
    }
    
    public void createUniformBlockIndex(String name) {
        int index = glGetUniformBlockIndex(programID,name);
        if (index < 0) throw new RuntimeException("No such block:" + name);
        blockIndices.put(name,index);
    }
    
    public void bindBlock(String name, int bindingPoint) {
        Integer index = blockIndices.get(name);
        if (index == null) throw new RuntimeException("No such block:" + name);
        glUniformBlockBinding(programID,index,bindingPoint);
    }
    
    public void createUniform(String name) {
        int uniformLocation = glGetUniformLocation(programID, name);
        if (uniformLocation < 0)
            throw new RuntimeException("No such uniform:" + name);
        uniforms.put(name, uniformLocation);
    }
    
    /*
    
    public void createPointLightUniformArray(String name, int size) {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(name + "[" + i + "]");
        }
    }
    
    public void createSpotLightUniformArray(String name, int size) {
        for (int i = 0; i < size; i++) {
            createSpotLightUniform(name + "[" + i + "]");
        }
    }
    
    public void createSpotLightUniform(String name) {
        createUniform(name + ".coneDir");
        createUniform(name + ".cutoffI");
        createUniform(name + ".cutoffO");
        createPointLightUniform(name + ".pl");
    }
    
    public void createPointLightUniform(String name) {
        createUniform(name + ".a");
        createUniform(name + ".d");
        createUniform(name + ".s");
        createUniform(name + ".pos");
        createUniform(name + ".att.c");
        createUniform(name + ".att.l");
        createUniform(name + ".att.q");
    }
    
    public void createDirectionalLightUniform(String name) {
        createUniform(name + ".a");
        createUniform(name + ".d");
        createUniform(name + ".s");
        createUniform(name + ".dir");
    }
    
    public void createMaterialUniform(String name) {
        createUniform(name + ".a");
        createUniform(name + ".d");
        createUniform(name + ".s");
        createUniform(name + ".e");
        createUniform(name + ".shine");
    }
    
    public void setUniform(String name, PointLight light, int index) {
        setUniform(name + "[" + index + "]", light);
    }
    
    public void setUniform(String name, SpotLight light, int index) {
        setUniform(name + "[" + index + "]", light);
    }
    
    public void setUniform(String name, PointLight[] lights) {
        int n = lights != null ? lights.length : 0;
        for (int i = 0; i < n; i++) {
            setUniform(name, lights[i], i);
        }
    }
    
    public void setUniform(String name, SpotLight[] lights) {
        int n = lights != null ? lights.length : 0;
        for (int i = 0; i < n; i++) {
            setUniform(name, lights[i], i);
        }
    }
    
    public void setUniform(String name, PointLight light) {
        Attenuation att = light.attenuation();
        setUniform(name + ".a", light.ambient());
        setUniform(name + ".d", light.diffuse());
        setUniform(name + ".s", light.specular());
        setUniform(name + ".pos", light.position());
        setUniform1f(name + ".att.c", att.constant());
        setUniform1f(name + ".att.l", att.linear());
        setUniform1f(name + ".att.q", att.quadratic());
    }
    
    public void setUniform(String name, SpotLight light) {
        setUniform(name + ".coneDir",light.direction());
        setUniform1f(name + ".cutoffI",light.cutoffInner());
        setUniform1f(name + ".cutoffO",light.cutoffOuter());
        setUniform(name + ".pl",light.pointLight());
    }
    
    public void setUniform(String name, DirectionalLight light) {
        setUniform(name + ".a", light.ambient());
        setUniform(name + ".d", light.diffuse());
        setUniform(name + ".s", light.specular());
        setUniform(name + ".dir", light.direction());
    }
    
    public void setUniform(String name, Material material) {
        setUniform(name + ".a", material.ambient());
        setUniform(name + ".d", material.diffuse());
        setUniform(name + ".s", material.specular());
        setUniform(name + ".e", material.emissivity());
        setUniform1f(name + ".shine", material.shine());
    }
    
     */
    
    public void setUniform(String name, Vector2f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2);
            buffer.put(value.x).put(value.y);
            buffer.flip();
            glUniform2fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Vector2f[] values) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(2 * values.length);
            for (Vector2f value : values) {
                buffer.put(value.x).put(value.y);
            } buffer.flip();
            glUniform2fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Vector3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3);
            buffer.put(value.x).put(value.y).put(value.z);
            buffer.flip();
            glUniform3fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Vector3f[] values) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(3 * values.length);
            for (Vector3f value : values) {
                buffer.put(value.x).put(value.y).put(value.z);
            } buffer.flip();
            glUniform3fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Vector4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4);
            buffer.put(value.x).put(value.y).put(value.z).put(value.w);
            buffer.flip();
            glUniform4fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Vector4f[] values) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * values.length);
            for (Vector4f value : values) {
                buffer.put(value.x).put(value.y).put(value.z).put(value.w);
            } buffer.flip();
            glUniform4fv(uniforms.get(name), buffer);
        }
    }
    
    public void setUniform(String name, Matrix3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(9);
            value.get(buffer);
            glUniformMatrix3fv(uniforms.get(name), false, buffer);
        }
    }
    
    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(uniforms.get(name), false, buffer);
        }
    }
    
    public void setUniform1i(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }
    
    public void setUniform1iv(String name, int[] array) {
        glUniform1iv(uniforms.get(name),array);
    }
    
    public void setUniform2iv(String name, int[] array) {
        glUniform2iv(uniforms.get(name),array);
    }
    
    public void setUniform3iv(String name, int[] array) {
        glUniform3iv(uniforms.get(name),array);
    }
    
    public void setUniform4iv(String name, int[] array) {
        glUniform4iv(uniforms.get(name),array);
    }
    
    public void setUniform1iv(String name, IntBuffer buffer) {
        glUniform1iv(uniforms.get(name),buffer);
    }
    
    public void setUniform2iv(String name, IntBuffer buffer) {
        glUniform2iv(uniforms.get(name),buffer);
    }
    
    public void setUniform3iv(String name, IntBuffer buffer) {
        glUniform3iv(uniforms.get(name),buffer);
    }
    
    public void setUniform4iv(String name, IntBuffer buffer) {
        glUniform4iv(uniforms.get(name),buffer);
    }
    
    public void setUniform1ui(String name, int value) {
        glUniform1ui(uniforms.get(name), value);
    }
    
    public void setUniform1uiv(String name, int[] array) {
        glUniform1uiv(uniforms.get(name),array);
    }
    
    public void setUniform2uiv(String name, int[] array) {
        glUniform2uiv(uniforms.get(name),array);
    }
    
    public void setUniform3uiv(String name, int[] array) {
        glUniform3uiv(uniforms.get(name),array);
    }
    
    public void setUniform4uiv(String name, int[] array) {
        glUniform4uiv(uniforms.get(name),array);
    }
    
    public void setUniform1uiv(String name, IntBuffer buffer) {
        glUniform1uiv(uniforms.get(name),buffer);
    }
    
    public void setUniform2uiv(String name, IntBuffer buffer) {
        glUniform2uiv(uniforms.get(name),buffer);
    }
    
    public void setUniform3uiv(String name, IntBuffer buffer) {
        glUniform3uiv(uniforms.get(name),buffer);
    }
    
    public void setUniform4uiv(String name, IntBuffer buffer) {
        glUniform4uiv(uniforms.get(name),buffer);
    }
    
    public void setUniform1f(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }
    
    public void setUniform1fv(String name, float[] array) {
        glUniform1fv(uniforms.get(name),array);
    }
    
    public void setUniform2fv(String name, float[] array) {
        glUniform2fv(uniforms.get(name),array);
    }
    
    public void setUniform3fv(String name, float[] array) {
        glUniform3fv(uniforms.get(name),array);
    }
    
    public void setUniform4fv(String name, float[] array) {
        glUniform4fv(uniforms.get(name),array);
    }
    
    public void setUniform1fv(String name, FloatBuffer buffer) {
        glUniform1fv(uniforms.get(name),buffer);
    }
    
    public void setUniform2fv(String name, FloatBuffer buffer) {
        glUniform2fv(uniforms.get(name),buffer);
    }
    
    public void setUniform3fv(String name, FloatBuffer buffer) {
        glUniform3fv(uniforms.get(name),buffer);
    }
    
    public void setUniform4fv(String name, FloatBuffer buffer) {
        glUniform1fv(uniforms.get(name),buffer);
    }
    
    public void use() {
        if (programID != currentID) {
            glUseProgram(programID);
            currentID = programID;
        }
    }
    
    public int id() {
        return programID;
    }
    
    @Override
    public void dispose() {
        if (glGetProgrami(programID,GL_ATTACHED_SHADERS) > 0)
            disposeShaders();
        if (glGetProgrami(programID,GL_DELETE_STATUS) == GL_FALSE)
            glDeleteProgram(programID);
    }
    
    public static void useZERO() {
        glUseProgram(currentID = GL_NONE);
    }
    
    public static String insert(String insert, String replace, String glslString) {
        return glslString.replace(replace,insert);
    }
}
