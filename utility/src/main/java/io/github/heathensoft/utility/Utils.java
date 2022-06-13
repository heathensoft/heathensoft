package io.github.heathensoft.utility;

/**
 * @author Frederik Dahl
 * 13/06/2022
 */


public class Utils {
    
    
    public static final int[][] adjacent = {
            {-1,-1},{-1, 0},{-1, 1},
            { 0,-1}        ,{ 0, 1},
            { 1,-1},{ 1, 0},{ 1, 1}
    };
    
    public static int nextPowerOfTwo(int value) {
        if (value-- == 0) return 1;
        value |= value >>> 1;
        value |= value >>> 2;
        value |= value >>> 4;
        value |= value >>> 8;
        value |= value >>> 16;
        return value + 1;
    }
}
