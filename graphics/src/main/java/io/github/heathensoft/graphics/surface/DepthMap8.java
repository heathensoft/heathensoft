package io.github.heathensoft.graphics.surface;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.graphics.resources.Image;
import io.github.heathensoft.graphics.texture.Texture2D;
import org.joml.Math;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBImageWrite.stbi_write_png;

/**
 * 8-bit depth map
 *
 * @author Frederik Dahl
 * 31/03/2022
 */


public class DepthMap8 {
    
    private final int cols;
    private final int rows;
    private final byte[] map;
    
    public DepthMap8(NoiseMap nm) {
        Assert.notNull(nm);
        this.cols = nm.cols();
        this.rows = nm.rows();
        this.map = new byte[cols * rows];
        float[][] m = nm.map().get();
        float amp = nm.amplitude();
        float bsl = nm.baseline();
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float n = ((m[r][c] - bsl) / amp + 1) / 2f;
                map[idx++] = (byte)(Math.round(n * 0xff) & 0xff);
            }
        }
    }
    
    public DepthMap8(Image img) {
        Assert.notNull(img);
        this.cols = img.width();
        this.rows = img.height();
        this.map = new byte[cols * rows];
        int c = img.channels();
        int avg = 0;
        int length = size();
        ByteBuffer data = img.data();
        switch (c) {
            case 1: case 2: case 3:
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < c; j++)
                        avg += (data.get(i*c+j) & 0xff);
                    avg = Math.round((float) avg/c);
                    map[i] = (byte) (avg & 0xff);
                    avg = 0;
                }
                break;
            case 4:
                for (int i = 0; i < length; i++) {
                    avg += (data.get(i*c) & 0xff);
                    avg += (data.get(i*c+1) & 0xff);
                    avg += (data.get(i*c+2) & 0xff);
                    avg = Math.round((float) avg/3);
                    map[i] = (byte) (avg & 0xff);
                    avg = 0;
                }
            break;
        }
    }
    
    public Texture2D toTexture(int GL_WRAP, int GL_FILTER) {
        Texture2D texture = new Texture2D();
        texture.bind();
        texture.filter(GL_FILTER);
        texture.wrapST(GL_WRAP);
        texture.R8(map,cols,rows);
        return texture;
    }
    
    public void toPNG(String path) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(size());
        buffer.put(map).flip();
        stbi_write_png(path, cols, rows,1,buffer, cols);
    }
    
    public byte[] data() {
        return map;
    }
    
    public int cols() {
        return cols;
    }
    
    public int rows() {
        return rows;
    }
    
    public int size() {
        return map.length;
    }
}
