package com.nitnelave.CreeperHeal.utils;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.DelayReplacement;
import com.nitnelave.CreeperHeal.block.Replaceable;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.events.CHBlockHealEvent;
import com.nitnelave.CreeperHeal.events.CHExplosionRecordEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

        Collections.addAll(set, elements);

        return Collections.unmodifiableSet(set);
    }

    /**
     * Delay the block's replacement until it is possible for it to spawn, or
     * drop it after a reasonable amount of tries.
     *
     * @param block  The block to be replaced later
     * @param reason How was this block replaced
     */
    public static void delayReplacement(Replaceable block, CHBlockHealEvent.CHBlockHealReason reason)
    {
        long delay = CreeperConfig.getInt(CfgVal.BLOCK_PER_BLOCK_INTERVAL);
        DelayReplacement dr = new DelayReplacement(block, 0, reason);
        int id = Bukkit.getServer().getScheduler()
                       .scheduleSyncRepeatingTask(CreeperHeal.getInstance(), dr, delay, delay);
        dr.setId(id);
    }
}
