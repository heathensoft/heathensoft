package io.github.heathensoft.storage.primitive;

import io.github.heathensoft.storage.primitive.iterators.PrimitiveReader;

/**
 * @author Frederik Dahl
 * 29/05/2022
 */

interface ReadablePrimitive<I extends PrimitiveReader> {
    void read(I itr);
}
