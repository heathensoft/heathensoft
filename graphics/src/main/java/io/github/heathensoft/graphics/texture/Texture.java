package io.github.heathensoft.graphics.texture;

import io.github.heathensoft.common.Disposable;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_LOD_BIAS;

/**
 * @author Frederik Dahl
 * 20/06/2022
 */


/*
 OpenGL image formats along with their un-sized variants and preferred formats for pixel transfer (Written by hand,
 needs verification) Pixel store for compressed textures not provided because there are glCompressedTexImage and family for them.
 EXT_texture_compression_s3tc formats not included.
 
The ones inlined are implemented
 
|          Image format (sized)         |      Unsized       | Compr |    Pixel format    |             Pixel type            |
|---------------------------------------|--------------------|-------|--------------------|-----------------------------------|
|   GL_R8                                 | GL_RED             | False | GL_RED             | GL_UNSIGNED_BYTE                  |
|   GL_R8_SNORM                           | GL_RED             | False | GL_RED             | GL_BYTE                           |
|   GL_R16                                | GL_RED             | False | GL_RED             | GL_UNSIGNED_SHORT                 |
|   GL_R16_SNORM                          | GL_RED             | False | GL_RED             | GL_SHORT                          |
|   GL_R32F                               | GL_RED             | False | GL_RED             | GL_FLOAT                          |
| GL_R8I                                | GL_RED             | False | GL_RED_INTEGER     | GL_INT                            |
| GL_R8UI                               | GL_RED             | False | GL_RED_INTEGER     | GL_UNSIGNED_INT                   |
| GL_R16I                               | GL_RED             | False | GL_RED_INTEGER     | GL_INT                            |
| GL_R16UI                              | GL_RED             | False | GL_RED_INTEGER     | GL_UNSIGNED_INT                   |
|   GL_R32I                               | GL_RED             | False | GL_RED_INTEGER     | GL_INT                            |
|   GL_R32UI                              | GL_RED             | False | GL_RED_INTEGER     | GL_UNSIGNED_INT                   |
| GL_R16F                               | GL_RED             | False | GL_RED             | GL_HALF_FLOAT                     |
|   GL_RG8                                | GL_RG              | False | GL_RG              | GL_UNSIGNED_BYTE                  |
|   GL_RG8_SNORM                          | GL_RG              | False | GL_RG              | GL_BYTE                           |
|   GL_RG16                               | GL_RG              | False | GL_RG              | GL_UNSIGNED_SHORT                 |
|   GL_RG16_SNORM                         | GL_RG              | False | GL_RG              | GL_SHORT                          |
| GL_RG16F                              | GL_RG              | False | GL_RG              | GL_HALF_FLOAT                     |
| GL_RG32F                              | GL_RG              | False | GL_RG              | GL_FLOAT                          |
| GL_RG8I                               | GL_RG              | False | GL_RG_INTEGER      | GL_INT                            |
| GL_RG8UI                              | GL_RG              | False | GL_RG_INTEGER      | GL_UNSIGNED_INT                   |
| GL_RG16I                              | GL_RG              | False | GL_RG_INTEGER      | GL_INT                            |
| GL_RG16UI                             | GL_RG              | False | GL_RG_INTEGER      | GL_UNSIGNED_INT                   |
| GL_RG32I                              | GL_RG              | False | GL_RG_INTEGER      | GL_INT                            |
| GL_RG32UI                             | GL_RG              | False | GL_RG_INTEGER      | GL_UNSIGNED_INT                   |
| GL_R3_G3_B2                           | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_BYTE_3_3_2            |
| GL_RGB4                               | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_BYTE                  |
| GL_RGB5                               | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_BYTE                  |
|   GL_RGB8                               | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_BYTE                  |
|   GL_RGB8_SNORM                         | GL_RGB             | False | GL_RGB             | GL_BYTE                           |
| GL_RGB10                              | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_SHORT                 |
| GL_RGB12                              | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_SHORT                 |
|   GL_RGB16_SNORM                        | GL_RGB             | False | GL_RGB             | GL_SHORT                          |
| GL_RGBA2                              | GL_RGBA            | False | GL_RGBA            | GL_UNSIGNED_SHORT_4_4_4_4         |
|   GL_RGBA4                              | GL_RGBA            | False | GL_RGBA            | GL_UNSIGNED_SHORT_4_4_4_4         |
| GL_SRGB8                              | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_BYTE                  |
| GL_RGB16F                             | GL_RGB             | False | GL_RGB             | GL_HALF_FLOAT                     |
|   GL_RGB32F                             | GL_RGB             | False | GL_RGB             | GL_FLOAT                          |
| GL_R11F_G11F_B10F                     | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_INT_10F_11F_11F_REV   |
| GL_RGB9_E5                            | GL_RGB             | False | GL_RGB             | GL_UNSIGNED_INT_5_9_9_9_REV       |
| GL_RGB8I                              | GL_RGB             | False | GL_RGB_INTEGER     | GL_INT                            |
| GL_RGB8UI                             | GL_RGB             | False | GL_RGB_INTEGER     | GL_UNSIGNED_INT                   |
| GL_RGB16I                             | GL_RGB             | False | GL_RGB_INTEGER     | GL_INT                            |
| GL_RGB16UI                            | GL_RGB             | False | GL_RGB_INTEGER     | GL_UNSIGNED_INT                   |
| GL_RGB32I                             | GL_RGB             | False | GL_RGB_INTEGER     | GL_INT                            |
| GL_RGB32UI                            | GL_RGB             | False | GL_RGB_INTEGER     | GL_UNSIGNED_INT                   |
| GL_RGB5_A1                            | GL_RGBA            | False | GL_RGBA            | GL_UNSIGNED_SHORT_5_5_5_1         |
|   GL_RGBA8                              | GL_RGBA            | False | GL_RGBA            | GL_UNSIGNED_BYTE                  |
|   GL_RGBA8_SNORM                        | GL_RGBA            | False | GL_RGBA            | GL_BYTE                           |
| GL_RGB10_A2                           | GL_RGBA            | False | GL_RGBA            | GL_UNSIGNED_INT_10_10_10_2        |
| GL_RGB10_A2UI                         | GL_RGBA            | False | GL_RGBA_INTEGER    | GL_UNSIGNED_INT_10_10_10_2        |
| GL_RGBA12                             | GL_RGBA            | False | GL_RGBA            | GL_UNSIGNED_SHORT                 |
| GL_RGBA16                             | GL_RGBA            | False | GL_RGBA            | GL_UNSIGNED_SHORT                 |
| GL_SRGB8_ALPHA8                       | GL_RGBA            | False | GL_RGBA            | GL_UNSIGNED_BYTE                  |
| GL_RGBA16F                            | GL_RGBA            | False | GL_RGBA            | GL_HALF_FLOAT                     |
| GL_RGBA32F                            | GL_RGBA            | False | GL_RGBA            | GL_FLOAT                          |
| GL_RGBA8I                             | GL_RGBA            | False | GL_RGBA_INTEGER    | GL_INT                            |
| GL_RGBA8UI                            | GL_RGBA            | False | GL_RGBA_INTEGER    | GL_UNSIGNED_INT                   |
| GL_RGBA16I                            | GL_RGBA            | False | GL_RGBA_INTEGER    | GL_INT                            |
| GL_RGBA16UI                           | GL_RGBA            | False | GL_RGBA_INTEGER    | GL_UNSIGNED_INT                   |
| GL_RGBA32I                            | GL_RGBA            | False | GL_RGBA_INTEGER    | GL_INT                            |
| GL_RGBA32UI                           | GL_RGBA            | False | GL_RGBA_INTEGER    | GL_UNSIGNED_INT                   |
| GL_DEPTH_COMPONENT16                  | GL_DEPTH_COMPONENT | False | GL_DEPTH_COMPONENT | GL_UNSIGNED_SHORT                 |
| GL_DEPTH_COMPONENT24                  | GL_DEPTH_COMPONENT | False | GL_DEPTH_COMPONENT | GL_UNSIGNED_INT                   |
| GL_DEPTH_COMPONENT32                  | GL_DEPTH_COMPONENT | False | GL_DEPTH_COMPONENT | GL_UNSIGNED_INT                   |
| GL_DEPTH_COMPONENT32F                 | GL_DEPTH_COMPONENT | False | GL_DEPTH_COMPONENT | GL_FLOAT                          |
| GL_DEPTH24_STENCIL8                   | GL_DEPTH_STENCIL   | False | GL_DEPTH_STENCIL   | GL_UNSIGNED_INT_24_8              |
| GL_DEPTH32F_STENCIL8                  | GL_DEPTH_STENCIL   | False | GL_DEPTH_STENCIL   | GL_FLOAT_32_UNSIGNED_INT_24_8_REV |
| GL_COMPRESSED_RED                     | GL_RED             | True  | -                  | -                                 |
| GL_COMPRESSED_RED_RGTC1               | GL_RED             | True  | -                  | -                                 |
| GL_COMPRESSED_SIGNED_RED_RGTC1        | GL_RED             | True  | -                  | -                                 |
| GL_COMPRESSED_RG                      | GL_RG              | True  | -                  | -                                 |
| GL_COMPRESSED_RG_RGTC2                | GL_RG              | True  | -                  | -                                 |
| GL_COMPRESSED_SIGNED_RG_RGTC2         | GL_RG              | True  | -                  | -                                 |
| GL_COMPRESSED_RGB                     | GL_RGB             | True  | -                  | -                                 |
| GL_COMPRESSED_RGB_BPTC_SIGNED_FLOAT   | GL_RGB             | True  | -                  | -                                 |
| GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT | GL_RGB             | True  | -                  | -                                 |
| GL_COMPRESSED_SRGB                    | GL_RGB             | True  | -                  | -                                 |
| GL_COMPRESSED_RGBA                    | GL_RGBA            | True  | -                  | -                                 |
| GL_COMPRESSED_RGBA_BPTC_UNORM         | GL_RGBA            | True  | -                  | -                                 |
| GL_COMPRESSED_SRGB_ALPHA              | GL_RGBA            | True  | -                  | -                                 |
| GL_COMPRESSED_SRGB_ALPHA_BPTC_UNORM   | GL_RGBA            | True  | -                  | -                                 |
 */


public class Texture implements Disposable, ITex {
    
    protected static int activeSlot = 0;
    protected final int id;
    protected final int target;
    
    public Texture(int target) {
        this.id = glGenTextures();
        this.target = target;
    }
    
    public void bind() {
        GL11C.glBindTexture(target,id);
    }
    
    public void bind(int slot) {
        if (slot != activeSlot) {
            glActiveTexture(slot + GL_TEXTURE0);
            activeSlot = slot;
        } bind();
    }
    
    public int id() {
        return id;
    }
    
    public int target() {
        return target;
    }
    
    public void generateMipMap(float lodBias, float min, float max) {
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
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
    }
    
    public void nearest() {
        filter(GL_NEAREST);
    }
    
    public void linear() {
        filter(GL_LINEAR);
    }
    
    public void wrapS(int wrapS) {
        glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
    }
    
    public void wrapT(int wrapT) {
        glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
    }
    
    public void wrapR(int wrapR) {
        glTexParameteri(target, GL_TEXTURE_WRAP_R, wrapR);
    }
    
    public void wrapST(int wrapS, int wrapT) {
        glTexParameteri(target, GL_TEXTURE_WRAP_S, wrapS);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, wrapT);
    }
    
    public void wrapSTR(int wrapS, int wrapT, int wrapR) {
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
    
    @Override
    public void dispose() {
        glDeleteTextures(id);
    }
    
    public static int activeSlot() {
        return activeSlot;
    }
    
    public static void unBind(int target) {
        GL11C.glBindTexture(target,0);
    }
}
