package com.nitnelave.CreeperHeal;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.listeners.CreatureSpawnListener;
import com.nitnelave.CreeperHeal.listeners.CreeperBlockListener;
import com.nitnelave.CreeperHeal.listeners.CreeperListener;
import com.nitnelave.CreeperHeal.listeners.FancyListener;
import com.nitnelave.CreeperHeal.utils.CreeperLog;

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

    @Override
    public void onEnable () {

        instance = this;

        registerEvents ();

        CreeperLog.logInfo ("CreeperHeal v" + getDescription ().getVersion () + " enabled", 0);
    }

    private void registerEvents () {
        PluginManager pm = getServer ().getPluginManager ();

        pm.registerEvents (new CreeperListener (), this);
        pm.registerEvents (new CreeperBlockListener (), this);
        if (CreeperConfig.debug)
            pm.registerEvents (new CreatureSpawnListener (), this);

        if (!(CreeperConfig.lightweightMode))
            pm.registerEvents (new FancyListener (), this);

        if (CreeperConfig.grief)
            pm.registerEvents (new GriefListener (), this);
    }

    @Override
    public void onDisable () {
        for (WorldConfig w : CreeperConfig.world_config.values ())
        {
            ExplodedBlockManager.forceReplace (w); //replace blocks still in memory, so they are not lost
            BurntBlockManager.forceReplaceBurnt (w); //same for burnt_blocks
        }
        CreeperLog.logInfo ("CreeperHeal Disabled", 0);
    }

    public static CreeperHeal getInstance () {
        return instance;
    }

}