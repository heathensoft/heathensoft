package io.github.heathensoft.storage.generic;

import static io.github.heathensoft.storage.generic.Heap.NOT_ASSIGNED;

/**
 * A HeapNode can only be assigned to one heap at a time.
 *
 * @author Frederik Dahl
 * 09/06/2022
 */


public abstract class HeapNode<T> implements Comparable<T>{
    
    protected int index = NOT_ASSIGNED;
    protected boolean isAssigned() {
        return index != NOT_ASSIGNED;
    }
}
