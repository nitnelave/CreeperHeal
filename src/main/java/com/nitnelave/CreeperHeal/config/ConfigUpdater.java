package com.nitnelave.CreeperHeal.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.utils.CreeperLog;

class ConfigUpdater {

    private static int waitBeforeHeal, logLevel = -42, blockPerBlockInterval, waitBeforeHealBurnt, dropChance, distanceNear, obsidianChance, obsidianRadius,
            waitBeforeBurnAgain;
    private static boolean dropReplacedBlocks, blockPerBlock, teleportOnSuffocate, dropDestroyedBlocks, crackDestroyedBricks, replaceProtectedChests,
            overwriteBlocks, preventBlockFall, lightweightMode, logWarnings, preventChainReaction, explodeObsidian, debug;
    private static String cmdAlias;

    private static void recordValues () {
        CreeperConfig.setBool (CfgVal.BLOCK_PER_BLOCK, blockPerBlock);
        CreeperConfig.setBool (CfgVal.DROP_REPLACED_BLOCKS, dropReplacedBlocks);
        CreeperConfig.setBool (CfgVal.TELEPORT_ON_SUFFOCATE, teleportOnSuffocate);
        CreeperConfig.setBool (CfgVal.DROP_DESTROYED_BLOCKS, dropDestroyedBlocks);
        CreeperConfig.setBool (CfgVal.CRACK_DESTROYED_BRICKS, crackDestroyedBricks);
        CreeperConfig.setBool (CfgVal.REPLACE_PROTECTED_CHESTS, replaceProtectedChests);
        CreeperConfig.setBool (CfgVal.OVERWRITE_BLOCKS, overwriteBlocks);
        CreeperConfig.setBool (CfgVal.PREVENT_BLOCK_FALL, preventBlockFall);
        CreeperConfig.setBool (CfgVal.LIGHTWEIHGTMODE, lightweightMode);
        CreeperConfig.setBool (CfgVal.LOG_WARNINGS, logWarnings);
        CreeperConfig.setBool (CfgVal.PREVENT_CHAIN_REACTION, preventChainReaction);
        CreeperConfig.setBool (CfgVal.EXPLODE_OBSIDIAN, explodeObsidian);
        CreeperConfig.setBool (CfgVal.DEBUG, debug);
        CreeperConfig.setInt (CfgVal.WAIT_BEFORE_HEAL, waitBeforeHeal);
        CreeperConfig.setInt (CfgVal.LOG_LEVEL, logLevel);
        CreeperConfig.setInt (CfgVal.BLOCK_PER_BLOCK_INTERVAL, blockPerBlockInterval);
        CreeperConfig.setInt (CfgVal.WAIT_BEFORE_HEAL_BURNT, waitBeforeHealBurnt);
        CreeperConfig.setInt (CfgVal.DROP_CHANCE, dropChance);
        CreeperConfig.setInt (CfgVal.DISTANCE_NEAR, distanceNear);
        CreeperConfig.setInt (CfgVal.OBSIDIAN_CHANCE, obsidianChance);
        CreeperConfig.setInt (CfgVal.OBSIDIAN_RADIUS, obsidianRadius);
        CreeperConfig.setInt (CfgVal.WAIT_BEFORE_BURN_AGAIN, waitBeforeBurnAgain);
        CreeperConfig.setAlias (cmdAlias);
    }

    private static void from4 () throws FileNotFoundException, IOException, InvalidConfigurationException {
        CreeperLog.logInfo ("Importing config from version 4", 1);
        YamlConfiguration config = new YamlConfiguration ();
        File configFile = new File (CreeperHeal.getCHFolder () + "config.yml");
        config.load (configFile);
        String tmp_str;
        try
        {
            tmp_str = config.getString ("replacement-method", "block-per-block").trim ();
        } catch (Exception e)
        {
            CreeperLog.warning ("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
            CreeperLog.warning (e.getMessage ());
            tmp_str = "block-per-block";
        }
        if (!tmp_str.equalsIgnoreCase ("all-at-once") && !tmp_str.equalsIgnoreCase ("block-per-block"))
            CreeperLog.warning ("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
        waitBeforeHeal = getInt (config, "wait-before-heal-explosions", 60);
        logLevel = getInt (config, "verbose-level", 1);
        dropReplacedBlocks = getBoolean (config, "drop-overwritten-blocks", true);
        blockPerBlock = (tmp_str.equalsIgnoreCase ("all-at-once")) ? false : true;
        teleportOnSuffocate = getBoolean (config, "teleport-when-buried", true);
        waitBeforeHealBurnt = getInt (config, "wait-before-heal-fire", 45);
        dropDestroyedBlocks = getBoolean (config, "drop-destroyed-blocks", true);
        dropChance = getInt (config, "drop-destroyed-blocks-chance", 100);
        crackDestroyedBricks = getBoolean (config, "crack-destroyed-bricks", false);
        overwriteBlocks = getBoolean (config, "overwrite-blocks", true);
        preventBlockFall = getBoolean (config, "prevent-block-fall", true);
        distanceNear = getInt (config, "distance-near", 20);
        lightweightMode = getBoolean (config, "lightweight-mode", false);
        cmdAlias = config.getString ("command-alias", "ch");
        logWarnings = true;
        debug = preventChainReaction = false;
        obsidianChance = 20;
        obsidianRadius = 5;
        explodeObsidian = false;
        waitBeforeBurnAgain = 240;
        config.set ("config-version", 5);
        try
        {
            tmp_str = config.getString ("chest-protection", "no").trim ().toLowerCase ();
        } catch (Exception e)
        {
            CreeperLog.warning ("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
            CreeperLog.warning (e.getMessage ());
            tmp_str = "no";
        }

        if (!tmp_str.equalsIgnoreCase ("no") && !tmp_str.equalsIgnoreCase ("lwc") && !tmp_str.equalsIgnoreCase ("all")
                && !tmp_str.equalsIgnoreCase ("lockette"))
            CreeperLog.warning ("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
        else if (tmp_str.equals ("all") || tmp_str.equals ("lwc") || tmp_str.equals ("lockette"))
            replaceProtectedChests = true;

        configFile.delete ();

    }

    private static void from5 () throws FileNotFoundException, IOException, InvalidConfigurationException {
        CreeperLog.logInfo ("Importing config from version 5", 1);

        YamlConfiguration config = new YamlConfiguration ();
        File configFile = new File (CreeperHeal.getCHFolder () + "config.yml");
        config.load (configFile);
        File advancedFile = new File (CreeperHeal.getCHFolder () + "advanced.yml");

        blockPerBlockInterval = getInt (config, "replacement.block-per-block.interval", 20);
        waitBeforeHeal = getInt (config, "replacement.wait-before-heal.explosions", 60);
        blockPerBlock = getBoolean (config, "replacement.block-per-block", true);
        waitBeforeHealBurnt = getInt (config, "replacement.wait-before-heal.fire", 45);
        crackDestroyedBricks = getBoolean (config, "replacement.crack-destroyed-bricks", false);
        boolean replaceAllChests = getBoolean (config, "replacement.ignore-chests.all", false);
        replaceProtectedChests = replaceAllChests || getBoolean (config, "replacement.ignore-chests.protected", false);
        logLevel = getInt (config, "advanced.verbose-level", 1);
        dropReplacedBlocks = getBoolean (config, "advanced.replacement-conflict.drop-overwritten-blocks", true);
        teleportOnSuffocate = getBoolean (config, "advanced.teleport-when-buried", true);
        dropDestroyedBlocks = getBoolean (config, "advanced.drop-destroyed-blocks.enabled", true);
        dropChance = getInt (config, "advanced.drop-destroyed-blocks.chance", 100);
        overwriteBlocks = getBoolean (config, "advanced.replacement-conflict.overwrite", true);
        preventBlockFall = getBoolean (config, "advanced.prevent-block-fall", true);
        distanceNear = getInt (config, "advanced.distance-near", 20);
        lightweightMode = getBoolean (config, "advanced.lightweight-mode", false);
        cmdAlias = config.getString ("advanced.command-alias", "ch");
        logWarnings = getBoolean (config, "advanced.log-warnings", true);
        preventChainReaction = getBoolean (config, "advanced.prevent-chain-reaction", false);
        explodeObsidian = getBoolean (config, "advanced.obsidian.explode", false);
        obsidianRadius = getInt (config, "advanced.obsidian.radius", 5);
        obsidianChance = getInt (config, "advanced.obsidian.chance", 20);
        debug = getBoolean (config, "advanced.debug-messages", false);
        waitBeforeBurnAgain = 240;

        configFile.delete ();
        advancedFile.delete ();

    }

    private static void from6 () throws FileNotFoundException, IOException, InvalidConfigurationException {
        CreeperLog.logInfo ("Importing config from version 6", 1);
        YamlConfiguration config = new YamlConfiguration ();
        File configFile = new File (CreeperHeal.getCHFolder () + "config.yml");
        config.load (configFile);
        YamlConfiguration advanced = new YamlConfiguration ();
        File advancedFile = new File (CreeperHeal.getCHFolder () + "advanced.yml");
        advanced.load (advancedFile);

        blockPerBlockInterval = getInt (config, "block-per-block.interval", 20);
        waitBeforeHeal = getInt (config, "wait-before-heal.explosions", 60);
        blockPerBlock = getBoolean (config, "block-per-block.enabled", true);
        waitBeforeHealBurnt = getInt (config, "wait-before-heal.fire", 45);
        crackDestroyedBricks = getBoolean (config, "crack-destroyed-bricks", false);
        boolean replaceAllChests = getBoolean (config, "ignore-chests.all", false);
        replaceProtectedChests = replaceAllChests || getBoolean (config, "ignore-chests.protected", false);

        logLevel = getInt (advanced, "verbose-level", 1);
        dropReplacedBlocks = getBoolean (advanced, "replacement-conflict.drop-overwritten-blocks", true);
        teleportOnSuffocate = getBoolean (advanced, "teleport-when-buried", true);
        dropDestroyedBlocks = getBoolean (advanced, "drop-destroyed-blocks.enabled", true);
        dropChance = getInt (advanced, "drop-destroyed-blocks.chance", 100);
        overwriteBlocks = getBoolean (advanced, "replacement-conflict.overwrite", true);
        preventBlockFall = getBoolean (advanced, "prevent-block-fall", true);
        distanceNear = getInt (advanced, "distance-near", 20);
        lightweightMode = getBoolean (advanced, "lightweight-mode", false);
        cmdAlias = advanced.getString ("command-alias", "ch");
        logWarnings = getBoolean (advanced, "log-warnings", true);
        preventChainReaction = getBoolean (advanced, "prevent-chain-reaction", false);
        explodeObsidian = getBoolean (advanced, "obsidian.explode", false);
        obsidianRadius = getInt (advanced, "obsidian.radius", 5);
        obsidianChance = getInt (advanced, "obsidian.chance", 20);
        debug = getBoolean (advanced, "debug-messages", false);
        waitBeforeBurnAgain = getInt (advanced, "wait-before-burn-again", 240);

        configFile.delete ();
        advancedFile.delete ();
    }

    protected static boolean getBoolean (YamlConfiguration config, String key, boolean def) {
        boolean tmp;
        try
        {
            tmp = config.getBoolean (key, def);
        } catch (Exception e)
        {
            CreeperLog.warning ("[CreeperHeal] Wrong value for " + key + " field in file " + config.getName () + ". Defaulting to " + Boolean.toString (def));
            tmp = def;
        }
        return tmp;
    }

    protected static int getInt (YamlConfiguration config, String key, int def) {
        int tmp;
        try
        {
            tmp = config.getInt (key, def);
        } catch (Exception e)
        {
            CreeperLog.warning ("[CreeperHeal] Wrong value for " + key + " field in file " + config.getName () + ". Defaulting to " + Integer.toString (def));
            tmp = def;
        }
        return tmp;
    }

    protected static void importFrom (int version) {
        try
        {
            switch (version)
            {
                case 4:
                    from4 ();
                    break;
                case 5:
                    from5 ();
                    break;
                case 6:
                    from6 ();
                    break;

            }
        } catch (Exception e)
        {
            e.printStackTrace ();
            return;
        }
        recordValues ();
    }

}
