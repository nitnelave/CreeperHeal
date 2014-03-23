package com.nitnelave.CreeperHeal.utils;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.nitnelave.CreeperHeal.block.CreeperBlock;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Utility class to check if a player is suffocating, and teleport him to
 * safety.
 * 
 * @author nitnelave
 * 
 */
public abstract class Suffocating
{

    /**
     * Check the living entities around the location. If any is suffocating,
     * teleport it to a safe location.
     * 
     * @param loc
     *            The location around which to check.
     */
    public static void checkPlayerOneBlock(Location loc)
    {
        Entity[] play_list = loc.getBlock().getChunk().getEntities();
        for (Entity en : play_list)
            if (en instanceof LivingEntity && loc.distance(en.getLocation()) < 2)
                en.teleport(check_player_suffocate((LivingEntity) en));
    }

    /**
     * Check the players and other animals (except in lightweight mode) to see
     * if they were trapped by the explosion's replacement.
     * 
     * @param loc
     *            The center of the explosion.
     * @param radius
     *            The radius of the explosion.
     */
    public static void checkPlayerExplosion(Location loc, double radius)
    {
        List<? extends Entity> entityList;
        if (CreeperConfig.getBool(CfgVal.SUFFOCATING_ANIMALS))
            entityList = loc.getWorld().getEntities();
        else
            entityList = loc.getWorld().getPlayers();
        for (Entity en : entityList)
            if (en instanceof LivingEntity && loc.distance(en.getLocation()) < radius + 3)
                en.teleport(check_player_suffocate((LivingEntity) en));
    }

    /*
     * Get the location to which an entity should be teleported for safety.
     */
    private static Location check_player_suffocate(LivingEntity en)
    {
        Location loc = en.getLocation();

        if (CreeperBlock.isSolid(loc.getBlock())
            || CreeperBlock.isSolid(loc.getBlock().getRelative(0, 1, 0)))
            for (int k = 1; k + loc.getBlockY() < 127; k++)
            {
                Location l = loc.clone().add(0, k, 0);
                if (check_free(l))
                    return l;

                l.add(k, -k, 0);
                if (check_free_horizontal(l))
                    return l;

                l.add(-2 * k, 0, 0);
                if (check_free_horizontal(l))
                    return l;

                l.add(k, 0, k);
                if (check_free_horizontal(l))
                    return l;

                l.add(0, 0, -2 * k);
                if (check_free_horizontal(l))
                    return l;
            }
        return loc;
    }

    /*
     * Check if the block at the coordinates, or one above or below is suitable
     * to put a living being.
     */
    private static boolean check_free_horizontal(Location loc)
    {
        loc.add(0, -1, 0);
        for (int k = -1; k < 2; k++)
        {
            loc.add(0, 1, 0);
            if (check_free(loc))
                return true;
        }
        loc.add(0, -1, 0);
        return false;
    }

    /*
     * Get whether the location is suitable ground so a player doesn't
     * suffocate.
     */
    private static boolean check_free(Location loc)
    {
        Block block = loc.getBlock();
        if (!CreeperBlock.isSolid(block) && !CreeperBlock.isSolid(block.getRelative(0, 1, 0))
            && CreeperBlock.isSolid(block.getRelative(0, -1, 0)))
        {
            loc.add(0.5, 0, 0.5);
            return true;
        }
        return false;
    }
}
