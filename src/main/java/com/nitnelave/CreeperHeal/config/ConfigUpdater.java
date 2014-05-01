package com.nitnelave.CreeperHeal.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.utils.CreeperLog;

class ConfigUpdater
{

    private static int waitBeforeHeal, logLevel = -42, blockPerBlockInterval, waitBeforeHealBurnt,
                    dropChance, distanceNear, obsidianChance, obsidianRadius,
                    waitBeforeBurnAgain;
    private static boolean blockPerBlock, teleportOnSuffocate, dropDestroyedBlocks,
                    crackDestroyedBricks, replaceProtectedChests, overwriteBlocks,
                    preventBlockFall, lightweightMode, logWarnings, preventChainReaction,
                    explodeObsidian, debug;
    private static String cmdAlias;

    private static void recordValues()
    {
        CreeperConfig.setBool(CfgVal.BLOCK_PER_BLOCK, blockPerBlock);
        CreeperConfig.setBool(CfgVal.TELEPORT_ON_SUFFOCATE, teleportOnSuffocate);
        CreeperConfig.setBool(CfgVal.DROP_DESTROYED_BLOCKS, dropDestroyedBlocks);
        CreeperConfig.setBool(CfgVal.CRACK_DESTROYED_BRICKS, crackDestroyedBricks);
        CreeperConfig.setBool(CfgVal.REPLACE_PROTECTED_CHESTS, replaceProtectedChests);
        CreeperConfig.setBool(CfgVal.OVERWRITE_BLOCKS, overwriteBlocks);
        CreeperConfig.setBool(CfgVal.PREVENT_BLOCK_FALL, preventBlockFall);
        CreeperConfig.setBool(CfgVal.RAIL_REPLACEMENT, lightweightMode);
        CreeperConfig.setBool(CfgVal.SUFFOCATING_ANIMALS, lightweightMode);
        CreeperConfig.setBool(CfgVal.LEAVES_VINES, lightweightMode);
        CreeperConfig.setBool(CfgVal.SORT_BY_RADIUS, lightweightMode);
        CreeperConfig.setBool(CfgVal.LOG_WARNINGS, logWarnings);
        CreeperConfig.setBool(CfgVal.PREVENT_CHAIN_REACTION, preventChainReaction);
        CreeperConfig.setBool(CfgVal.EXPLODE_OBSIDIAN, explodeObsidian);
        CreeperConfig.setBool(CfgVal.DEBUG, debug);
        CreeperConfig.setInt(CfgVal.WAIT_BEFORE_HEAL, waitBeforeHeal);
        CreeperConfig.setInt(CfgVal.LOG_LEVEL, logLevel);
        CreeperConfig.setInt(CfgVal.BLOCK_PER_BLOCK_INTERVAL, blockPerBlockInterval);
        CreeperConfig.setInt(CfgVal.WAIT_BEFORE_HEAL_BURNT, waitBeforeHealBurnt);
        CreeperConfig.setInt(CfgVal.DROP_CHANCE, dropChance);
        CreeperConfig.setInt(CfgVal.DISTANCE_NEAR, distanceNear);
        CreeperConfig.setInt(CfgVal.OBSIDIAN_CHANCE, obsidianChance);
        CreeperConfig.setInt(CfgVal.OBSIDIAN_RADIUS, obsidianRadius);
        CreeperConfig.setInt(CfgVal.WAIT_BEFORE_BURN_AGAIN, waitBeforeBurnAgain);
        CreeperConfig.setAlias(cmdAlias);
        for (OutDatedCfgVal v : OutDatedCfgVal.values())
            CreeperConfig.remove(v.getKey(), v.isAdvanced());
    }

    private static void from4() throws FileNotFoundException, IOException,
                               InvalidConfigurationException
    {
        CreeperLog.logInfo("Importing config from version 4", 1);
        YamlConfiguration config = new YamlConfiguration();
        File configFile = new File(CreeperHeal.getCHFolder() + "/config.yml");
        config.load(configFile);
        String tmp_str;
        try
        {
            tmp_str = config.getString("replacement-method", "block-per-block").trim();
        } catch (Exception e)
        {
            CreeperLog.warning("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
            CreeperLog.warning(e.getMessage());
            tmp_str = "block-per-block";
        }
        if (!tmp_str.equalsIgnoreCase("all-at-once")
            && !tmp_str.equalsIgnoreCase("block-per-block"))
            CreeperLog.warning("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
        waitBeforeHeal = config.getInt("wait-before-heal-explosions", 60);
        logLevel = config.getInt("verbose-level", 1);
        blockPerBlock = (tmp_str.equalsIgnoreCase("all-at-once")) ? false : true;
        teleportOnSuffocate = config.getBoolean("teleport-when-buried", true);
        waitBeforeHealBurnt = config.getInt("wait-before-heal-fire", 45);
        dropDestroyedBlocks = config.getBoolean("drop-destroyed-blocks", true);
        dropChance = config.getInt("drop-destroyed-blocks-chance", 100);
        crackDestroyedBricks = config.getBoolean("crack-destroyed-bricks", false);
        overwriteBlocks = config.getBoolean("overwrite-blocks", true);
        preventBlockFall = config.getBoolean("prevent-block-fall", true);
        distanceNear = config.getInt("distance-near", 20);
        lightweightMode = config.getBoolean("lightweight-mode", false);
        cmdAlias = config.getString("command-alias", "ch");
        logWarnings = true;
        debug = preventChainReaction = false;
        obsidianChance = 20;
        obsidianRadius = 5;
        explodeObsidian = false;
        waitBeforeBurnAgain = 240;
        config.set("config-version", 5);
        try
        {
            tmp_str = config.getString("chest-protection", "no").trim().toLowerCase();
        } catch (Exception e)
        {
            CreeperLog.warning("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
            CreeperLog.warning(e.getMessage());
            tmp_str = "no";
        }

        if (!tmp_str.equalsIgnoreCase("no") && !tmp_str.equalsIgnoreCase("lwc")
            && !tmp_str.equalsIgnoreCase("all")
            && !tmp_str.equalsIgnoreCase("lockette"))
            CreeperLog.warning("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
        else if (tmp_str.equals("all") || tmp_str.equals("lwc") || tmp_str.equals("lockette"))
            replaceProtectedChests = true;

        configFile.delete();

    }

    private static void from5() throws FileNotFoundException, IOException,
                               InvalidConfigurationException
    {
        CreeperLog.logInfo("Importing config from version 5", 1);

        YamlConfiguration config = new YamlConfiguration();
        File configFile = new File(CreeperHeal.getCHFolder() + "/config.yml");
        config.load(configFile);
        File advancedFile = new File(CreeperHeal.getCHFolder() + "/advanced.yml");

        blockPerBlockInterval = config.getInt("replacement.block-per-block.interval", 20);
        waitBeforeHeal = config.getInt("replacement.wait-before-heal.explosions", 60);
        blockPerBlock = config.getBoolean("replacement.block-per-block", true);
        waitBeforeHealBurnt = config.getInt("replacement.wait-before-heal.fire", 45);
        crackDestroyedBricks = config.getBoolean("replacement.crack-destroyed-bricks", false);
        boolean replaceAllChests = config.getBoolean("replacement.ignore-chests.all", false);
        replaceProtectedChests = replaceAllChests
                                 || config.getBoolean("replacement.ignore-chests.protected", false);
        logLevel = config.getInt("advanced.verbose-level", 1);
        teleportOnSuffocate = config.getBoolean("advanced.teleport-when-buried", true);
        dropDestroyedBlocks = config.getBoolean("advanced.drop-destroyed-blocks.enabled", true);
        dropChance = config.getInt("advanced.drop-destroyed-blocks.chance", 100);
        overwriteBlocks = config.getBoolean("advanced.replacement-conflict.overwrite", true);
        preventBlockFall = config.getBoolean("advanced.prevent-block-fall", true);
        distanceNear = config.getInt("advanced.distance-near", 20);
        lightweightMode = config.getBoolean("advanced.lightweight-mode", false);
        cmdAlias = config.getString("advanced.command-alias", "ch");
        logWarnings = config.getBoolean("advanced.log-warnings", true);
        preventChainReaction = config.getBoolean("advanced.prevent-chain-reaction", false);
        explodeObsidian = config.getBoolean("advanced.obsidian.explode", false);
        obsidianRadius = config.getInt("advanced.obsidian.radius", 5);
        obsidianChance = config.getInt("advanced.obsidian.chance", 20);
        debug = config.getBoolean("advanced.debug-messages", false);
        waitBeforeBurnAgain = 240;

        configFile.delete();
        advancedFile.delete();

    }

    private static void from6() throws FileNotFoundException, IOException,
                               InvalidConfigurationException
    {
        CreeperLog.logInfo("Importing config from version 6", 1);
        YamlConfiguration config = new YamlConfiguration();
        File configFile = new File(CreeperHeal.getCHFolder() + "/config.yml");
        config.load(configFile);
        YamlConfiguration advanced = new YamlConfiguration();
        File advancedFile = new File(CreeperHeal.getCHFolder() + "/advanced.yml");
        advanced.load(advancedFile);

        blockPerBlockInterval = config.getInt("block-per-block.interval", 20);
        waitBeforeHeal = config.getInt("wait-before-heal.explosions", 60);
        blockPerBlock = config.getBoolean("block-per-block.enabled", true);
        waitBeforeHealBurnt = config.getInt("wait-before-heal.fire", 45);
        crackDestroyedBricks = config.getBoolean("crack-destroyed-bricks", false);
        boolean replaceAllChests = config.getBoolean("ignore-chests.all", false);
        replaceProtectedChests = replaceAllChests
                                 || config.getBoolean("ignore-chests.protected", false);

        logLevel = advanced.getInt("verbose-level", 1);
        teleportOnSuffocate = advanced.getBoolean("teleport-when-buried", true);
        dropDestroyedBlocks = advanced.getBoolean("drop-destroyed-blocks.enabled", true);
        dropChance = advanced.getInt("drop-destroyed-blocks.chance", 100);
        overwriteBlocks = advanced.getBoolean("replacement-conflict.overwrite", true);
        preventBlockFall = advanced.getBoolean("prevent-block-fall", true);
        distanceNear = advanced.getInt("distance-near", 20);
        lightweightMode = advanced.getBoolean("lightweight-mode", false);
        cmdAlias = advanced.getString("command-alias", "ch");
        logWarnings = advanced.getBoolean("log-warnings", true);
        preventChainReaction = advanced.getBoolean("prevent-chain-reaction", false);
        explodeObsidian = advanced.getBoolean("obsidian.explode", false);
        obsidianRadius = advanced.getInt("obsidian.radius", 5);
        obsidianChance = advanced.getInt("obsidian.chance", 20);
        debug = advanced.getBoolean("debug-messages", false);
        waitBeforeBurnAgain = advanced.getInt("wait-before-burn-again", 240);

        configFile.delete();
        advancedFile.delete();
    }

    protected static void importFrom(int version)
    {
        try
        {
            switch (version)
            {
            case 4:
                from4();
                break;
            case 5:
                from5();
                break;
            case 6:
            case 7:
                from6();
                break;
            default:
                CreeperLog.warning("Trying to import from an unknown config version.");

            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        recordValues();
    }

}
