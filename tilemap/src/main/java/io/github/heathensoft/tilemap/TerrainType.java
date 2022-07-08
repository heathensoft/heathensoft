package io.github.heathensoft.tilemap;

/**
 * @author Frederik Dahl
 * 27/06/2022
 */


public enum TerrainType {
    
    INVALID("INVALID",(short) 0x1111),
    PRIMARY("CLEAR", (short) 0x0000),
    SECONDARY("RED", (short) 0xF000),
    DIRT("GREEN", (short) 0x0F00),
    WATER("BLUE", (short) 0x00F0),
    ROAD("ALPHA", (short) 0x000F);
    
    public final String channel;
    public final short rgba4;
    
    public TerrainType topLayer(short mask) {
        if (mask == PRIMARY.rgba4) return PRIMARY;
        if ((mask & ROAD.rgba4) == ROAD.rgba4) return ROAD;
        if ((mask & WATER.rgba4) == WATER.rgba4) return WATER;
        if ((mask & DIRT.rgba4) == DIRT.rgba4) return DIRT;
        if ((mask & SECONDARY.rgba4) == SECONDARY.rgba4) return SECONDARY;
        return INVALID;
    }
    
    public boolean contains(short mask, TerrainType type) {
        return (mask & type.rgba4) == type.rgba4;
    }
    
    public boolean isPrimary(short mask) {
        return mask == PRIMARY.rgba4;
    }
    
    public boolean isSecondary(short mask) {
        return (mask | 0x0FFF) == SECONDARY.rgba4;
    }
    
    public boolean isDirt(short mask) {
        return ((mask | 0x00FF) & DIRT.rgba4) == DIRT.rgba4;
    }
    
    public boolean isRoad(short mask) {
        return (mask & ROAD.rgba4) == ROAD.rgba4;
    }
    
    public boolean isWater(short mask) {
        return !isRoad(mask) && (mask & WATER.rgba4) == WATER.rgba4;
    }
    
    TerrainType(String channel, short rgba4) {
        this.channel = channel;
        this.rgba4 = rgba4;
    }
    
}
