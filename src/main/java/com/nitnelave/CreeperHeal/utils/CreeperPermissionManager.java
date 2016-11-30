package com.nitnelave.CreeperHeal.utils;

import org.bukkit.entity.Player;

/**
 * Handler for permissions.
 * 
 * @author nitnelave
 * 
 */
public class CreeperPermissionManager
{
    /**
     * Check if a player has certain permissions.
     * 
     * @param player
     *            The player.
     * @param warning
     *            Whether this is a warning permission (i.e. ops shouldn't have
     *            it by default).
     * @param nodes
     *            The permissions to check.
     * @return Whether the player has any of the permissions listed in nodes.
     */
    public static boolean checkPermissions(Player player, boolean warning, String... nodes)
    {
        if (!warning && player.isOp())
            return true;
        if (!warning && player.hasPermission("CreeperHeal.*"))
            return true;
        for (String node : nodes)
            if (player.hasPermission("CreeperHeal." + node))
                return true;
        return player.hasPermission("CreeperHeal.*");
    }
}
