package io.github.heathensoft.graphics.texture;

/**
 * @author Frederik Dahl
 * 08/07/2022
 */


public interface ITex {
    
    void bind();
    void bind(int slot);
    int id();
    int target();
    void dispose();
    
}
