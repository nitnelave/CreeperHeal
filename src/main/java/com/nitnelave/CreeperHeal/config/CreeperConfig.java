package com.nitnelave.CreeperHeal.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.BlockManager;

/**
 * Configuration management class.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperConfig {

    /**
     * Config settings
     */

    public static int waitBeforeHeal, logLevel = -42, blockPerBlockInterval, waitBeforeHealBurnt, dropChance, distanceNear, obsidianChance, obsidianRadius,
            waitBeforeBurnAgain;
    public static boolean dropReplacedBlocks, blockPerBlock, teleportOnSuffocate, dropDestroyedBlocks, crackDestroyedBricks, lockette, replaceAllChests,
            replaceProtectedChests, overwriteBlocks, preventBlockFall, lightweightMode, opEnforce, logWarnings, preventChainReaction, explodeObsidian, debug,
            grief;

    public static String alias;
    public static double configVersion;

    public static Map<String, WorldConfig> world_config = Collections.synchronizedMap (new HashMap<String, WorldConfig> ());
    protected static YamlConfiguration advancedFile;

    protected static YamlConfiguration configFile;
    protected static final Logger log = Logger.getLogger ("Minecraft");

    protected static File yml, advanced;

    static
    {
        yml = new File (getDataFolder () + "/config.yml");
        advanced = new File (getDataFolder () + "/advanced.yml");
        configFile = new YamlConfiguration ();
        advancedFile = new YamlConfiguration ();

        if (!new File (getDataFolder ().toString ()).exists ())
            new File (getDataFolder ().toString ()).mkdir ();

        if (!yml.exists ())
        {
            log.warning ("[CreeperHeal] Config file not found, creating default.");
            copyJarConfig (yml, "config.yml"); //write the config with the default values.
        }

        load ();
        write ();

    }

    /**
     * Load/reload the main and advanced configuration.
     */
    public static void load () {
        try
        {
            configFile.load (new File (getDataFolder () + "/config.yml"));
        } catch (FileNotFoundException e1)
        {
            e1.printStackTrace ();
        } catch (IOException e1)
        {
            e1.printStackTrace ();
        } catch (InvalidConfigurationException e1)
        {
            e1.printStackTrace ();
        }

        configVersion = configFile.getDouble ("config-version", 4);
        if (configVersion == 4)
            ConfigUpdater.from4 ();
        else if (configVersion == 5)
        {

            blockPerBlockInterval = getInt (configFile, "replacement.block-per-block.interval", 20);
            waitBeforeHeal = getInt (configFile, "replacement.wait-before-heal.explosions", 60); //tries to read the value directly from the config
            blockPerBlock = getBoolean (configFile, "replacement.block-per-block", true);
            waitBeforeHealBurnt = getInt (configFile, "replacement.wait-before-heal.fire", 45);
            crackDestroyedBricks = getBoolean (configFile, "replacement.crack-destroyed-bricks", false);
            replaceAllChests = getBoolean (configFile, "replacement.ignore-chests.all", false);
            replaceProtectedChests = getBoolean (configFile, "replacement.ignore-chests.protected", false);
            logLevel = getInt (configFile, "advanced.verbose-level", 1);
            dropReplacedBlocks = getBoolean (configFile, "advanced.replacement-conflict.drop-overwritten-blocks", true);
            teleportOnSuffocate = getBoolean (configFile, "advanced.teleport-when-buried", true);
            dropDestroyedBlocks = getBoolean (configFile, "advanced.drop-destroyed-blocks.enabled", true);
            dropChance = getInt (configFile, "advanced.drop-destroyed-blocks.chance", 100);
            opEnforce = getBoolean (configFile, "advanced.op-have-all-permissions", true);
            overwriteBlocks = getBoolean (configFile, "advanced.replacement-conflict.overwrite", true);
            preventBlockFall = getBoolean (configFile, "advanced.prevent-block-fall", true);
            distanceNear = getInt (configFile, "advanced.distance-near", 20);
            lightweightMode = getBoolean (configFile, "advanced.lightweight-mode", false);
            alias = configFile.getString ("advanced.command-alias", "ch");
            logWarnings = getBoolean (configFile, "advanced.log-warnings", true);
            preventChainReaction = getBoolean (configFile, "advanced.prevent-chain-reaction", false);
            explodeObsidian = getBoolean (configFile, "advanced.obsidian.explode", false);
            obsidianRadius = getInt (configFile, "advanced.obsidian.radius", 5);
            obsidianChance = getInt (configFile, "advanced.obsidian.chance", 20);
            debug = getBoolean (configFile, "advanced.debug-messages", false);
            waitBeforeBurnAgain = 240;
            configVersion = 6;
            set (configFile, "config-version", 6);
            set (configFile, "replacement", null);
            set (configFile, "advanced", null);

            if (!advanced.exists ())
            {
                log.warning ("[CreeperHeal] Migrating to config v 6");
                copyJarConfig (advanced, "advanced.yml"); //write the config with the default values.
            }
            write ();

        }
        else if (configVersion >= 6)
        {

            blockPerBlockInterval = getInt (configFile, "block-per-block.interval", 20);
            waitBeforeHeal = getInt (configFile, "wait-before-heal.explosions", 60); //tries to read the value directly from the config
            blockPerBlock = getBoolean (configFile, "block-per-block.enabled", true);
            waitBeforeHealBurnt = getInt (configFile, "wait-before-heal.fire", 45);
            crackDestroyedBricks = getBoolean (configFile, "crack-destroyed-bricks", false);
            replaceAllChests = getBoolean (configFile, "ignore-chests.all", false);
            replaceProtectedChests = getBoolean (configFile, "ignore-chests.protected", false);

            if (!advanced.exists ())
            {
                log.warning ("[CreeperHeal] Advanced config file not found, creating default.");
                copyJarConfig (advanced, "advanced.yml"); //write the config with the default values.
            }

            try
            {
                advancedFile.load (advanced);
            } catch (Exception e)
            {
                log.severe ("Error loading advanced configuration file");
                e.printStackTrace ();
            }
            logLevel = getInt (advancedFile, "verbose-level", 1);
            dropReplacedBlocks = getBoolean (advancedFile, "replacement-conflict.drop-overwritten-blocks", true);
            teleportOnSuffocate = getBoolean (advancedFile, "teleport-when-buried", true);
            dropDestroyedBlocks = getBoolean (advancedFile, "drop-destroyed-blocks.enabled", true);
            dropChance = getInt (advancedFile, "drop-destroyed-blocks.chance", 100);
            opEnforce = getBoolean (advancedFile, "op-have-all-permissions", true);
            overwriteBlocks = getBoolean (advancedFile, "replacement-conflict.overwrite", true);
            preventBlockFall = getBoolean (advancedFile, "prevent-block-fall", true);
            distanceNear = getInt (advancedFile, "distance-near", 20);
            lightweightMode = getBoolean (advancedFile, "lightweight-mode", false);
            alias = advancedFile.getString ("command-alias", "ch");
            logWarnings = getBoolean (advancedFile, "log-warnings", true);
            preventChainReaction = getBoolean (advancedFile, "prevent-chain-reaction", false);
            explodeObsidian = getBoolean (advancedFile, "obsidian.explode", false);
            obsidianRadius = getInt (advancedFile, "obsidian.radius", 5);
            obsidianChance = getInt (advancedFile, "obsidian.chance", 20);
            debug = getBoolean (advancedFile, "debug-messages", false);
            waitBeforeBurnAgain = getInt (advancedFile, "wait-before-burn-again", 240);
            set (configFile, "config-version", 7);
            if (configVersion == 6)
                log.warning ("[CreeperHeal] Migrating to config v 7");
            write ();
        }

        boolean timeRepairs = false;
        world_config.clear ();
        try
        {
            for (World w : Bukkit.getServer ().getWorlds ())
            {
                String name = w.getName ();
                WorldConfig world = new WorldConfig (name, getDataFolder ());
                if (configVersion == 6)
                    world.migrate6to7 ();
                world_config.put (name, world);
                timeRepairs = timeRepairs || world.repairTime > -1;
                grief = grief || world.hasGriefProtection ();
            }
        } catch (Exception e)
        {
            log.severe ("[CreeperHeal] Could not load world configurations");
            log.severe (e.getMessage ());
        }
        configVersion = 7;

        if (timeRepairs)
            BlockManager.scheduleTimeRepairs ();

    }

    protected static boolean getBoolean (YamlConfiguration config, String path, boolean def) { //read a boolean from the config
        boolean tmp;
        try
        {
            tmp = config.getBoolean (path, def);
        } catch (Exception e)
        {
            log.warning ("[CreeperHeal] Wrong value for " + path + " field in file " + config.getName () + ". Defaulting to " + Boolean.toString (def));
            tmp = def;
        }
        return tmp;
    }

    protected static int getInt (YamlConfiguration config, String path, int def) {
        int tmp;
        try
        {
            tmp = config.getInt (path, def);
        } catch (Exception e)
        {
            log.warning ("[CreeperHeal] Wrong value for " + path + " field in file " + config.getName () + ". Defaulting to " + Integer.toString (def));
            tmp = def;
        }
        return tmp;
    }

    /**
     * Save the main and advanced configuration to the file.
     */
    public static void write () {
        if (!yml.exists ())
        {
            new File (getDataFolder ().toString ()).mkdir ();
            try
            {
                yml.createNewFile ();
            } catch (IOException ex)
            {
                log.warning ("[CreeperHeal] Cannot create file " + yml.getPath ());
            }
        }

        if (!advanced.exists ())
        {
            new File (getDataFolder ().toString ()).mkdir ();
            try
            {
                advanced.createNewFile ();
            } catch (IOException ex)
            {
                log.warning ("[CreeperHeal] Cannot create file " + advanced.getPath ());
            }
        }

        set (configFile, "wait-before-heal.explosions", waitBeforeHeal);
        set (configFile, "wait-before-heal.fire", waitBeforeHealBurnt);
        set (configFile, "block-per-block.enabled", blockPerBlock);
        set (configFile, "block-per-block.interval", blockPerBlockInterval);
        set (configFile, "ignore-chests.all", replaceAllChests);
        set (configFile, "ignore-chests.protected", replaceProtectedChests);
        set (configFile, "crack-destroyed-bricks", crackDestroyedBricks);
        set (advancedFile, "replacement-conflict.overwrite", overwriteBlocks);
        set (advancedFile, "replacement-conflict.drop-overwritten-blocks", dropReplacedBlocks);
        set (advancedFile, "drop-destroyed-blocks.enabled", dropDestroyedBlocks);
        set (advancedFile, "drop-destroyed-blocks.chance", dropChance);
        set (advancedFile, "teleport-when-buried", teleportOnSuffocate);
        set (advancedFile, "verbose-level", logLevel);
        set (advancedFile, "op-have-all-permissions", opEnforce);
        set (advancedFile, "prevent-block-fall", preventBlockFall);
        set (advancedFile, "distance-near", distanceNear);
        set (advancedFile, "lightweight-mode", lightweightMode);
        set (advancedFile, "command-alias", alias);
        set (advancedFile, "prevent-chain-reaction", preventChainReaction);
        set (advancedFile, "log-warnings", logWarnings);
        set (configFile, "config-version", configVersion);
        set (advancedFile, "obsidian.explode", explodeObsidian);
        set (advancedFile, "obsidian.radius", obsidianRadius);
        set (advancedFile, "obsidian.chance", obsidianChance);
        set (advancedFile, "debug-messages", debug);
        set (advancedFile, "wait-before-burn-again", waitBeforeBurnAgain);
        ConfigUpdater.removeOldWorldConfig ();

        try
        {
            for (WorldConfig w : world_config.values ())
                w.save ();
            configFile.save (yml);
            advancedFile.save (advanced);
        } catch (IOException e)
        {
            e.printStackTrace ();
        }

    }

    protected static void set (YamlConfiguration config, String string, Object o) {
        config.set (string, o);
    }

    /**
     * Load a world configuration file into memory the first time, and return
     * the configuration
     * 
     * @param world
     *            The world to load.
     * @return The world configuration file.
     */
    public static WorldConfig loadWorld (World world) {

        String name = world.getName ();
        WorldConfig returnValue = world_config.get (name);
        if (returnValue == null)
            try
            {
                returnValue = new WorldConfig (name, getDataFolder ());
                world_config.put (name, returnValue);
            } catch (Exception e)
            {
                log.severe ("[CreeperHeal] Could not load configuration for world : " + name);
                log.severe (e.getMessage ());
            }
        return returnValue;
    }

    private static File getDataFolder () {
        return CreeperHeal.getInstance ().getDataFolder ();
    }

    protected static void copyJarConfig (File file, String resource) {
        OutputStream outStream = null;
        try
        {
            file.getParentFile ().mkdirs ();
            file.createNewFile ();
            InputStream templateIn = CreeperHeal.getInstance ().getResource (resource);
            outStream = new FileOutputStream (file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = templateIn.read (bytes)) != -1)
                outStream.write (bytes, 0, read);

            templateIn.close ();
            outStream.flush ();
            outStream.close ();
            log.info ("[CreeperHeal] Default config created");

        } catch (Exception e)
        {
            log.warning ("[CreeperHeal] Failed to create file: " + file.getName ());
            log.warning (e.getMessage ());
            if (outStream != null)
                try
                {
                    outStream.flush ();
                    outStream.close ();
                } catch (IOException e1)
                {
                    e1.printStackTrace ();
                }
        }
    }

}
