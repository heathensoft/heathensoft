package io.github.heathensoft.tilemap.terrain;

/**
 * @author Frederik Dahl
 * 27/06/2022
 */


public interface TerrainEditCallback {
    
    void onEdit(int row, int col, short oldMask, short newMask);
    
    
}
