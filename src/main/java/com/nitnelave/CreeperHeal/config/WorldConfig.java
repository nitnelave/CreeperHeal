package com.nitnelave.CreeperHeal.config;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.PluginHandler;
import com.nitnelave.CreeperHeal.block.BlockId;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.InventoryHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * World configuration settings. Gathers all the settings for a given world.
 *
 * @author nitnelave
 *
 */
public class WorldConfig
{

    private final HashMap<String, ConfigValue<Boolean>> booleans = new HashMap<String, ConfigValue<Boolean>>();
    private final String name;
    private IntegerConfigValue repairTime, replaceLimit;
    private BlockIdListValue replaceBlackList, griefPlaceList, protectList, replaceWhiteList;
    private final YamlConfiguration config = new YamlConfiguration(),
                    advanced = new YamlConfiguration(), grief = new YamlConfiguration(),
                    replacement = new YamlConfiguration();
    private final File worldFolder, configFile, advancedFile, griefFile, replaceFile;

    /**
     * Main constructor. Load the config from the file, or create a default one
     * if needed.
     *
     * @param name
     *            The world's name.
     */
    public WorldConfig(String name)
    {
        this.name = name;
        worldFolder = new File(CreeperHeal.getCHFolder().getPath() + "/" + name);
        configFile = new File(worldFolder + "/config.yml");
        advancedFile = new File(worldFolder + "/advanced.yml");
        griefFile = new File(worldFolder + "/grief.yml");
        replaceFile = new File(worldFolder + "/replace.yml");
        fillMaps();
    }

    private void fillMaps()
    {
        for (WCfgVal v : WCfgVal.values())
            if (v.getDefaultValue() instanceof Boolean)
                booleans.put(v.getKey(), new BooleanConfigValue(v, getFile(v)));
            else
                switch (v)
                {
                case REPAIR_TIME:
                    repairTime = new IntegerConfigValue(v, getFile(v));
                    break;
                case REPLACE_LIMIT:
                    replaceLimit = new IntegerConfigValue(v, getFile(v));
                    break;
                case REPLACE_BLACK_LIST:
                    replaceBlackList = new BlockIdListValue(v, getFile(v));
                    break;
                case REPLACE_WHITE_LIST:
                    replaceWhiteList = new BlockIdListValue(v, getFile(v));
                    break;
                case GRIEF_PLACE_LIST:
                    griefPlaceList = new BlockIdListValue(v, getFile(v));
                    break;
                case PROTECTED_LIST:
                    protectList = new BlockIdListValue(v, getFile(v));
                    break;
                default:
                    CreeperLog.warning("Unknown config value : " + v.toString());
                }
    }

    private YamlConfiguration getFile(WCfgVal v)
    {
        switch (v.getFile())
        {
        case ADVANCED:
            return advanced;
        case CONFIG:
            return config;
        case GRIEF:
            return grief;
        }
        return null;
    }

    /**
     * Get the world's name.
     *
     * @return The world's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get whether the world has timed repairs enabled.
     *
     * @return Whether the world has timed repairs enabled.
     */
    public boolean isRepairTimed()
    {
        return repairTime.getValue() > -1;
    }

    /**
     * Load the config from the file.
     */
    protected void load()
    {
        worldFolder.mkdirs();
        if (!configFile.exists())
            FileUtils.copyJarConfig(configFile, "world.yml");
        if (!advancedFile.exists())
            FileUtils.copyJarConfig(advancedFile, "world-advanced.yml");
        if (!griefFile.exists())
            FileUtils.copyJarConfig(griefFile, "world-grief.yml");
        if (!replaceFile.exists())
            FileUtils.createNewFile(replaceFile);
        try
        {
            config.load(configFile);
            advanced.load(advancedFile);
            grief.load(griefFile);
            replacement.load(replaceFile);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return;
        } catch (IOException e)
        {
            e.printStackTrace();
            return;
        } catch (InvalidConfigurationException e)
        {
            e.printStackTrace();
            return;
        }

        for (ConfigValue<Boolean> v : booleans.values())
            v.load();
        repairTime.load();
        replaceLimit.load();
        replaceBlackList.load();
        replaceWhiteList.load();
        protectList.load();
        griefPlaceList.load();

        if (isRepairTimed())
            Bukkit.getScheduler().scheduleSyncRepeatingTask(CreeperHeal.getInstance(), new Runnable()
            {
                @Override
                public void run()
                {
                    checkReplaceTime();
                }
            }, 200, 1200);
    }

    /*
     * Task to check if the explosions should be replaced.
     */
    protected void checkReplaceTime()
    {
        long time = Bukkit.getServer().getWorld(getName()).getTime();
        if (((Math.abs(getRepairTime() - time) < 600) || (Math.abs(Math.abs(getRepairTime()
                                                                            - time) - 24000)) < 600))
        {
            ExplodedBlockManager.forceReplace(this);
            BurntBlockManager.forceReplaceBurnt(this);
        }
    }

    /**
     * Write the world's settings to the corresponding file.
     */
    protected void save()
    {
        for (ConfigValue<Boolean> v : booleans.values())
            v.write();
        repairTime.write();
        replaceLimit.write();
        replaceBlackList.write();
        replaceWhiteList.write();
        protectList.write();
        griefPlaceList.write();

        try
        {
            config.save(configFile);
            advanced.save(advancedFile);
            grief.save(griefFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Get the value of the boolean represented by the key.
     *
     * @param key
     *            The key
     * @return The value of the boolean.
     * @throws NullPointerException
     *             If the key does not represent a boolean.
     */
    public boolean getBool(WCfgVal key) throws NullPointerException
    {
        return booleans.get(key.getKey()).getValue();
    }

    /**
     * Get whether damage caused by an entity should be replaced in this world.
     *
     * @param entity
     *            The entity that caused the damage.
     * @return Whether the damage should be replaced.
     */
    public boolean shouldReplace(Entity entity)
    {
        if (getBool(WCfgVal.WORLD_ON))
        {
            if (entity != null)
                switch (entity.getType())
                {
                case CREEPER:
                    return getBool(WCfgVal.CREEPERS) && isAbove(entity.getLocation());
                case PRIMED_TNT:
                    return getBool(WCfgVal.TNT) && isAbove(entity.getLocation());
                case FIREBALL:
                    return getBool(WCfgVal.GHAST) && isAbove(entity.getLocation());
                case ENDER_DRAGON:
                    return getBool(WCfgVal.DRAGONS);
                case WITHER_SKULL:
                    return getBool(WCfgVal.WITHER);
                case MINECART_TNT:
                    return getBool(WCfgVal.MINECART_TNT);
                case ENDER_CRYSTAL:
                    return getBool(WCfgVal.ENDER_CRYSTAL);
                default:
                    return getBool(WCfgVal.CUSTOM);
                }
            return getBool(WCfgVal.CUSTOM);
        }
        return false;
    }

    /**
     * Get whether the location is above the replace limit defined in this
     * world, if height replacement is enabled.
     *
     * @param loc
     *            The location to test.
     * @return Whether the location is above the limit, or true if height
     *         replacement is not enabled.
     */
    public boolean isAbove(Location loc)
    {
        return !getBool(WCfgVal.REPLACE_ABOVE) || loc.getBlockY() >= replaceLimit.getValue();
    }

    /**
     * Get whether the given block type is protected in this world.
     *
     * @param block
     *            The block to test.
     * @return Whether the block's type is protected.
     */
    public boolean isProtected(Block block)
    {
        return protectList.getValue().contains(new BlockId(block))
               || (block.getState() instanceof InventoryHolder
                   && CreeperConfig.getBool(CfgVal.REPLACE_PROTECTED_CHESTS) && PluginHandler
                               .isProtected(block));
    }

    /**
     * Get whether any grief protection feature is enabled, thus requiring to
     * listen to the grief events.
     *
     * @return Whether any grief protection is enabled.
     */
    public boolean hasGriefProtection()
    {
        return getBool(WCfgVal.BLOCK_LAVA)
               || getBool(WCfgVal.BLOCK_IGNITE)
               || getBool(WCfgVal.BLOCK_PVP)
               || getBool(WCfgVal.BLOCK_SPAWN_EGGS)
               || getBool(WCfgVal.BLOCK_TNT)
               || getBool(WCfgVal.WARN_IGNITE)
               || getBool(WCfgVal.WARN_LAVA)
               || getBool(WCfgVal.WARN_PVP)
               || getBool(WCfgVal.WARN_SPAWN_EGGS)
               || getBool(WCfgVal.WARN_TNT)
               || getBool(WCfgVal.PREVENT_FIRE_LAVA)
               || getBool(WCfgVal.PREVENT_FIRE_SPREAD)
               || (!griefPlaceList.getValue().isEmpty() && (getBool(WCfgVal.GRIEF_BLOCK_BLACKLIST) || getBool(WCfgVal.WARN_BLACKLIST)));
    }

    /**
     * Get whether the block is in the grief blackList (or not in the whitelist,
     * if the whitelist is used).
     *
     * @param block
     *            The block to test.
     * @return Whether the block is blacklisted.
     */
    public boolean isGriefBlackListed(Block block)
    {
        return griefPlaceList.getValue().contains(new BlockId(block));
    }

    /**
     * Get the World corresponding to the WorldConfig.
     *
     * @return The world.
     */
    public World getWorld()
    {
        return Bukkit.getWorld(name);
    }

    /**
     * Set the boolean value associated with the key.
     *
     * @param key
     *            The key.
     * @param value
     *            The value.
     * @throws NullPointerException
     *             If the key is not a valid boolean configuration value.
     */
    public void setBool(WCfgVal key, boolean value) throws NullPointerException
    {
        ConfigValue<Boolean> v = booleans.get(key.getKey());
        if (v == null)
            throw new NullPointerException("Unknown config key path : " + key.getKey());
        v.setValue(value);
    }

    /**
     * Set the boolean value associated with the key.
     *
     * @param key
     *            The key.
     * @param value
     *            The value.
     * @throws NullPointerException
     *             If the key is not a valid Integer configuration value.
     */
    public void setInt(WCfgVal key, int value) throws NullPointerException
    {
        switch (key)
        {
        case REPAIR_TIME:
            repairTime.setValue(value);
            break;
        case REPLACE_LIMIT:
            replaceLimit.setValue(value);
            break;
        default:
            throw new NullPointerException("Unknown config key path : " + key.getKey());
        }
    }

    protected void setList(WCfgVal val, HashSet<BlockId> value)
    {
        switch (val)
        {
        case REPLACE_BLACK_LIST:
            replaceBlackList.setValue(value);
            break;
        case REPLACE_WHITE_LIST:
            replaceWhiteList.setValue(value);
            break;
        case PROTECTED_LIST:
            protectList.setValue(value);
            break;
        case GRIEF_PLACE_LIST:
            griefPlaceList.setValue(value);
            break;
        default:
            CreeperLog.warning("Wrong key type : " + val.toString());
        }
    }

    /**
     * Get the time at which all repairs are automatically enforced.
     *
     * @return The time at which repairs are enforced, -1 if deactivated.
     */
    public int getRepairTime()
    {
        return repairTime.getValue();
    }

    /**
     * Get whether a block is blacklisted for replacement (i.e. should not be
     * replaced).
     *
     * @param id
     *            The blockId of the block.
     * @return True if the block should not be blacklisted.
     */
    public boolean isBlackListed(BlockId id)
    {
        if (getBool(WCfgVal.USE_REPLACE_WHITE_LIST))
            return !replaceWhiteList.getValue().contains(id);
        else
            return replaceBlackList.getValue().contains(id);
    }

    public void getReplacement(BlockState state)
    {
        String key = state.getType().toString() + ';' + state.getRawData();
        String s = replacement.getString(key + ".type");
        if (s == null)
        {
            key = state.getType().toString();
            s = replacement.getString(key + ".type");
        }
        if (s == null)
            return;
        Material material = Material.getMaterial(s);
        if (material == null)
        {
            CreeperLog.warning("Invalid block type for " + key + " in world " + state.getWorld().getName());
            return;
        }
        int data = replacement.getInt(key + ".data", -1);
        state.setType(material);
        if (data != -1)
            state.setRawData((byte) data);
        else
            state.setRawData((byte) 0);
    }
}
