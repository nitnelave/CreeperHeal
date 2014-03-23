package com.nitnelave.CreeperHeal.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Entity;

import com.nitnelave.CreeperHeal.events.CHExplosionRecordEvent;

/**
 * A utility class for common tasks used in several places.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperUtils
{

    public static CHExplosionRecordEvent.ExplosionReason getReason(Entity e)
    {
        if (e == null)
            return CHExplosionRecordEvent.ExplosionReason.OTHER;
        switch (e.getType())
        {
        case CREEPER:
            return CHExplosionRecordEvent.ExplosionReason.CREEPER;
        case ENDER_DRAGON:
            return CHExplosionRecordEvent.ExplosionReason.DRAGON;
        case FIREBALL:
            return CHExplosionRecordEvent.ExplosionReason.GHAST;
        case PRIMED_TNT:
        case MINECART_TNT:
            return CHExplosionRecordEvent.ExplosionReason.TNT;
        default:
            return CHExplosionRecordEvent.ExplosionReason.OTHER;
        }
    }


    /**
     * Concatenate two arrays.
     * 
     * @param <T>
     *            The type of the array elements.
     * @param first
     *            The first array.
     * @param second
     *            The second array
     * @return An array containing the elements of both arrays in the correct
     *         order.
     */
    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Create a final HashSet from a collection of elements.
     * 
     * @param <T>
     *            The type of the set elements.
     * @param elements
     *            The elements to be added in the HashSet.
     * @return An unmodifiable HashSet containing the elements.
     */
    public static <T> Set<T> createFinalHashSet(T... elements)
    {
        Set<T> set = new HashSet<T>(elements.length);

        for (T element : elements)
            set.add(element);

        return Collections.unmodifiableSet(set);
    }

}
