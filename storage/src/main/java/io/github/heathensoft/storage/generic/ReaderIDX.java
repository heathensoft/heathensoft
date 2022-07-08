package io.github.heathensoft.storage.generic;

/**
 * @author Frederik Dahl
 * 02/07/2022
 */


public interface ReaderIDX<E> {
    
    /**
     * For all implementations in the "storage" package:
     * Passed items are never null.
     * @param item passed item
     * @param index passed items index
     */
    void next(E item, int index);
}
