package com.nitnelave.CreeperHeal.utils;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * A simple utility class to store a date and a location.
 * 
 * @author nitnelave
 * 
 */
public class DateLoc
{
    private final Date date;
    private final Location location;

    /**
     * Basic constructor.
     * 
     * @param date
     *            The date to be stored.
     * @param location
     *            The location to be stored.
     */
    public DateLoc(Date date, Location location)
    {
        this.date = date;
        this.location = location;
    }

    /**
     * Get the stored date.
     * 
     * @return The stored date.
     */
    public Date getTime()
    {
        return date;
    }

    /**
     * The stored location.
     * 
     * @return The stored location.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Get the location's world.
     * 
     * @return The location's world.
     */
    public World getWorld()
    {
        return location.getWorld();
    }

}