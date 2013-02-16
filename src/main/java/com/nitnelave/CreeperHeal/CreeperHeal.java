package com.nitnelave.CreeperHeal;

import java.lang.reflect.Field;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.command.CreeperCommand;
import com.nitnelave.CreeperHeal.command.CreeperCommandManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.listeners.CreatureSpawnListener;
import com.nitnelave.CreeperHeal.listeners.CreeperBlockListener;
import com.nitnelave.CreeperHeal.listeners.CreeperListener;
import com.nitnelave.CreeperHeal.listeners.FancyListener;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;



public class CreeperHeal extends JavaPlugin {


    private static CreeperHeal instance;

    @Override
    public void onEnable() {

        instance = this;



        new CreeperConfig(this);

        new CreeperLog (this);

        new CreeperMessenger (this);


        registerCommands ();

        /*
         * Recurrent tasks
         */


        PluginManager pm = getServer().getPluginManager();

        PluginHandler.init();
        /**
         * Listeners
         */

        pm.registerEvents(new CreeperListener(), this);
        pm.registerEvents(new CreeperBlockListener(), this);
        if (CreeperConfig.debug)
            pm.registerEvents(new CreatureSpawnListener(), this);

        if(!(CreeperConfig.lightweightMode))
            pm.registerEvents(new FancyListener(), this);

        CreeperMessenger.populateWarnList ();

        logInfo("CreeperHeal v" + getDescription().getVersion() + " enabled", 0);
    }


    private void registerCommands () {
        CommandMap commandMap = null;
        try
        {
            Field field = SimplePluginManager.class.getDeclaredField ("commandMap");
            field.setAccessible (true);
            commandMap = (CommandMap) (field.get (getServer ().getPluginManager ()));
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace ();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace ();
        }

        String[] aliases = {"CreeperHeal", CreeperConfig.alias};
        CreeperCommand com = new CreeperCommand (aliases, "", "", new CreeperCommandManager ());

        if (commandMap != null)
            commandMap.register ("_", com);

    }



    @Override
    public void onDisable() {
        for(WorldConfig w : CreeperConfig.world_config.values()) {
            ExplodedBlockManager.forceReplace (w); //replace blocks still in memory, so they are not lost
            BurntBlockManager.forceReplaceBurnt (w); //same for burnt_blocks
        }
        logInfo("CreeperHeal Disabled", 0);
    }



    public static void logInfo(String msg, int level) {        //logs a message, according to the log_level
        CreeperLog.logInfo(msg, level);
    }





    public static CreeperHeal getInstance () {
        return instance;
    }

}