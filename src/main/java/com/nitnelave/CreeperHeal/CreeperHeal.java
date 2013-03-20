package com.nitnelave.CreeperHeal;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.command.CreeperCommandManager;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.listeners.CreeperBlockListener;
import com.nitnelave.CreeperHeal.listeners.CreeperListener;
import com.nitnelave.CreeperHeal.listeners.FancyListener;
import com.nitnelave.CreeperHeal.listeners.GriefListener;

/**
 * The main class of the CreeperHeal plugin. The main aim of this plugin is to
 * replace the damage created by Creepers or TNT, but in a natural way, one
 * block at a time, over time.
 * 
 * @author nitnelave
 * 
 */
public class CreeperHeal extends JavaPlugin {

    private static CreeperHeal instance;

    /*
     * Store whether the grief-related events have already been registered.
     */
    private static boolean griefRegistered = false;

    /*
     * (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable () {

        instance = this;
        CreeperCommandManager.registerCommands ();
        registerEvents ();
    }

    /*
     * Register the listeners.
     */
    private void registerEvents () {
        PluginManager pm = getServer ().getPluginManager ();

        pm.registerEvents (new CreeperListener (), this);
        pm.registerEvents (new CreeperBlockListener (), this);

        if (CreeperConfig.getBool (CfgVal.LEAVES_VINES))
            pm.registerEvents (new FancyListener (), this);
    }

    /*
     * (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     */
    @Override
    public void onDisable () {
        for (WorldConfig w : CreeperConfig.getWorlds ())
        {
            ExplodedBlockManager.forceReplace (w); //replace blocks still in memory, so they are not lost
            BurntBlockManager.forceReplaceBurnt (w); //same for burnt_blocks
        }
    }

    /**
     * Get the instance of the CreeperHeal plugin.
     * 
     * @return The instance of CreeperHeal.
     */
    public static CreeperHeal getInstance () {
        return instance;
    }

    /**
     * Register grief-related events.
     */
    public static void registerGriefEvents () {
        if (!griefRegistered)
        {
            Bukkit.getServer ().getPluginManager ().registerEvents (new GriefListener (), getInstance ());
            griefRegistered = true;
        }
    }

    /**
     * Gets the plugin data folder.
     * 
     * @return The plugin data folder
     */
    public static File getCHFolder () {
        return getInstance ().getDataFolder ();
    }

}