package com.nitnelave.CreeperHeal;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.command.CreeperCommandManager;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.listeners.BlockFallListener;
import com.nitnelave.CreeperHeal.listeners.BlockIgniteListener;
import com.nitnelave.CreeperHeal.listeners.CreeperBlockListener;
import com.nitnelave.CreeperHeal.listeners.CreeperListener;
import com.nitnelave.CreeperHeal.listeners.GriefListener;
import com.nitnelave.CreeperHeal.listeners.LeavesListener;
import com.nitnelave.CreeperHeal.listeners.RailsUpdateListener;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.MetricsLite;

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

    /*
     * Register the listeners.
     */
    private void registerEvents()
    {
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

    /**
     * Register grief-related events.
     */
    public static void registerGriefEvents()
    {
        if (!griefRegistered)
        {
            Bukkit.getServer().getPluginManager().registerEvents(new GriefListener(), getInstance());
            griefRegistered = true;
        }
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