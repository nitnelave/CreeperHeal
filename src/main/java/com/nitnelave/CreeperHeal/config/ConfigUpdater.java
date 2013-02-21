package com.nitnelave.CreeperHeal.config;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.nitnelave.CreeperHeal.block.BlockId;
import com.nitnelave.CreeperHeal.block.BlockManager;

public class ConfigUpdater extends CreeperConfig {

    private final static String[] STRING_BOOLEAN_OPTIONS = {"true", "false", "time"};

    protected static void importFrom4 () {
        log.info ("Importing config from version 4");
        waitBeforeHeal = getInt (configFile, "wait-before-heal-explosions", 60); //tries to read the value directly from the config
        logLevel = getInt (configFile, "verbose-level", 1);
        dropReplacedBlocks = getBoolean (configFile, "drop-overwritten-blocks", true);
        String tmp_str;
        try
        {
            tmp_str = configFile.getString ("replacement-method", "block-per-block").trim ();
        } catch (Exception e)
        {
            log.warning ("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
            log.info (e.getLocalizedMessage ());
            tmp_str = "block-per-block";
        }
        if (!tmp_str.equalsIgnoreCase ("all-at-once") && !tmp_str.equalsIgnoreCase ("block-per-block"))
            log.warning ("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
        blockPerBlock = (tmp_str.equalsIgnoreCase ("all-at-once")) ? false : true;
        teleportOnSuffocate = getBoolean (configFile, "teleport-when-buried", true);
        waitBeforeHealBurnt = getInt (configFile, "wait-before-heal-fire", 45);
        dropDestroyedBlocks = getBoolean (configFile, "drop-destroyed-blocks", true);
        dropChance = getInt (configFile, "drop-destroyed-blocks-chance", 100);
        opEnforce = getBoolean (configFile, "op-have-all-permissions", true);
        crackDestroyedBricks = getBoolean (configFile, "crack-destroyed-bricks", false);
        overwriteBlocks = getBoolean (configFile, "overwrite-blocks", true);
        preventBlockFall = getBoolean (configFile, "prevent-block-fall", true);
        distanceNear = getInt (configFile, "distance-near", 20);
        lightweightMode = getBoolean (configFile, "lightweight-mode", false);
        alias = configFile.getString ("command-alias", "ch");
        configVersion = 5;
        logWarnings = true;
        debug = preventChainReaction = false;
        obsidianChance = 20;
        obsidianRadius = 5;
        explodeObsidian = false;
        waitBeforeBurnAgain = 240;
        set (advancedFile, "log-warnings", true);
        set (configFile, "config-version", 5);
        try
        {
            tmp_str = configFile.getString ("chest-protection", "no").trim ().toLowerCase ();
        } catch (Exception e)
        {
            log.warning ("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
            log.info (e.getLocalizedMessage ());
            tmp_str = "no";
        }

        if (!tmp_str.equalsIgnoreCase ("no") && !tmp_str.equalsIgnoreCase ("lwc") && !tmp_str.equalsIgnoreCase ("all")
                && !tmp_str.equalsIgnoreCase ("lockette"))
            log.warning ("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
        else
        {
            replaceAllChests = replaceProtectedChests = false;

            if (tmp_str.equals ("all"))
                replaceAllChests = true;
            else if (tmp_str.equals ("lwc") || tmp_str.equals ("lockette"))
                replaceProtectedChests = true;
        }
        boolean timeRepairs = false;
        world_config.clear ();
        for (World w : Bukkit.getServer ().getWorlds ())
        {
            String name = w.getName ();
            timeRepairs = timeRepairs || importWorld (name).repairTime > -1;
        }
        if (timeRepairs)
            BlockManager.scheduleTimeRepairs ();
    }

    @SuppressWarnings("deprecation")
    private static WorldConfig importWorld (String name) {
        WorldConfig returnValue = world_config.get (name);

        if (returnValue == null)
        {
            log.info ("Importing settings for world: " + name);
            boolean creeper = !getStringBoolean (name + ".Creepers", "true").equalsIgnoreCase ("false");
            boolean tnt = !getStringBoolean (name + ".TNT", "true").equalsIgnoreCase ("false");
            boolean fire = !getStringBoolean (name + ".Fire", "true").equalsIgnoreCase ("false");
            boolean ghast = !getStringBoolean (name + ".Ghast", "true").equalsIgnoreCase ("false");
            boolean magical = !getStringBoolean (name + ".Magical", "false").equalsIgnoreCase ("false");
            boolean replaceAbove = getBoolean (configFile, name + ".replace-above-limit-only", false);
            int replaceLimit = getInt (configFile, name + ".replace-limit", 64);
            boolean enderman = getBoolean (configFile, name + ".block-enderman-pickup", false);
            boolean dragons = !getStringBoolean (name + ".dragons", "false").equalsIgnoreCase ("false");
            int wRepairTime = getInt (configFile, name + ".repair-time", -1);

            HashSet<BlockId> restrict_list = new HashSet<BlockId> ();
            try
            {
                String tmp_str1 = configFile.getString (name + ".restrict-list", "").trim ();
                String[] split = tmp_str1.split (",");
                for (String elem : split)
                    restrict_list.add (new BlockId (elem));
            } catch (NumberFormatException e)
            {
                log.warning ("[CreeperHeal] Wrong values for restrict-list field for world " + name);
                restrict_list.clear ();
                restrict_list.add (new BlockId (0));
            }

            returnValue = new WorldConfig (name, creeper, tnt, ghast, dragons, magical, fire, enderman, replaceAbove, replaceLimit, restrict_list, wRepairTime);
            world_config.put (name, returnValue);
            return returnValue;
        }

        return returnValue;
    }

    private static String getStringBoolean (String path, String defaultValue) {
        String result = "";
        try
        {
            result = configFile.getString (path, defaultValue).trim ().toLowerCase ();
        } catch (Exception e)
        {
            log.warning ("[CreeperHeal] Wrong value for " + path + " field. Defaulting to " + defaultValue + ".");
            log.info (e.getLocalizedMessage ());
            result = defaultValue;
        }

        boolean correct = false;
        for (int i = 0; i <= 2; i++)
            correct = correct || STRING_BOOLEAN_OPTIONS[i].equalsIgnoreCase (result);

        if (!correct)
        {
            log.warning ("[CreeperHeal] Wrong value for " + path + " field. Defaulting to " + defaultValue + ".");
            return defaultValue;
        }
        return result;
    }

    protected static void removeOldWorldConfig () {
        set (configFile, "world", null);
    }

}
