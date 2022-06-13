package io.github.heathensoft.storage.primitive.external;


public interface IntIntMap {
    public int get( final int key );
    public int put( final int key, final int value );
    public int remove( final int key );
    public int size();
}
