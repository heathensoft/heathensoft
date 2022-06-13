package io.github.heathensoft.astar;

import io.github.heathensoft.storage.generic.HeapNode;
import io.github.heathensoft.storage.primitive.IntStack;
import io.github.heathensoft.utility.GridPoint;

/**
 * @author Frederik Dahl
 * 13/06/2022
 */


class SearchNode extends HeapNode<SearchNode> {
    
    private SearchNode parent;
    private final int x;
    private final int y;
    private int hCost; // distance from end node
    private int gCost; // distance from starting node
    private int penalty; // custom move penalty
    private static final int DIAGONAL_COST = 14;
    private static final int ORTHOGONAL_COST = 10;
    
    public SearchNode(GridPoint gridPoint) {
        this(gridPoint.x,gridPoint.y);
    }
    
    public SearchNode(int x, int y) { this.x = x; this.y = y; }
    
    protected IntStack retracePath(boolean collapse) {
        if (collapse) collapse();
        int nodes = depth();
        if (nodes == 0) return null;
        IntStack path = new IntStack(nodes<<1);
        SearchNode current = this;
        while (current.parent != null) {
            path.push(current.x);
            path.push(current.y);
            current = current.parent;
        } return path;
    }
    
    private void collapse() {
        SearchNode rear = this;
        SearchNode center = rear.parent;
        SearchNode front;
        float dirRCX, dirCFX;
        float dirRCY, dirCFY;
        while (center != null) {
            front = center.parent;
            if (front == null) break;
            dirRCX = Math.signum(rear.x - center.x);
            dirRCY = Math.signum(rear.y - center.y);
            dirCFX = Math.signum(center.x - front.x);
            dirCFY = Math.signum(center.y - front.y);
            if (dirRCX == dirCFX && dirRCY == dirCFY) {
                rear.parent = front;
            } else rear = rear.parent;
            center = front;
        }
    }
    
    private int depth() {
        // excluding the startNode
        int depth = 0;
        SearchNode current = this;
        while (current.parent != null) {
            current = current.parent;
            depth++;
        } return depth;
    }
    
    protected int movePenalty() {
        return penalty;
    }
    
    protected void setMovePenalty(int penalty) {
        this.penalty = penalty;
    }
    
    protected void setParent(SearchNode parent) {
        this.parent = parent;
    }
    
    protected SearchNode getParent() {
        return parent;
    }
    
    protected void setGCost(int gCost) {
        this.gCost = gCost;
    }
    
    protected void setHCost(int hCost) {
        this.hCost = hCost;
    }
    
    protected int getFCost() {
        return hCost + gCost;
    }
    
    protected int getGCost() {
        return gCost;
    }
    
    protected int getHCost() {
        return hCost;
    }
    
    protected int getX() {
        return x;
    }
    
    protected int getY() {
        return y;
    }
    
    protected void setCost(int hCost, int gCost) {
        this.hCost = hCost;
        this.gCost = gCost;
    }
    
    @Override
    public int compareTo(SearchNode o) {
        int compare = Integer.compare(o.getFCost(), getFCost());
        return compare == 0 ? Integer.compare(o.hCost,hCost) : compare;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchNode)) return false;
        SearchNode node = (SearchNode) o;
        return x == node.x && y == node.y;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }
    
    @Override
    public String toString() {
        return "Node{" + "x=" + x + ", y=" + y + '}';
    }
    
    public static int getDistance(SearchNode n1, SearchNode n2) {
        int xDist = Math.abs(n1.x - n2.x);
        int yDist = Math.abs(n1.y - n2.y);
        if (xDist < yDist)
            return DIAGONAL_COST  * xDist + ORTHOGONAL_COST * (yDist - xDist);
        else return DIAGONAL_COST * yDist + ORTHOGONAL_COST * (xDist - yDist);
    }
}
