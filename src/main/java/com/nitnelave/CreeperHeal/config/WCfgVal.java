package com.nitnelave.CreeperHeal.config;

import org.bukkit.Material;

import java.util.HashSet;

/**
 * An enum with all of the config values related to a single world.
 */
public enum WCfgVal implements CfgValEnumMember
{
    CREEPERS("creepers", true, CONFIG_FILES.CONFIG),
    TNT("TNT", true, CONFIG_FILES.CONFIG),
    GHAST("ghast", true, CONFIG_FILES.CONFIG),
    DRAGONS("dragons", false, CONFIG_FILES.CONFIG),
    CUSTOM("custom", false, CONFIG_FILES.CONFIG),
    FIRE("fire", true, CONFIG_FILES.CONFIG),
    ENDERMAN("enderman", false, CONFIG_FILES.CONFIG),
    WITHER("wither", true, CONFIG_FILES.CONFIG),
    MINECART_TNT("minecart-tnt", true, CONFIG_FILES.CONFIG),
    ENDER_CRYSTAL("ender-crystal", false, CONFIG_FILES.CONFIG),
    ZOMBIE_DOOR("door-broken-by-zombie", false, CONFIG_FILES.CONFIG),
    REPLACE_ABOVE("replace-above-limit-only", false, CONFIG_FILES.CONFIG),
    REPLACE_LIMIT("replace-limit", 64, CONFIG_FILES.CONFIG),
    REPLACE_BLACK_LIST("restrict.blacklist", new HashSet<Material>(), CONFIG_FILES.ADVANCED),
    REPLACE_WHITE_LIST("restrict.whitelist", new HashSet<Material>(), CONFIG_FILES.ADVANCED),
    USE_REPLACE_WHITE_LIST("restrict.use-whitelist", false, CONFIG_FILES.ADVANCED),
    PROTECTED_LIST("protected-list", new HashSet<Material>(), CONFIG_FILES.ADVANCED),
    REPAIR_TIME("repair-time-of-day", -1, CONFIG_FILES.ADVANCED),
    FACTIONS_IGNORE_WILDERNESS("factions.ignore-wilderness", false, CONFIG_FILES.ADVANCED),
    FACTIONS_IGNORE_TERRITORY("factions.ignore-territory", false, CONFIG_FILES.ADVANCED),
    GRASS_TO_DIRT("replace-grass-with-dirt", false, CONFIG_FILES.ADVANCED),
    BLOCK_LAVA("block.lava", false, CONFIG_FILES.GRIEF),
    BLOCK_TNT("block.TNT", false, CONFIG_FILES.GRIEF),
    BLOCK_IGNITE("block.flint-and-steel", false, CONFIG_FILES.GRIEF),
    GRIEF_BLOCK_BLACKLIST("block.blacklist", false, CONFIG_FILES.GRIEF),
    BLOCK_SPAWN_EGGS("block.spawn-eggs", false, CONFIG_FILES.GRIEF),
    BLOCK_PVP("block.PvP", false, CONFIG_FILES.GRIEF),
    WARN_LAVA("warn.lava", false, CONFIG_FILES.GRIEF),
    WARN_TNT("warn.TNT", false, CONFIG_FILES.GRIEF),
    WARN_IGNITE("warn.flint-and-steel", false, CONFIG_FILES.GRIEF),
    WARN_BLACKLIST("warn.blacklist", false, CONFIG_FILES.GRIEF),
    WARN_SPAWN_EGGS("warn.spawn-eggs", false, CONFIG_FILES.GRIEF),
    WARN_PVP("warn.PvP", false, CONFIG_FILES.GRIEF),
    PREVENT_FIRE_SPREAD("prevent-fire-spread.fire", false, CONFIG_FILES.GRIEF),
    PREVENT_FIRE_LAVA("prevent-fire-spread.lava", false, CONFIG_FILES.GRIEF),
    GRIEF_PLACE_LIST("blacklist", new HashSet<Material>(), CONFIG_FILES.GRIEF),
    WORLD_ON("enabled", true, CONFIG_FILES.CONFIG),
    DROP_CHEST_CONTENTS("drop-chest-contents", false, CONFIG_FILES.ADVANCED);

    protected enum CONFIG_FILES
    {
        CONFIG,
        ADVANCED,
        GRIEF,
    }

    private final String key;
    private final Object defaultValue;
    private final CONFIG_FILES file;

    WCfgVal(String key, Object value, CONFIG_FILES file)
    {
        this.key = key;
        defaultValue = value;
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.config.CfgValEnumMember#getKey()
     */
    @Override
    public String getKey()
    {
        return key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.config.CfgValEnumMember#getDefaultValue()
     */
    @Override
    public Object getDefaultValue()
    {
        return defaultValue;
    }

    protected CONFIG_FILES getFile()
    {
        return file;
    }
}
