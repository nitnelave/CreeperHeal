package com.nitnelave.CreeperHeal.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.FileUtils;

/**
 * Configuration management class.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperConfig
{

    private static final int CONFIG_VERSION = 12;
    private static final File CONFIG_FILE = new File(CreeperHeal.getCHFolder() + "/config.yml"),
                    ADVANCED_FILE = new File(CreeperHeal.getCHFolder()
                                             + "/advanced.yml");
    private static final Logger LOG = Logger.getLogger("Minecraft");
    private static final Map<String, WorldConfig> world_config = new HashMap<String, WorldConfig>();
    private static final Map<String, ConfigValue<Boolean>> booleans = new HashMap<String, ConfigValue<Boolean>>();
    private static final Map<String, ConfigValue<Integer>> integers = new HashMap<String, ConfigValue<Integer>>();
    private static final YamlConfiguration config = new YamlConfiguration(),
                    advanced = new YamlConfiguration();

    private static ConfigValue<String> alias, soundName;
    private static Sound sound = null;
    private static int configVersion = CONFIG_VERSION;

    static
    {
        fillMaps();
        load();
    }

    /*
     * Put the config values in the maps, with the default values.
     */
    private static void fillMaps()
    {
        booleans.clear();
        integers.clear();
        for (CfgVal v : CfgVal.values())
            if (v.getDefaultValue() instanceof Boolean)
                booleans.put(v.getKey(), new BooleanConfigValue(v, getFile(v)));
            else if (v.getDefaultValue() instanceof Integer)
                integers.put(v.getKey(), new IntegerConfigValue(v, getFile(v)));
            else if (v == CfgVal.ALIAS)
                alias = new StringConfigValue(v, getFile(v));
            else if (v == CfgVal.SOUND_NAME)
            {
                soundName = new StringConfigValue(v, getFile(v));
                loadSound();
            }
            else
                CreeperLog.warning("Unknown config value : " + v.toString());

    }

    private static void loadSound()
    {
        CreeperLog.debug("Loading sound");
        try
        {
            sound = Sound.valueOf(soundName.getValue());
        } catch (IllegalArgumentException e)
        {
            sound = null;
        }
    }

    private static YamlConfiguration getFile(CfgVal v)
    {
        return v.isAdvanced() ? advanced : config;
    }

    /**
     * Get the boolean value associated with the CfgVal.
     * 
     * @param val
     *            The config key.
     * @return The boolean value.
     */
    public static boolean getBool(CfgVal val)
    {
        ConfigValue<Boolean> v = booleans.get(val.getKey());
        if (v == null)
            throw new NullPointerException("Missing config value : " + val.getKey());
        return v.getValue();
    }

    /**
     * Get the int value associated with the CfgVal.
     * 
     * @param val
     *            The config key.
     * @return The int value.
     */
    public static int getInt(CfgVal val)
    {
        ConfigValue<Integer> v = integers.get(val.getKey());
        if (v == null)
            throw new NullPointerException("Missing config value : " + val.getKey());
        return v.getValue();
    }

    /**
     * Set the boolean value associated with the key.
     * 
     * @param val
     *            The key
     * @param value
     *            The value.
     */
    public static void setBool(CfgVal val, boolean value)
    {
        ConfigValue<Boolean> v = booleans.get(val.getKey());
        if (v == null)
            throw new NullPointerException("Unknown config key path : " + val.getKey());
        v.setValue(value);
    }

    /**
     * Set the int value associated with the key.
     * 
     * @param val
     *            The key
     * @param value
     *            The value.
     */
    public static void setInt(CfgVal val, int value)
    {
        ConfigValue<Integer> v = integers.get(val.getKey());
        if (v == null)
            throw new NullPointerException("Unknown config key path : " + val.getKey());
        v.setValue(value);
    }

    /*
     * Load a file, with all the checks that go with this.
     */
    private static void loadFile(YamlConfiguration conf, File f)
    {
        try
        {
            conf.load(f);
        } catch (FileNotFoundException e1)
        {
            FileUtils.copyJarConfig(f);
            try
            {
                conf.load(f);
            } catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        } catch (IOException e1)
        {
            e1.printStackTrace();
            return;
        } catch (InvalidConfigurationException e1)
        {
            CreeperLog.warning("Invalid configuration : " + f.getName()
                               + " is not a valid YAML file.");
            return;
        }
    }

    /**
     * Load/reload the main and advanced configuration.
     */
    public static void load()
    {
        if (!CONFIG_FILE.exists())
        {
            FileUtils.copyJarConfig(CONFIG_FILE);
            if (!ADVANCED_FILE.exists())
                FileUtils.copyJarConfig(ADVANCED_FILE);
        }
        else
        {
            loadFile(config, CONFIG_FILE);
            if (!ADVANCED_FILE.exists())
                FileUtils.copyJarConfig(ADVANCED_FILE);
            loadFile(advanced, ADVANCED_FILE);
            configVersion = advanced.getInt("config-version", 4);
            if (configVersion < 8)
                ConfigUpdater.importFrom(configVersion);
            else
            {
                for (ConfigValue<Boolean> v : booleans.values())
                    v.load();
                CreeperLog.setDebug(getBool(CfgVal.DEBUG));
                for (ConfigValue<Integer> v : integers.values())
                    v.load();
                alias.load();
                soundName.load();
                loadSound();
                if (configVersion == 8)
                    loadLightWeight();
            }
            advanced.set("config-version", CONFIG_VERSION);
            configVersion = CONFIG_VERSION;
            write();
        }

        loadWorlds();
    }

    private static void loadLightWeight()
    {
        if (!advanced.getBoolean("lightweight-mode", false))
        {
            CreeperConfig.setBool(CfgVal.RAIL_REPLACEMENT, false);
            CreeperConfig.setBool(CfgVal.SUFFOCATING_ANIMALS, false);
            CreeperConfig.setBool(CfgVal.LEAVES_VINES, false);
            CreeperConfig.setBool(CfgVal.SORT_BY_RADIUS, false);
        }

    }

    /*
     * Load every world detected.
     */
    private static void loadWorlds()
    {
        world_config.clear();
        try
        {
            for (World w : Bukkit.getServer().getWorlds())
            {
                WorldConfig world = loadWorld(w.getName());
                world_config.put(w.getName(), world);
            }
        } catch (Exception e)
        {
            CreeperLog.severe("[CreeperHeal] Could not load world configurations");
            CreeperLog.severe(e.getMessage());
        }
    }

    /*
     * Load a world, and return the loaded world.
     */
    private static WorldConfig loadWorld(String name)
    {
        WorldConfig w;
        if (configVersion < 8)
        {
            w = WorldConfigImporter.importFrom(name, configVersion);
            w.save();
        }
        else
        {
            w = new WorldConfig(name);
            w.load();
        }
        if (w.hasGriefProtection())
            CreeperHeal.registerGriefEvents();
        return w;
    }

    /**
     * Save the main and advanced configuration to the file.
     */
    public static void write()
    {
        if (!CONFIG_FILE.exists() && !FileUtils.createNewFile(CONFIG_FILE)
            || !ADVANCED_FILE.exists() && !FileUtils.createNewFile(ADVANCED_FILE))
            return;

        for (ConfigValue<Boolean> v : booleans.values())
            v.write();

        for (ConfigValue<Integer> v : integers.values())
            v.write();

        alias.write();
        soundName.write();
        loadSound();
        advanced.set("config-version", CONFIG_VERSION);

        try
        {
            for (WorldConfig w : world_config.values())
                w.save();
            config.save(CONFIG_FILE);
            advanced.save(ADVANCED_FILE);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Load a world configuration file into memory the first time, and return
     * the configuration.
     * 
     * @param world
     *            The world to load.
     * @return The world configuration file.
     */
    public static WorldConfig getWorld(World world)
    {
        return getWorld(world.getName());
    }

    /**
     * Load a world configuration file into memory the first time, and return
     * the configuration.
     * 
     * @param name
     *            The name of the world to be loaded.
     * @return The world configuration file.
     */
    public static WorldConfig getWorld(String name)
    {
        WorldConfig returnValue = world_config.get(name);
        if (returnValue == null)
            try
            {
                returnValue = loadWorld(name);
                world_config.put(name, returnValue);
            } catch (Exception e)
            {
                LOG.severe("[CreeperHeal] Could not load configuration for world : " + name);
                e.printStackTrace();
            }
        return returnValue;
    }

    protected static void setAlias(String cmdAlias)
    {
        alias.setValue(cmdAlias);
    }

    /**
     * Gets the command alias.
     * 
     * @return The command alias.
     */
    public static String getAlias()
    {
        return alias.getValue();
    }

    /**
     * Gets the list of worlds recorded.
     * 
     * @return The list of worlds.
     */
    public static Collection<WorldConfig> getWorlds()
    {
        return world_config.values();
    }

    protected static void remove(String key, boolean isAdvanced)
    {
        if (isAdvanced)
            advanced.set(key, null);
        else
            config.set(key, null);
    }

    public static Sound getSound()
    {
        return sound;
    }

}
