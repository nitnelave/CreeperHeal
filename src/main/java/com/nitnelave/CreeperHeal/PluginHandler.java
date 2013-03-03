package com.nitnelave.CreeperHeal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.yi.acru.bukkit.Lockette.Lockette;

import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.FactionHandler;
import com.nitnelave.CreeperTrap.CreeperTrapCommands;

public class PluginHandler {

    private static MobArenaHandler maHandler = null;
    private static LWC lwc = null;
    private static boolean playerHeads = false, creeperTrap = false;

    static
    {
        Plugin lwcp = detectPlugin ("LWC");
        if (lwcp != null)
            lwc = ((LWCPlugin) lwcp).getLWC ();

        CreeperConfig.lockette = detectPlugin ("Lockette") != null;

        if (detectPlugin ("MobArena") != null)
            maHandler = new MobArenaHandler ();

        FactionHandler.setFactionsEnabled (detectPlugin ("Factions") != null);

        playerHeads = detectPlugin ("PlayerHeads") != null;
    }

    /*
     * Check if the plugin is active on the server, and if so, display a message
     * stating that it was successfully detected.
     */
    private static Plugin detectPlugin (String name) {
        Plugin plugin = Bukkit.getServer ().getPluginManager ().getPlugin (name);
        if (plugin != null)
            CreeperLog.logInfo ("Successfully hooked into " + name, 1);
        return plugin;
    }

    /**
     * Get whether the block is protected by LWC or Lockette.
     * 
     * @param block
     *            The block to check.
     * @return Whether the block is protected.
     */
    public static boolean isProtected (Block block) {
        if (lwc != null)
            return lwc.findProtection (block) != null;
        else if (CreeperConfig.lockette)
            return Lockette.isProtected (block);
        else
            return false;
    }

    /**
     * Get whether the block is inside a mob arena.
     * 
     * @param location
     *            The location of the block.
     * @return Whether the block is inside an arena.
     */
    public static boolean isInArena (Location location) {
        if (maHandler != null)
            if (maHandler.inRegion (location))
                return true; //Explosion inside a mob arena
        return false;
    }

    /**
     * Get whether the plugin PlayerHeads is active on the server.
     * 
     * @return True if PlayerHeads is activated.
     */
    public static boolean isPlayerHeadsActivated () {
        return playerHeads;
    }

    /**
     * Meant only for CreeperTrap to call, on plugin startup. Register
     * CreeperTrap as enabled on the server.
     */
    public static void setCreeperTrapEnabled () {
        creeperTrap = true;
    }

    /**
     * Get whether CreeperTrap is enabled or not.
     * 
     * @return True if CreeperTrap is enabled.
     */
    public static boolean isCreeperTrapEnabled () {
        return creeperTrap;
    }

    /**
     * Forward a command to the CreeperTrap plugin.
     * 
     * @param sender
     *            The command's sender.
     * @param args
     *            The command's arguments
     * @return False in case of a syntax error in the command.
     */
    public static boolean trapCommand (CommandSender sender, String[] args) {
        if (!isCreeperTrapEnabled ())
        {
            sender.sendMessage ("You have to install the CreeperTrap plugin to use traps");
            return true;
        }
        return CreeperTrapCommands.onCommand (sender, args);
    }

}
