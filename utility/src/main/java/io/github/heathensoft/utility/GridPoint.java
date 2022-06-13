package io.github.heathensoft.utility;

public class GridPoint {

    public int x, y;
    
    public GridPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GridPoint() {
        this(0,0);
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * @param other GridPoint
     * @return minimum discrete moves (diagonal allowed)
     */
    public int distance(GridPoint other) {
        return distance(this,other);
    }
    
    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        GridPoint c = (GridPoint)o;
        return this.x == c.x && this.y == c.y;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.x;
        result = prime * result + this.y;
        return result;
    }

    @Override
    public String toString () {
        return "(" + x + ", " + y + ")";
    }
    
    /**
     * @param n1 GridPoint
     * @param n2 GridPoint
     * @return minimum discrete moves (diagonal allowed)
     */
    public static int distance(GridPoint n1, GridPoint n2) {
        int xDist = Math.abs(n1.x - n2.x);
        int yDist = Math.abs(n1.y - n2.y);
        if (xDist < yDist)
            return xDist + (yDist - xDist);
        else return yDist + (xDist - yDist);
    }
}
