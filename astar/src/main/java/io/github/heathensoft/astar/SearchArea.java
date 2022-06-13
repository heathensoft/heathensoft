package io.github.heathensoft.astar;

/**
 * @author Frederik Dahl
 * 13/06/2022
 */


public interface SearchArea {
    
    /**
     * @param x coordinate x
     * @param y coordinate y
     * @return if coordinate is traversable
     */
    boolean traversable(int x, int y);
    
    default boolean notTraversable(int x, int y) {
        return !traversable(x,y);
    }
    
    /**
     * This method does not need to check whether we are
     * outside the area. This is never the case.
     * @param x coordinate x
     * @param y coordinate y
     * @return movement penalty of coordinate
     */
    default int movementPenalty(int x, int y) {
        return 0;
    }
    
    /** @return rows * cols */
    int areaSize();
    
    
}
