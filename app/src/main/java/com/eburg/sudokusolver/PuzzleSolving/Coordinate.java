package com.eburg.sudokusolver.PuzzleSolving;

public class Coordinate implements Comparable<Coordinate> {
    public int x;
    public int y;

    public Coordinate(int xVal, int yVal) {
        x = xVal;
        y = yVal;
    }

    @Override
    public int compareTo(Coordinate other) {
        int res = Integer.compare(this.y, other.y);
        if (res != 0)
            return res;
        return Integer.compare(this.x, other.x);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof Coordinate))
            return false;
        Coordinate other = (Coordinate)otherObject;
        return this.x == other.x && this.y == other.y;
    }
}
