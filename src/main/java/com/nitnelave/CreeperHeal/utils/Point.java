package com.nitnelave.CreeperHeal.utils;

/**
 * Utility class to store a simple 2-D point with integer coordinates.
 * 
 * @author nitnelave
 * 
 */
public class Point
{
    /**
     * The coordinates.
     */
    public int x, y;

    /**
     * Simple constructor.
     * 
     * @param x
     *            The x coordinate.
     * @param y
     *            The y coordinate.
     */
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o instanceof Point)
        {
            Point p = (Point) o;
            if (p.x == x && p.y == y)
                return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return x * 10000 + y;
    }

}
