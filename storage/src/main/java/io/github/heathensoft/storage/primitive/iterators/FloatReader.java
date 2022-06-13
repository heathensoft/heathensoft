package io.github.heathensoft.storage.primitive.iterators;

/**
 * @author Frederik Dahl
 * 29/05/2022
 */

@FunctionalInterface
public interface FloatReader extends PrimitiveReader {
    void next(float value);
}
