package io.github.heathensoft.common;

/**
 * @author Frederik Dahl
 * 13/06/2022
 */


public class Utils {
    
    
    private static final int[] logTable;
    
    static {
        logTable = new int[256];
        logTable[0] = logTable[1] = 0;
        for (int i=2; i<256; i++) logTable[i] = 1 + logTable[i/2];
        logTable[0] = -1;
    }
    
    public static int nextPowerOfTwo(int value) {
        if (value-- == 0) return 1;
        value |= value >>> 1;
        value |= value >>> 2;
        value |= value >>> 4;
        value |= value >>> 8;
        value |= value >>> 16;
        return value + 1;
    }
    
    public static int log2(float f) {
        int x = Float.floatToIntBits(f);
        int c = x >> 23;
        if (c != 0) return c - 127; //Compute directly from exponent.
        else { //Subnormal, must compute from mantissa.
            int t = x >> 16;
            if (t != 0) return logTable[t] - 133;
            else return (x >> 8 != 0) ? logTable[t] - 141 : logTable[x] - 149;
        }
    }
    
    public static final int[][] adjacent = {
            {-1,-1},{-1, 0},{-1, 1},
            { 0,-1}        ,{ 0, 1},
            { 1,-1},{ 1, 0},{ 1, 1}
    };
    
    public static boolean equals(double a, double b, double delta) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b) || Math.abs(a - b) <= delta;
    }
    
    public static boolean equals(float a, float b, float delta) {
        return Float.floatToIntBits(a) == Float.floatToIntBits(b) || Math.abs(a - b) <= delta;
    }
}
