package io.github.heathensoft.storage.primitive.iterators;

/**
 * @author Frederik Dahl
 * 29/05/2022
 */

@FunctionalInterface
public interface LongReader extends PrimitiveReader {
    void next(long value);
}
