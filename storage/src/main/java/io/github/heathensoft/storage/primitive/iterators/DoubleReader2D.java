package io.github.heathensoft.storage.primitive.iterators;

/**
 * @author Frederik Dahl
 * 29/05/2022
 */

@FunctionalInterface
public interface DoubleReader2D extends PrimitiveReader2D {
    void next(int x, int y, double value);
}
