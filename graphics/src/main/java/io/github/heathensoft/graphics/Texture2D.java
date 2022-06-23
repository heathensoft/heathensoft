package io.github.heathensoft.graphics;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.storage.primitive.ByteArray2D;
import io.github.heathensoft.storage.primitive.FloatArray2D;
import io.github.heathensoft.storage.primitive.IntArray2D;
import io.github.heathensoft.storage.primitive.ShortArray2D;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

/**
 * All initializing methods with a java.nio.Buffer argument, can be null to create space for a texture.
 * Specify the null argument according to the data primitive type. i.e. (ByteBuffer) null
 *
 *
 * @author Frederik Dahl
 * 22/06/2022
 */


public class Texture2D extends Texture {
    
    protected int width;
    protected int height;
    
    public Texture2D(int width, int height) {
        super(GL_TEXTURE_2D);
        this.width = width;
        this.height = height;
    }
    
    public void image(Image image) throws Exception {
        if (image == null) {
            throw new Exception("Image == null");
        } this.width = image.width();
        this.height = image.height();
        int i_format; // GPU side
        int format; // client memory
        int stride = 4;
        switch (image.channels()) {
            case 1:
                format = GL_RED;
                i_format = GL_R8;
                stride = 1;
                break;
            case 2:
                format = GL_RG;
                i_format = GL_RG8;
                stride = 2;
                break;
            case 3:
                i_format = format = GL_RGB;
                if ((width & 3) != 0) {
                    stride = 2 - (width & 1);
                }break;
            case 4:
                format = GL_RGBA;
                i_format = GL_RGBA8;
                break;
            default:
                throw new Exception("Unsupported format");
        } glPixelStorei(GL_UNPACK_ALIGNMENT,stride);
        glTexImage2D(target, 0, i_format, width, height,
                0, format, GL_UNSIGNED_BYTE, image.data());
    }
    
    
    // ---------------------------------R8 Unsigned Normalized
    
    
    
    public void R8(ByteArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        ByteBuffer buffer = MemoryUtil.memAlloc(data.size());
        byte[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(target, 0, GL_R8, width, height,
                0, GL_RED, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R8(byte[][] data) {
        R8(new ByteArray2D(data));
    }
    
    public void R8(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(target, 0, GL_R8, width, height,
                0, GL_RED, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R8(ByteBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(target, 0, GL_R8, width, height,
                0, GL_RED, GL_UNSIGNED_BYTE, data);
    }
    
    
    
    // ---------------------------------R8 Signed Normalized
    
    
    
    public void R8_SNORM(ByteArray2D byteArray) {
        Assert.notNull(byteArray);
        this.width = byteArray.cols();
        this.height = byteArray.rows();
        ByteBuffer buffer = MemoryUtil.memAlloc(byteArray.size());
        byte[][] array2D = byteArray.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(target, 0, GL_R8_SNORM, width, height,
                0, GL_RED, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R8_SNORM(byte[][] data) {
        R8_SNORM(new ByteArray2D(data));
    }
    
    public void R8_SNORM(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(target, 0, GL_R8_SNORM, width, height,
                0, GL_RED, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R8_SNORM(ByteBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        glTexImage2D(target, 0, GL_R8_SNORM, width, height,
                0, GL_RED, GL_BYTE, data);
    }
    
    
    
    // ---------------------------------R16 Unsigned Normalized
    
    
    
    public void R16(ShortArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.size());
        short[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_R16, width, height,
                0, GL_RED, GL_UNSIGNED_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R16(short[][] data) {
        R16(new ShortArray2D(data));
    }
    
    public void R16(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_R16, width, height,
                0, GL_RED, GL_UNSIGNED_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R16(ShortBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_R16, width, height,
                0, GL_RED, GL_UNSIGNED_SHORT, data);
    }
    
    
    
    // ---------------------------------R16 Signed Normalized
    
    
    
    
    public void R16_SNORM(ShortArray2D shortArray) {
        Assert.notNull(shortArray);
        this.width = shortArray.cols();
        this.height = shortArray.rows();
        ShortBuffer buffer = MemoryUtil.memAllocShort(shortArray.size());
        short[][] array2D = shortArray.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_R16_SNORM, width, height,
                0, GL_RED, GL_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R16_SNORM(short[][] data) {
        R16_SNORM(new ShortArray2D(data));
    }
    
    public void R16_SNORM(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_R16_SNORM, width, height,
                0, GL_RED, GL_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R16_SNORM(ShortBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_R16_SNORM, width, height,
                0, GL_RED, GL_SHORT, data);
    }
    
    
    
    // ---------------------------------R32F Float
    
    
    
    public void TextureR32F(FloatArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.size());
        float[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32F, width, height,
                0, GL_RED, GL_FLOAT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void TextureR32F(float[][] data) {
        TextureR32F(new FloatArray2D(data));
    }
    
    public void TextureR32F(float[] data, int width, int height) {
        this.width = width;
        this.height = height;
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32F, width, height,
                0, GL_RED, GL_FLOAT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R16(FloatBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32F, width, height,
                0, GL_RED, GL_FLOAT, data);
    }
    
    
    
    // ---------------------------------R32I Signed Integer
    
    
    
    public void R32I(IntArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        IntBuffer buffer = MemoryUtil.memAllocInt(data.size());
        int[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32I, width, height,
                0, GL_RED_INTEGER, GL_INT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R32I(int[][] data) {
        R32I(new IntArray2D(data));
    }
    
    public void R32I(int[] data, int width, int height) {
        this.width = width;
        this.height = height;
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32I, width, height,
                0, GL_RED_INTEGER, GL_INT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R32I(IntBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32I, width, height,
                0, GL_RED_INTEGER, GL_INT, data);
    }
    
    
    
    // ---------------------------------R32UI Unsigned Integer
    
    
    
    public void R32UI(IntArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        IntBuffer buffer = MemoryUtil.memAllocInt(data.size());
        int[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32I, width, height,
                0, GL_RED_INTEGER, GL_UNSIGNED_INT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R32UI(int[][] data) {
        R32UI(new IntArray2D(data));
    }
    
    public void R32UI(int[] data, int width, int height) {
        this.width = width;
        this.height = height;
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32I, width, height,
                0, GL_RED_INTEGER, GL_UNSIGNED_INT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void R32UI(IntBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_R32I, width, height,
                0, GL_RED_INTEGER, GL_UNSIGNED_INT, data);
    }
    
    
    
    // ---------------------------------RG8 Unsigned Normalized
    
    
    
    public void RG8(ShortArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.size());
        short[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8, width, height,
                0, GL_RG, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG8(short[][] data) {
        RG8(new ShortArray2D(data));
    }
    
    public void RG8(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8, width, height,
                0, GL_RG, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG8(ShortBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8, width, height,
                0, GL_RG, GL_UNSIGNED_BYTE, data);
    }
    
    public void RG8(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ByteBuffer buffer = MemoryUtil.memAlloc(length);
        for (int i = 0; i < length; i += 2) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8, width, height,
                0, GL_RG, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG8(ByteBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8, width, height,
                0, GL_RG, GL_UNSIGNED_BYTE, data);
    }
    
    
    
    // ---------------------------------RG8 Signed Normalized
    
    
    
    public void RG8_SNORM(ShortArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.size());
        short[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8_SNORM, width, height,
                0, GL_RED, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG8_SNORM(short[][] data) {
        RG8_SNORM(new ShortArray2D(data));
    }
    
    public void RG8_SNORM(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8_SNORM, width, height,
                0, GL_RED, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG8_SNORM(ShortBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8_SNORM, width, height,
                0, GL_RED, GL_BYTE, data);
    }
    
    public void RG8_SNORM(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ByteBuffer buffer = MemoryUtil.memAlloc(length);
        for (int i = 0; i < length; i += 2) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8_SNORM, width, height,
                0, GL_RED, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG8_SNORM(ByteBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RG8_SNORM, width, height,
                0, GL_RED, GL_BYTE, data);
    }
    
    
    
    // ---------------------------------RG16 Unsigned Normalized
    
    
    
    public void RG16(IntArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        IntBuffer buffer = MemoryUtil.memAllocInt(data.size());
        int[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16, width, height,
                0, GL_RG, GL_UNSIGNED_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG16(int[][] data) {
        RG16(new IntArray2D(data));
    }
    
    public void RG16(int[] data, int width, int height) {
        this.width = width;
        this.height = height;
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16, width, height,
                0, GL_RG, GL_UNSIGNED_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG16(IntBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16, width, height,
                0, GL_RG, GL_UNSIGNED_SHORT, data);
    }
    
    
    public void RG16(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ShortBuffer buffer = MemoryUtil.memAllocShort(length);
        for (int i = 0; i < length; i += 2) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16, width, height,
                0, GL_RG, GL_UNSIGNED_SHORT, data);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG16(ShortBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16, width, height,
                0, GL_RG, GL_UNSIGNED_SHORT, buffer);
    }
    
    public void RG16(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ByteBuffer buffer = MemoryUtil.memAlloc(length);
        for (int i = 0; i < length; i += 4) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
            buffer.put(data[i + 2]);
            buffer.put(data[i + 3]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16, width, height,
                0, GL_RG, GL_UNSIGNED_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG16(ByteBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16, width, height,
                0, GL_RG, GL_UNSIGNED_SHORT, buffer);
    }
    
    
    
    // ---------------------------------RG16 Signed Normalized
    
    
    
    public void RG16_SNORM(IntArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        IntBuffer buffer = MemoryUtil.memAllocInt(data.size());
        int[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16_SNORM, width, height,
                0, GL_RG, GL_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG16_SNORM(int[][] data) {
        RG16_SNORM(new IntArray2D(data));
    }
    
    public void RG16_SNORM(int[] data, int width, int height) {
        this.width = width;
        this.height = height;
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                buffer.put(data[c + (height * r)]);
            }
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16_SNORM, width, height,
                0, GL_RG, GL_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG16_SNORM(IntBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16_SNORM, width, height,
                0, GL_RG, GL_SHORT, data);
    }
    
    
    public void RG16_SNORM(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ShortBuffer buffer = MemoryUtil.memAllocShort(length);
        for (int i = 0; i < length; i += 2) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16_SNORM, width, height,
                0, GL_RG, GL_SHORT, data);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG16_SNORM(ShortBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16_SNORM, width, height,
                0, GL_RG, GL_SHORT, buffer);
    }
    
    public void RG16_SNORM(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ByteBuffer buffer = MemoryUtil.memAlloc(length);
        for (int i = 0; i < length; i += 4) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
            buffer.put(data[i + 2]);
            buffer.put(data[i + 3]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16_SNORM, width, height,
                0, GL_RG, GL_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RG16_SNORM(ByteBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RG16_SNORM, width, height,
                0, GL_RG, GL_SHORT, buffer);
    }
    
    
    
    // ---------------------------------RGB8 Unsigned Normalized
    
    
    
    public void RGB8(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ByteBuffer buffer = MemoryUtil.memAlloc(length);
        for (int i = 0; i < length; i += 3) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
            buffer.put(data[i + 2]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,3);
        glTexImage2D(target, 0, GL_RGB8, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGB8(ByteBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,3);
        glTexImage2D(target, 0, GL_RGB8, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
    }
    
    
    
    // ---------------------------------RGB8 Signed Normalized
    
    
    
    public void RGB8_SNORM(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ByteBuffer buffer = MemoryUtil.memAlloc(length);
        for (int i = 0; i < length; i += 3) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
            buffer.put(data[i + 2]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,3);
        glTexImage2D(target, 0, GL_RGB8_SNORM, width, height,
                0, GL_RGB, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGB8_SNORM(ByteBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,3);
        glTexImage2D(target, 0, GL_RGB8_SNORM, width, height,
                0, GL_RGB, GL_BYTE, buffer);
    }
    
    
    
    // ---------------------------------RGB16 Unsigned Normalized
    
    
    
    public void RGB16(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ShortBuffer buffer = MemoryUtil.memAllocShort(length);
        for (int i = 0; i < length; i += 3) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
            buffer.put(data[i + 2]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,3);
        glTexImage2D(target, 0, GL_RGB16, width, height,
                0, GL_RGB, GL_UNSIGNED_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGB16(ShortBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,3);
        glTexImage2D(target, 0, GL_RGB16, width, height,
                0, GL_RGB, GL_UNSIGNED_SHORT, buffer);
    }
    
    
    
    // ---------------------------------RGB16 Signed Normalized
    
    
    
    public void RGB16_SNORM(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ShortBuffer buffer = MemoryUtil.memAllocShort(length);
        for (int i = 0; i < length; i += 3) {
            buffer.put(data[i]);
            buffer.put(data[i + 1]);
            buffer.put(data[i + 2]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,3);
        glTexImage2D(target, 0, GL_RGB16_SNORM, width, height,
                0, GL_RGB, GL_SHORT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGB16_SNORM(ShortBuffer data, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,3);
        glTexImage2D(target, 0, GL_RGB16_SNORM, width, height,
                0, GL_RGB, GL_SHORT, data);
    }
    
    
    
    // ---------------------------------RGBA4 Unsigned Normalized
    // todo: include bytes
    
    
    
    public void RGBA4(ShortArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        ShortBuffer buffer = MemoryUtil.memAllocShort(data.size());
        short[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RGBA4, width, height,
                0, GL_RGBA, GL_UNSIGNED_SHORT_4_4_4_4, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGBA4(short[][] data) {
        RGBA4(new ShortArray2D(data));
    }
    
    public void RGBA4(short[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ShortBuffer buffer = MemoryUtil.memAllocShort(length);
        for (short datum : data) {
            buffer.put(datum);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RGBA4, width, height,
                0, GL_RGBA, GL_UNSIGNED_SHORT_4_4_4_4, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGBA4(ShortBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_RGBA4, width, height,
                0, GL_RGBA, GL_UNSIGNED_SHORT_4_4_4_4, buffer);
    }
    
    
    
    // ---------------------------------RGB32F Float
    
    
    
    public void RGB32F(FloatArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.size());
        float[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGB32F, width, height,
                0, GL_RGB, GL_FLOAT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGB32F(float[][] data) {
        RGB32F(new FloatArray2D(data));
    }
    
    public void RGB32F(float[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        FloatBuffer buffer = MemoryUtil.memAllocFloat(length);
        for (float datum : data) {
            buffer.put(datum);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGB32F, width, height,
                0, GL_RGB, GL_FLOAT, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGB32F(FloatBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGB32F, width, height,
                0, GL_RGB, GL_FLOAT, buffer);
    }
    
    
    
    // ---------------------------------RGBA8 Unsigned Normalized
    
    
    
    public void RGBA8(IntArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        IntBuffer buffer = MemoryUtil.memAllocInt(data.size());
        int[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8, width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGBA8(int[][] data) {
        RGBA8(new IntArray2D(data));
    }
    
    public void RGBA8(int[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        IntBuffer buffer = MemoryUtil.memAllocInt(length);
        for (int datum : data) {
            buffer.put(datum);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8, width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGBA8(IntBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8, width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }
    
    public void RGBA8(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ByteBuffer buffer = MemoryUtil.memAlloc(length);
        for (byte datum : data) {
            buffer.put(datum);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8, width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGBA8(ByteBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8, width, height,
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }
    
    
    
    // ---------------------------------RGBA8 Signed Normalized
    
    
    
    public void RGBA8_SNORM(IntArray2D data) {
        Assert.notNull(data);
        this.width = data.cols();
        this.height = data.rows();
        IntBuffer buffer = MemoryUtil.memAllocInt(data.size());
        int[][] array2D = data.get();
        for (int row = 0; row < height; row++) {
            buffer.put(array2D[row]);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8_SNORM, width, height,
                0, GL_RGBA, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGBA8_SNORM(int[][] data) {
        RGBA8_SNORM(new IntArray2D(data));
    }
    
    public void RGBA8_SNORM(int[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        IntBuffer buffer = MemoryUtil.memAllocInt(length);
        for (int datum : data) {
            buffer.put(datum);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8_SNORM, width, height,
                0, GL_RGBA, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGBA8_SNORM(IntBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8_SNORM, width, height,
                0, GL_RGBA, GL_BYTE, buffer);
    }
    
    public void RGBA8_SNORM(byte[] data, int width, int height) {
        this.width = width;
        this.height = height;
        int length = data.length;
        ByteBuffer buffer = MemoryUtil.memAlloc(length);
        for (byte datum : data) {
            buffer.put(datum);
        } buffer.flip();
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8_SNORM, width, height,
                0, GL_RGBA, GL_BYTE, buffer);
        MemoryUtil.memFree(buffer);
    }
    
    public void RGBA8_SNORM(ByteBuffer buffer, int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_RGBA8_SNORM, width, height,
                0, GL_RGBA, GL_BYTE, buffer);
    }
    
    
    
    // ---------------------------------DEPTH / STENCIL
    
    
    
    
    public void DEPTH16(int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,2);
        glTexImage2D(target, 0, GL_DEPTH_COMPONENT16, width, height,
                0, GL_DEPTH_COMPONENT, GL_UNSIGNED_SHORT, (ShortBuffer) null);
    }
    
    public void DEPTH32(int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_DEPTH_COMPONENT32, width, height,
                0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, (IntBuffer) null);
    }
    
    public void DEPTH32F(int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_DEPTH_COMPONENT32F, width, height,
                0, GL_DEPTH_COMPONENT, GL_FLOAT, (FloatBuffer) null);
    }
    
    public void DEPTH24_STENCIL8(int width, int height) {
        this.width = width;
        this.height = height;
        glPixelStorei(GL_UNPACK_ALIGNMENT,4);
        glTexImage2D(target, 0, GL_DEPTH24_STENCIL8, width, height,
                0, GL_UNSIGNED_INT_24_8 , GL_FLOAT, (IntBuffer) null);
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
}
