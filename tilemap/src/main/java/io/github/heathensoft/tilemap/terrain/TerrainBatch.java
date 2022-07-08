package io.github.heathensoft.tilemap.terrain;

import io.github.heathensoft.common.Assert;
import io.github.heathensoft.common.Disposable;
import io.github.heathensoft.graphics.BufferObject;
import io.github.heathensoft.graphics.VAO;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * @author Frederik Dahl
 * 14/05/2022
 */


public class TerrainBatch implements Disposable {
    
    public static final int BATCH_SIZE = 32;
    
    public enum MeshResolution {
        HIGH(33,0),
        MID(17,1),
        LOW(9,2),
        VERY_LOW(5,3);
        public int value, id;
        MeshResolution(int value, int id) {
            this.value = value;
            this.id = id;
        }
    }
    
    private final TerrainMesh[] meshes = new TerrainMesh[4];
    private final FloatBuffer instanceBuffer;
    private int instanceCount;
    private int drawCallsFrame;
    private int drawCallsTotal;
    private boolean renderState;
    private MeshResolution currentRes;
    
    public TerrainBatch() {
        instanceBuffer = MemoryUtil.memAllocFloat(3 * BATCH_SIZE);
        meshes[MeshResolution.VERY_LOW.id] = new TerrainMesh(MeshResolution.VERY_LOW,BATCH_SIZE);
        meshes[MeshResolution.LOW.id] = new TerrainMesh(MeshResolution.LOW,BATCH_SIZE);
        meshes[MeshResolution.MID.id] = new TerrainMesh(MeshResolution.MID,BATCH_SIZE);
        meshes[MeshResolution.HIGH.id] = new TerrainMesh(MeshResolution.HIGH,BATCH_SIZE);
    }
    
    public void begin(MeshResolution resolution) {
        if (renderState) return;
        drawCallsFrame = 0;
        renderState = true;
        currentRes = resolution;
    }
    
    public void draw(float centerX, float centerY, float scale) {
        if (renderState) {
            if (instanceCount >= BATCH_SIZE) flush();
            instanceBuffer.put(centerX).put(centerY).put(scale);
            instanceCount++;
        }
    }
    
    public void end() {
        if (!renderState) return;
        if (instanceCount > 0) flush();
        renderState = false;
    }
    
    private void flush() {
        instanceBuffer.flip();
        meshes[currentRes.id].render(instanceBuffer, instanceCount);
        instanceBuffer.clear();
        drawCallsTotal++;
        drawCallsFrame++;
        instanceCount = 0;
    }
    
    public int drawCallsTotal() {
        return drawCallsTotal;
    }
    
    public int drawCallsFrame() {
        return drawCallsFrame;
    }
    
    @Override
    public void dispose() {
        if (instanceBuffer != null)
            MemoryUtil.memFree(instanceBuffer);
        VAO.unbind();
        Disposable.dispose(meshes);
    }
    
    private static final class TerrainMesh implements Disposable {
    
    
        private final VAO vao;
        private final BufferObject indices;
        private final BufferObject vertices;
        private final BufferObject instanceData;
        private final int idxCount;
        
    
        TerrainMesh(MeshResolution res, int batchSize) {
            Assert.notNull(res);
            vao = new VAO();
            indices = new BufferObject(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW);
            vertices = new BufferObject(GL_ARRAY_BUFFER, GL_STATIC_DRAW);
            instanceData = new BufferObject(GL_ARRAY_BUFFER, GL_DYNAMIC_DRAW);
            vao.bind();
            indices.bind();
            idxCount = generateIndices(indices, res.value);
            vertices.bind();
            generateVertices(vertices, res.value);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);
            instanceData.bind();
            instanceData.bufferData((long) 3 * batchSize * Float.BYTES);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
            glVertexAttribDivisor(1, 1);
            glEnableVertexAttribArray(1);
        }
    
        void render(FloatBuffer instanceBuffer, int instances) {
            vao.bind();
            instanceData.bind();
            instanceData.bufferSubData(instanceBuffer, 0);
            int count = instances * idxCount;
            glDrawElementsInstanced(GL_TRIANGLE_STRIP, count, GL_UNSIGNED_SHORT, 0, instances);
        }
    
    
        void generateVertices(BufferObject buffer, int res) {
            FloatBuffer positions = null;
            final float delta = 1f / (res - 1);
            try {
                positions = MemoryUtil.memAllocFloat(res * res * 2);
                for (int r = 0; r < res; r++) {
                    for (int c = 0; c < res; c++) {
                        positions.put(-0.5f + c * delta);
                        positions.put(-0.5f + r * delta);
                    }
                }
                buffer.bufferData(positions.flip());
            } finally {
                if (positions != null)
                    MemoryUtil.memFree(positions);
            }
        }
    
        int generateIndices(BufferObject buffer, int res) {
            int stripsReq = res - 1;
            int degensReq = 2 * (stripsReq - 1);
            int verticesPerStrip = 2 * res;
            int numIndices = (verticesPerStrip * stripsReq) + degensReq;
            ShortBuffer indices = null;
            try {
                indices = MemoryUtil.memAllocShort(numIndices);
                for (int r = 0; r < res - 1; r++) {
                    if (r > 0) indices.put((short) (r * res));
                    for (int c = 0; c < res; c++) {
                        indices.put((short) ((r * res) + c));
                        indices.put((short) (((r + 1) * res) + c));
                    }
                    if (r < res - 2) indices.put((short) (((r + 1) * res) + (res - 1)));
                }
                buffer.bufferData(indices.flip());
            } finally {
                if (indices != null)
                    MemoryUtil.memFree(indices);
            }
            return numIndices;
        }
    
        @Override
        public void dispose() {
            VAO.unbind();
            Disposable.dispose(vertices, indices, instanceData, vao);
        }
    }
}
