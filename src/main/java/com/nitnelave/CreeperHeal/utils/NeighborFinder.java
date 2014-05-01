package com.nitnelave.CreeperHeal.utils;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Location;

/**
 * 2-D map, allowing for fast neighbor search. The map is divided in zones, and
 * in each zone there is a list of Location.
 * 
 * @param <T>
 *            The type of the data stored for each point.
 * @author nitnelave
 */
public abstract class NeighborFinder<T>
{

    /**
     * The size of each zone.
     */
    private static final int BLOCK_SIZE = 64;
    protected HashMap<Point, LinkedList<T>> map = new HashMap<Point, LinkedList<T>>();

    /**
     * Add an element to the map. It is placed in the correct zone, created if
     * necessary.
     * 
     * @param el
     *            The element.
     * @param x
     *            The x coordinate.
     * @param y
     *            The y coordinate.
     */
    public void addElement(T el, double x, double y)
    {
        if (el == null)
            return;
        Point p = new Point((int) (x / BLOCK_SIZE), (int) (y / BLOCK_SIZE));
        LinkedList<T> list = map.get(p);
        if (list == null)
        {
            list = new LinkedList<T>();
            map.put(p, list);
        }
        list.add(el);
    }

    /**
     * Remove an element from the map. It must be in the zone at the x, y
     * coordinate.
     * 
     * @param el
     *            The element to remove.
     * @param x
     *            The x coordinate for the zone.
     * @param y
     *            The y coordinate for the zone.
     */
    public void removeElement(T el, double x, double y)
    {
        Point p = new Point((int) (x / BLOCK_SIZE), (int) (y / BLOCK_SIZE));
        LinkedList<T> list = map.get(p);
        if (list == null)
            return;
        list.remove(el);
        if (list.isEmpty())
            map.remove(p);
    }

    /**
     * Get whether the location is "close" to an element. The actual definition
     * of "close" is in the overridden methods of the children classes.
     * 
     * @param loc
     *            The point to check for neighbors.
     * @return Whether the location is close to an element.
     */
    public boolean hasNeighbor(Location loc)
    {
        return getNeighbor(loc) != null;
    }

    /**
     * Get a neighbor of the location provided. It is not guaranteed to be the
     * closest, but it is an element which satisfies the condition of a
     * neighbor.
     * 
     * @param loc
     *            The location to check for neighbors.
     * @return A neighboring element, null if no such element exists.
     */
    public T getNeighbor(Location loc)
    {
        int x = (int) (loc.getX() / BLOCK_SIZE), y = (int) (loc.getZ() / BLOCK_SIZE);
        LinkedList<T> list = map.get(new Point(x, y));
        T neighbor = getNeighbor(loc, list);
        if (neighbor != null)
            return neighbor;
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++)
            {
                if (i == 0 && j == 0)
                    continue;
                list = map.get(new Point(x + i, y + j));
                neighbor = getNeighbor(loc, list);
                if (neighbor != null)
                    return neighbor;
            }
        return null;
    }

    protected abstract T getNeighbor(Location loc, LinkedList<T> list);

    /**
     * Get whether the map is completely empty (no zones).
     * 
     * @return Whether the map is empty.
     */
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    /**
     * Clean the map by removing useless elements in zones, and empty zones.
     */
    public abstract void clean();

    /**
     * Clear the map of all elements.
     */
    public void clear()
    {
        map.clear();
    }
}
