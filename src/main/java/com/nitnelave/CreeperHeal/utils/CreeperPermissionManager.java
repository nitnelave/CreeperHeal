package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

/**
 * Handler for permissions.
 * 
 * @author nitnelave
 * 
 */
public class CreeperPermissionManager {
    private static boolean bPerms = false;

    /**
     * Constructor, to initiate contact with the permission plugins.
     */
    static
    {
        PluginManager pm = Bukkit.getServer ().getPluginManager ();

        Plugin bPermissions = pm.getPlugin ("bPermissions");
        if (bPermissions != null)
        {
            bPerms = true;
            CreeperLog.logInfo ("Successfully hooked in bPermissions", 0);
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
    public static boolean checkPermissions (Player player, boolean warning, String... nodes) { // check permission for a given node for a given
        // player
        if (bPerms)
        {
            if (!warning && ApiLayer.hasPermission (player.getWorld ().getName (), CalculableType.USER, player.getName (), "CreeperHeal.*"))
                return true;

            for (String node : nodes)
                if (ApiLayer.hasPermission (player.getWorld ().getName (), CalculableType.USER, player.getName (), "CreeperHeal." + node))
                    return true;
            if (CreeperConfig.getBool (CfgVal.OP_ENFORCE) && !warning)
                return player.isOp ();

        }
        else
        {
            if (!warning && player.hasPermission ("CreeperHeal.*"))
                return true;
            for (String node : nodes)
                if (player.hasPermission ("CreeperHeal." + node))
                    return true;
            if (player.hasPermission ("CreeperHeal.*"))
                return true;
            if (CreeperConfig.getBool (CfgVal.OP_ENFORCE) && !warning)
                return player.isOp ();
        }
        return false;
    }

}
