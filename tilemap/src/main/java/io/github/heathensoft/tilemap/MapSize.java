package io.github.heathensoft.tilemap;

/**
 * @author Frederik Dahl
 * 29/06/2022
 */


public enum MapSize {
    
    TINY(6),
    SMALL(7),
    MEDIUM(8),
    LARGE(9),
    HUGE(10),
    GARGANTUAN(11);
    
    public final int value;
    public final int log2;
    
    MapSize(int log2) {
        this.log2 = log2;
        this.value = (int)Math.pow(2,log2);
    }
}
