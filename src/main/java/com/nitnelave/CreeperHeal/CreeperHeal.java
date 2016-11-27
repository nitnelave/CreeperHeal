package com.nitnelave.CreeperHeal;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.command.CreeperCommandManager;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.listeners.*;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * The main class of the CreeperHeal plugin. The main aim of this plugin is to
 * replace the damage created by Creepers or TNT, but in a natural way, one
 * block at a time, over time.
 * 
 * @author nitnelave
 * 
 */
public class CreeperHeal extends JavaPlugin
{

    private static CreeperHeal instance;

    /*
     * Store whether the grief-related events have already been registered.
     */
    private static boolean griefRegistered = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable()
    {

        instance = this;
        CreeperCommandManager.registerCommands();
        registerEvents();
        try
        {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e)
        {
            CreeperLog.warning("Could not submit data to MC-Stats");
        }
    }

    /**
     * Register grief-related events.
     */
    public static void registerGriefEvents()
    {
        if (!griefRegistered)
        {
            CreeperLog.debug("Registering Grief events");
            Bukkit.getServer().getPluginManager().registerEvents(new GriefListener(), getInstance());
            griefRegistered = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable()
    {
        ExplodedBlockManager.forceReplace(); //replace blocks still in memory, so they are not lost
        BurntBlockManager.forceReplaceBurnt(); //same for burnt_blocks
    }

    /**
     * Get the instance of the CreeperHeal plugin.
     * 
     * @return The instance of CreeperHeal.
     */
    public static CreeperHeal getInstance()
    {
        return instance;
    }

    /*
     * Register the listeners.
     */
    private void registerEvents()
    {
        CreeperLog.debug("Registering events");
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new CreeperListener(), this);
        pm.registerEvents(new CreeperBlockListener(), this);

        if (CreeperConfig.getBool(CfgVal.LEAVES_VINES))
            pm.registerEvents(new LeavesListener(), this);

        if (CreeperConfig.getBool(CfgVal.PREVENT_BLOCK_FALL))
            pm.registerEvents(new BlockFallListener(), this);

        if (CreeperConfig.getBool(CfgVal.RAIL_REPLACEMENT))
            pm.registerEvents(new RailsUpdateListener(), this);

        if (CreeperConfig.getInt(CfgVal.WAIT_BEFORE_BURN_AGAIN) > 0)
            pm.registerEvents(new BlockIgniteListener(), this);

        ExplodedBlockManager.init();
        BurntBlockManager.init();
        CreeperLog.debug("Events registered");
    }

    /**
     * Gets the plugin data folder.
     * 
     * @return The plugin data folder
     */
    public static File getCHFolder()
    {
        return getInstance().getDataFolder();
    }

}