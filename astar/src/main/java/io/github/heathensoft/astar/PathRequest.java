package io.github.heathensoft.astar;

import io.github.heathensoft.utility.GridPoint;

/**
 * @author Frederik Dahl
 * 13/06/2022
 */


public class PathRequest implements Comparable<PathRequest> {
    
    private final GridPoint start;
    private final GridPoint stop;
    private final SearchArea searchArea;
    private int[] result;
    private int priority;
    
    private final boolean collapse;
    private boolean resolved;
    private boolean pathFound;
    
    public PathRequest(int x1, int y1, int x2, int y2, SearchArea searchArea, boolean collapse) {
        this(new GridPoint(x1, y1), new GridPoint(x2, y2), searchArea, collapse);
    }
    
    public PathRequest(int x1, int y1, int x2, int y2, SearchArea searchArea) {
        this(x1, y1, x2, y2, searchArea, true);
    }
    
    protected PathRequest(GridPoint start, GridPoint stop, SearchArea searchArea, boolean collapse) {
        this.searchArea = searchArea;
        this.collapse = collapse;
        this.start = start;
        this.stop = stop;
        preValidate();
    }
    
    protected PathRequest(GridPoint start, GridPoint stop, SearchArea searchArea) {
        this(start, stop, searchArea, true);
    }
    
    public SearchArea searchArea() {
        return searchArea;
    }
    
    protected GridPoint start() {
        return start;
    }
    
    protected GridPoint stop() {
        return stop;
    }
    
    public int pathLength() {
        return result == null ? 0 : result.length / 2;
    }
    
    public int[] result() {
        return result;
    }
    
    public boolean resolved() {
        return resolved;
    }
    
    public boolean pathFound() {
        return pathFound;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public int priority() {
        return priority;
    }
    
    protected void resolve(int[] path) {
        this.result = path;
        this.pathFound = true;
        this.resolved = true;
    }
    
    protected void resolve() {
        this.pathFound = false;
        this.resolved = true;
    }
    
    protected boolean collapsePath() {
        return collapse;
    }
    
    private void preValidate() {
        if (searchArea.traversable(start.x, start.y) && searchArea.traversable(stop.x, stop.y)) {
            if (start.equals(stop)) resolve(new int[]{stop.x,stop.y});
        } else resolve();
    }
    
    @Override
    public int compareTo(PathRequest o) {
        return Integer.compare(priority, o.priority);
    }
    
}