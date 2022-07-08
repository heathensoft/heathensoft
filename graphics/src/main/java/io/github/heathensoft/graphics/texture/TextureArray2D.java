package io.github.heathensoft.graphics.texture;

import io.github.heathensoft.graphics.resources.Image;

import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_RG8;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

/**
 * @author Frederik Dahl
 * 08/07/2022
 */


public class TextureArray2D extends Texture implements ITex2D {
    
    private int width;
    private int height;
    private int depth;
    
    public TextureArray2D() {
        super(GL_TEXTURE_2D_ARRAY);
    }
    
    public void images(Image... images) throws Exception{
        if (images == null || images.length < 2)
            throw new Exception("Invalid argument");
        int width = images[0].width();
        int height = images[0].height();
        int channels = images[0].channels();
        for (int i = 1; i < images.length; i++) {
            if (width != images[i].width()
                        || height != images[i].height()
                        || channels != images[i].channels()) {
                throw new Exception(
                        "Texture Array images must equal in dimensions");
            }
        }
        this.width = width;
        this.height = height;
        this.depth = images.length;
        int i_format; // GPU side
        int format; // client memory
        int stride = 4;
        switch (channels) {
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
        glTexStorage3D(target,0,i_format,width,height,depth);
        for (int i = 0; i < depth; i++) {
            glTexSubImage3D(target,0,0,0,i, width,height,
                    depth,format,GL_UNSIGNED_BYTE,images[i].data());
        }
    }
    
    public void repeat() {
        wrapST(GL_REPEAT);
    }
    
    public void clampToEdge() {
        wrapST(GL_CLAMP_TO_EDGE);
    }
    
    public void clampToBorder() {
        wrapST(GL_CLAMP_TO_BORDER);
    }
    
    public int width() {
        return width;
    }
    
    public int height() {
        return height;
    }
    
    public int depth() {
        return depth;
    }
}
