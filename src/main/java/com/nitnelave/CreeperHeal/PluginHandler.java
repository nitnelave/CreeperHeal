package com.nitnelave.CreeperHeal;

import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

/**
 * This class handles all the interaction with external plugins.
 * 
 * @author nitnelave
 * 
 */
public class PluginHandler
{

    private static MobArenaHandler maHandler = null;
    private static LWC lwc = null;

    static
    {
        Plugin lwcp = detectPlugin("LWC");
        if (lwcp != null)
            lwc = ((LWCPlugin) lwcp).getLWC();

        if (detectPlugin("MobArena") != null)
            maHandler = new MobArenaHandler();

    }

    /*
     * Check if the plugin is active on the server, and if so, display a message
     * stating that it was successfully detected.
     */
    private static Plugin detectPlugin(String name)
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
        if (plugin != null)
            CreeperLog.logInfo("Successfully hooked into " + name, 1);
        return plugin;
    }

    /**
     * Get whether the block is protected by LWC or Lockette.
     * 
     * @param block
     *            The block to check.
     * @return Whether the block is protected.
     */
    public static boolean isProtected(Block block)
    {
        return lwc != null && lwc.findProtection(block) != null;
    }

    /**
     * Get whether the block is inside a mob arena.
     * 
     * @param location
     *            The location of the block.
     * @return Whether the block is inside an arena.
     */
    public static boolean isInArena(Location location)
    {
        return maHandler != null && maHandler.inRegion(location);
    }

    /**
     * Checks if is Factions enabled.
     *
     * @return true, if Factions is enabled
     */
    public static boolean isFactionsEnabled()
    {
        return detectPlugin("Factions") != null;
    }

}
