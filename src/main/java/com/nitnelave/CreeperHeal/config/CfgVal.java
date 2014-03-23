package com.nitnelave.CreeperHeal.config;

/**
 * An enum with all of the configuration value.
 */
public enum CfgVal implements CfgValEnumMember
{

    BLOCK_PER_BLOCK_INTERVAL("block-per-block.interval", 20, false),
    WAIT_BEFORE_HEAL("wait-before-heal.explosions", 60, false),
    BLOCK_PER_BLOCK("block-per-block.enabled", true, false),
    WAIT_BEFORE_HEAL_BURNT("wait-before-heal.fire", 45, false),
    CRACK_DESTROYED_BRICKS("crack-destroyed-bricks", false, false),
    REPLACE_PROTECTED_CHESTS("replace-protected-chests-immediately", false, false),
    LOG_LEVEL("verbose-level", 1, true),
    TELEPORT_ON_SUFFOCATE("teleport-when-buried", true, true),
    DROP_DESTROYED_BLOCKS("drop-destroyed-blocks.enabled", true, true),
    DROP_CHANCE("drop-destroyed-blocks.chance", 100, true),
    OVERWRITE_BLOCKS("overwrite-blocks", true, true),
    PREVENT_BLOCK_FALL("prevent-block-fall", true, true),
    DISTANCE_NEAR("distance-near", 20, true),
    ALIAS("command-alias", "ch", true),
    LOG_WARNINGS("log-warnings", true, true),
    PREVENT_CHAIN_REACTION("prevent-chain-reaction", false, true),
    EXPLODE_OBSIDIAN("obsidian.explode", false, true),
    OBSIDIAN_RADIUS("obsidian.radius", 5, true),
    OBSIDIAN_CHANCE("obsidian.chance", 20, true),
    DEBUG("debug-messages", false, true),
    REPLACE_SILVERFISH_BLOCKS("replace-silverfish-blocks", false, true),
    WAIT_BEFORE_BURN_AGAIN("wait-before-burn-again", 240, true),
    RAIL_REPLACEMENT("performance.replace-rails-correctly", true, true),
    SUFFOCATING_ANIMALS("performance.save-suffocating-mobs", true, true),
    LEAVES_VINES("performance.keep-leaves-vines", true, true),
    SORT_BY_RADIUS("performance.sort-exploded-blocks-by-distance", true, true),
    JOIN_EXPLOSIONS("join-nearby-explosions", true, true),
    STONE_TO_COBBLE("change-stone-to-cobble", false, true),
    OBSIDIAN_TABLE("obsidian.enchant-tables-and-chests", false, true),
    SOUND_NAME("sound.type", "ITEM_PICKUP", false),
    SOUND_VOLUME("sound.volume", 1, false);

    private final String key;
    private final Object defaultValue;
    private final boolean advanced;

    private CfgVal(String key, Object value, boolean adv)
    {
        this.key = key;
        defaultValue = value;
        advanced = adv;
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

    /*
     * Get whether this value is in the advanced file or the config file. Return
     * true if it is in the advanced file.
     */
    protected boolean isAdvanced()
    {
        return advanced;
    }
}
