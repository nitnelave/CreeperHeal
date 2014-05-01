package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

/**
 * Handler for permissions.
 * 
 * @author nitnelave
 * 
 */
public class CreeperPermissionManager
{
    private static boolean bPerms = false;

    /**
     * Constructor, to initiate contact with the permission plugins.
     */
    static
    {
        PluginManager pm = Bukkit.getServer().getPluginManager();

        Plugin bPermissions = pm.getPlugin("bPermissions");
        if (bPermissions != null)
        {
            bPerms = true;
            CreeperLog.logInfo("Successfully hooked in bPermissions", 0);
        }
    }

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
        if (bPerms)
        {
            if (!warning
                && ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), "CreeperHeal.*"))
                return true;

            for (String node : nodes)
                if (ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), "CreeperHeal."
                                                                                                               + node))
                    return true;

        }
        else
        {
            if (!warning && player.hasPermission("CreeperHeal.*"))
                return true;
            for (String node : nodes)
                if (player.hasPermission("CreeperHeal." + node))
                    return true;
            if (player.hasPermission("CreeperHeal.*"))
                return true;
        }
        return false;
    }

}
