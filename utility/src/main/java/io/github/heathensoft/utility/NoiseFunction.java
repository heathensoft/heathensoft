package io.github.heathensoft.utility;

/**
 *
 * Should return a value n: 1 >= n >= -1
 *
 * @author Frederik Dahl
 * 23/06/2022
 */


public interface NoiseFunction {
    
    float get(float x, float y);
}
