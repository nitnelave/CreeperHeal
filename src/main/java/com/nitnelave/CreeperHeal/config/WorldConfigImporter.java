package com.nitnelave.CreeperHeal.config;

import java.io.File;
import java.util.HashSet;

import org.bukkit.configuration.file.YamlConfiguration;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.BlockId;
import com.nitnelave.CreeperHeal.utils.CreeperLog;

abstract class WorldConfigImporter {

    private final static String[] STRING_BOOLEAN_OPTIONS = {"true", "false", "time"};
    private static boolean enderman, replaceAbove, blockLava, blockTNT, blockIgnite, griefBlockList, grassToDirt, blockSpawnEggs, blockPvP, warnLava, warnTNT,
            warnIgnite, warnBlackList, warnSpawnEggs, warnPvP, preventFireSpread, preventFireLava, creepers, tnt, fire, ghast, custom, dragons, wither,
            ignoreFactionsWilderness, ignoreFactionsTerritory;
    private static int replaceLimit, repairTime;
    private static HashSet<BlockId> blockBlackList, blockWhiteList, protectList, placeList;

    private static void storeSettings (WorldConfig w) {
        w.setBool (WCfgVal.ENDERMAN, enderman);
        w.setBool (WCfgVal.REPLACE_ABOVE, replaceAbove);
        w.setBool (WCfgVal.BLOCK_LAVA, blockLava);
        w.setBool (WCfgVal.BLOCK_TNT, blockTNT);
        w.setBool (WCfgVal.BLOCK_IGNITE, blockIgnite);
        w.setBool (WCfgVal.GRIEF_PLACE_LIST, griefBlockList);
        w.setBool (WCfgVal.GRASS_TO_DIRT, grassToDirt);
        w.setBool (WCfgVal.BLOCK_SPAWN_EGGS, blockSpawnEggs);
        w.setBool (WCfgVal.BLOCK_PVP, blockPvP);
        w.setBool (WCfgVal.WARN_LAVA, warnLava);
        w.setBool (WCfgVal.WARN_TNT, warnTNT);
        w.setBool (WCfgVal.WARN_IGNITE, warnIgnite);
        w.setBool (WCfgVal.WARN_BLACKLIST, warnBlackList);
        w.setBool (WCfgVal.WARN_SPAWN_EGGS, warnSpawnEggs);
        w.setBool (WCfgVal.WARN_PVP, warnPvP);
        w.setBool (WCfgVal.PREVENT_FIRE_SPREAD, preventFireSpread);
        w.setBool (WCfgVal.PREVENT_FIRE_LAVA, preventFireLava);
        w.setBool (WCfgVal.CREEPERS, creepers);
        w.setBool (WCfgVal.TNT, tnt);
        w.setBool (WCfgVal.FIRE, fire);
        w.setBool (WCfgVal.GHAST, ghast);
        w.setBool (WCfgVal.CUSTOM, custom);
        w.setBool (WCfgVal.WITHER, wither);
        w.setBool (WCfgVal.DRAGONS, dragons);
        w.setBool (WCfgVal.FACTIONS_IGNORE_WILDERNESS, ignoreFactionsWilderness);
        w.setBool (WCfgVal.FACTIONS_IGNORE_TERRITORY, ignoreFactionsTerritory);
        w.setInt (WCfgVal.REPLACE_LIMIT, replaceLimit);
        w.setInt (WCfgVal.REPAIR_TIME, repairTime);
        w.setList (WCfgVal.REPLACE_BLACK_LIST, blockBlackList);
        w.setList (WCfgVal.REPLACE_WHITE_LIST, blockWhiteList);
        w.setList (WCfgVal.PROTECTED_LIST, protectList);
        w.setList (WCfgVal.GRIEF_PLACE_LIST, placeList);

    }

    private static void from4 (String name) {

        YamlConfiguration config = new YamlConfiguration ();
        try
        {
            config.load (new File (CreeperHeal.getCHFolder () + "/config.yml"));
        } catch (Exception e)
        {
            e.printStackTrace ();
            return;
        }

        creepers = getStringBoolean (name + ".Creepers", "true", config);
        tnt = getStringBoolean (name + ".TNT", "true", config);
        fire = getStringBoolean (name + ".Fire", "true", config);
        ghast = getStringBoolean (name + ".Ghast", "true", config);
        custom = getStringBoolean (name + ".Magical", "false", config);
        replaceAbove = ConfigUpdater.getBoolean (config, name + ".replace-above-limit-only", false);
        replaceLimit = ConfigUpdater.getInt (config, name + ".replace-limit", 64);
        enderman = ConfigUpdater.getBoolean (config, name + ".block-enderman-pickup", false);
        dragons = getStringBoolean (name + ".dragons", "false", config);
        repairTime = ConfigUpdater.getInt (config, name + ".repair-time", -1);

        HashSet<BlockId> restrict_list = new HashSet<BlockId> ();
        try
        {
            String tmp_str1 = config.getString (name + ".restrict-list", "").trim ();
            String[] split = tmp_str1.split (",");
            for (String elem : split)
                restrict_list.add (new BlockId (elem));
        } catch (NumberFormatException e)
        {
            CreeperLog.warning ("[CreeperHeal] Wrong values for restrict-list field for world " + name);
            restrict_list.clear ();
            restrict_list.add (new BlockId (0));
        }
    }

    private static void from6 (String name) {
        File configFile = new File (CreeperHeal.getCHFolder ().getPath () + "/" + name + ".yml");
        if (!configFile.exists ())
            return;
        YamlConfiguration config = new YamlConfiguration ();
        try
        {
            config.load (configFile);
        } catch (Exception e)
        {
            e.printStackTrace ();
            return;
        }

        creepers = ConfigUpdater.getBoolean (config, "replace.Creepers", true);
        tnt = ConfigUpdater.getBoolean (config, "replace.TNT", true);
        ghast = ConfigUpdater.getBoolean (config, "replace.Ghast", true);
        dragons = ConfigUpdater.getBoolean (config, "replace.Dragons", false);
        custom = ConfigUpdater.getBoolean (config, "replace.Magical", false);
        fire = ConfigUpdater.getBoolean (config, "replace.Fire", true);
        enderman = ConfigUpdater.getBoolean (config, "replace.Enderman", true);
        replaceAbove = ConfigUpdater.getBoolean (config, "replace.replace-above-limit-only", false);
        replaceLimit = ConfigUpdater.getInt (config, "replace.replace-limit", 64);
        blockBlackList = loadList (config, "replace.restrict.blacklist");
        blockWhiteList = loadList (config, "replace.restrict.whitelist");
        protectList = loadList (config, "replace.protect-list");
        repairTime = ConfigUpdater.getInt (config, "replace.repair-time-of-day", -1);
        wither = ConfigUpdater.getBoolean (config, "replace.Wither", true);
        ignoreFactionsWilderness = ConfigUpdater.getBoolean (config, "replace.factions.ignore-wilderness", false);
        ignoreFactionsTerritory = ConfigUpdater.getBoolean (config, "replace.factions.ignore-territory", false);
        grassToDirt = ConfigUpdater.getBoolean (config, "replace.replace-grass-with-dirt", false);
        blockLava = ConfigUpdater.getBoolean (config, "grief.block.lava", false);
        blockTNT = ConfigUpdater.getBoolean (config, "grief.block.TNT", false);
        blockIgnite = ConfigUpdater.getBoolean (config, "grief.block.flint-and-steel", false);
        griefBlockList = ConfigUpdater.getBoolean (config, "grief.block.blacklist", false);
        blockSpawnEggs = ConfigUpdater.getBoolean (config, "grief.block.spawn-eggs", false);
        blockPvP = ConfigUpdater.getBoolean (config, "grief.block.PvP", false);
        warnLava = ConfigUpdater.getBoolean (config, "grief.warn.lava", false);
        warnTNT = ConfigUpdater.getBoolean (config, "grief.warn.TNT", false);
        warnIgnite = ConfigUpdater.getBoolean (config, "grief.warn.flint-and-steel", false);
        warnBlackList = ConfigUpdater.getBoolean (config, "grief.warn.blacklist", false);
        warnSpawnEggs = ConfigUpdater.getBoolean (config, "grief.warn.spawn-eggs", false);
        warnPvP = ConfigUpdater.getBoolean (config, "grief.warn.PvP", false);
        preventFireSpread = ConfigUpdater.getBoolean (config, "grief.prevent-fire-spread.fire", false);
        preventFireLava = ConfigUpdater.getBoolean (config, "grief.prevent-fire-spread.lava", false);
        placeList = loadList (config, "grief.blacklist");

        configFile.delete ();
    }

    private static HashSet<BlockId> loadList (YamlConfiguration config, String key) {
        HashSet<BlockId> set = new HashSet<BlockId> ();
        String tmp_str1 = config.getString (key, "").trim ();
        String[] split = tmp_str1.split (",");
        try
        {
            for (String elem : split)
            {
                BlockId bId = new BlockId (elem);
                if (bId.getId () != 0)
                    set.add (bId);
            }
            return set;
        } catch (NumberFormatException e)
        {
            return new HashSet<BlockId> ();
        }
    }

    protected static WorldConfig importFrom (String name, int version) {
        WorldConfig w = new WorldConfig (name);
        CreeperLog.logInfo ("Importing settings for world : " + name, 1);
        switch (version)
        {
            case 4:
                from4 (name);
                break;
            case 5:
            case 6:
            case 7:
                from6 (name);
                break;
        }
        storeSettings (w);
        return w;
    }

    private static boolean getStringBoolean (String path, String defaultValue, YamlConfiguration configFile) {
        String result = "";
        try
        {
            result = configFile.getString (path, defaultValue).trim ().toLowerCase ();
        } catch (Exception e)
        {
            CreeperLog.warning ("[CreeperHeal] Wrong value for " + path + " field. Defaulting to " + defaultValue + ".");
            CreeperLog.warning (e.getLocalizedMessage ());
            result = defaultValue;
        }

        boolean correct = false;
        for (int i = 0; i <= 2; i++)
            correct = correct || STRING_BOOLEAN_OPTIONS[i].equalsIgnoreCase (result);

        if (!correct)
        {
            CreeperLog.warning ("[CreeperHeal] Wrong value for " + path + " field. Defaulting to " + defaultValue + ".");
            return !defaultValue.equalsIgnoreCase ("false");
        }
        return !result.equalsIgnoreCase ("false");
    }
}
