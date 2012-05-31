package com.nitnelave.CreeperHeal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;


public class WorldConfig {

	public boolean enderman, replaceAbove, blockLava, blockTNT, blockIgnite, blockBlackList, 
	blockSpawnEggs, blockPvP, warnLava, warnTNT, warnIgnite, warnBlackList, warnSpawnEggs, warnPvP, preventFireSpread, preventFireLava,
	creepers, tnt, fire, ghast, magical, dragons;
	public String name;
	public int repairTime, replaceLimit;
	public ArrayList<BlockId> blockList = new ArrayList<BlockId>(), placeList = new ArrayList<BlockId>();
	private File pluginFolder;
	private YamlConfiguration config;
	protected final Logger log = Logger.getLogger("Minecraft");            //to output messages to the console/log




	public WorldConfig(String name, File folder) throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.name = name;
		if(folder != null)
		{
			pluginFolder = folder;
			load();
		}
		else
		{
			creepers = tnt = ghast = fire = true;
			magical = dragons = replaceAbove = enderman = blockLava = blockTNT = blockIgnite = blockBlackList = blockSpawnEggs = blockPvP= 
					warnLava = warnTNT = warnIgnite = warnBlackList = warnSpawnEggs = warnPvP = preventFireSpread = preventFireLava = false;
			replaceLimit = 60;
			repairTime = -1;
			blockList = new ArrayList<BlockId>();        //sample whitelist
			int[] tmp_list = { 1,2,3,9,11,12,13,14,15,16,17,18,21,24,31,32,37,38,39,40,48,49,56,73,79,81,82,86,87,88,89 };
			for(int k : tmp_list)
				blockList.add(new BlockId(k));
		}
	}

	
	@SuppressWarnings("unchecked")
	public WorldConfig(String name, Object... l) {
		this.name = name;
		creepers =  (Boolean) l[0];
		tnt = (Boolean) l[1];
		ghast = (Boolean) l[2];
		dragons = (Boolean) l[3];
		magical = (Boolean) l[4];
		fire = (Boolean) l[5];
		enderman = (Boolean) l[6];
		replaceAbove = (Boolean) l[7];
		replaceLimit = (Integer) l[8]; 
		blockList = (ArrayList<BlockId>) l[9];
		repairTime = (Integer) l[10];
		blockLava = blockTNT = blockIgnite = blockBlackList = blockSpawnEggs = blockPvP = warnLava = 
		warnTNT = warnIgnite = warnBlackList = warnSpawnEggs = warnPvP = preventFireSpread = preventFireLava = false;
		placeList = new ArrayList<BlockId>();
	}



	public String getName() {
		return name;
	}

	public String formatList(ArrayList<BlockId> list)
	{
		String blocklist = "";
		for(BlockId block : list)
			blocklist += block.toString() + ", ";

		return blocklist.substring(0, blocklist.length() - 2);

	}

	public boolean isRepairTimed()
	{
		return repairTime > -1;
	}

	public void load() throws FileNotFoundException, IOException, InvalidConfigurationException
	{
		File configFile = new File(pluginFolder.getPath() + "/" + name + ".yml");
		if(!configFile.exists())
			configFile.createNewFile();
		config = new YamlConfiguration();
		config.load(configFile);
		
		
		creepers = getBoolean("replace.Creepers", true);
		tnt = getBoolean("replace.TNT", true);
		ghast = getBoolean("replace.Ghast", true);
		dragons = getBoolean("replace.Dragons", false);
		magical = getBoolean("replace.Magical", false);
		fire = getBoolean("replace.Fire", true);
		enderman = getBoolean("replace.Enderman", true);
		replaceAbove = getBoolean("replace.replace-above-limit-only", false);
		replaceLimit = getInt("replace.replace-limit", 64);
		blockList = loadList("replace.restrict-list");
		repairTime = getInt("replace.repair-time-of-day", -1);
		blockLava = getBoolean("grief.block.lava", false);
		blockTNT = getBoolean("grief.block.TNT", false);
		blockIgnite = getBoolean("grief.block.flint-and-steel", false);
		blockBlackList = getBoolean("grief.block.blacklist", false);
		blockSpawnEggs = getBoolean("grief.block.spawn-eggs", false);
		blockPvP = getBoolean("grief.block.PvP", false);
		warnLava = getBoolean("grief.warn.lava", false);
		warnTNT = getBoolean("grief.warn.TNT", false);
		warnIgnite = getBoolean("grief.warn.flint-and-steel", false);
		warnBlackList = getBoolean("grief.warn.blacklist", false);
		warnSpawnEggs = getBoolean("grief.warn.spawn-eggs", false);
		warnPvP = getBoolean("grief.warn.PvP", false);
		preventFireSpread = getBoolean("grief.prevent-fire-spread.fire", false);
		preventFireLava = getBoolean("grief.prevent-fire-spread.lava", false);
		placeList = loadList("grief.blacklist"); 

		
	}
	
	public void save() throws IOException
	{
		set("replace.Creepers", creepers);
		set("replace.TNT", tnt);
		set("replace.Ghast", ghast);
		set("replace.Dragons", dragons);
		set("replace.Magical", magical);
		set("replace.Fire", fire);
		set("replace.Enderman", enderman);
		set("replace.replace-above-limit-only", replaceAbove);
		set("replace.replace-limit", replaceLimit);
		set("replace.restrict-list", formatList(blockList));
		set("replace.repair-time-of-day", repairTime);
		set("grief.block.lava", blockLava);
		set("grief.block.TNT", blockTNT);
		set("grief.block.flint-and-steel", blockIgnite);
		set("grief.block.blacklist", blockBlackList);
		set("grief.block.spawn-eggs", blockSpawnEggs);
		set("grief.block.PvP", blockPvP);
		set("grief.warn.lava", warnLava);
		set("grief.warn.TNT", warnTNT);
		set("grief.warn.flint-and-steel", warnIgnite);
		set("grief.warn.blacklist", warnBlackList);
		set("grief.warn.spawn-eggs", warnSpawnEggs);
		set("grief.warn.PvP", warnPvP);
		set("grief.prevent-fire-spread.fire", preventFireSpread);
		set("grief.prevent-fire-spread.lava", preventFireLava);
		set("grief.blacklist", formatList(placeList)); 
		
		config.save(pluginFolder.getPath() + "/" + name + ".yml");
	}

	private void set(String path, Object value)
    {
		config.set(path, value);
    }

	private ArrayList<BlockId> loadList(String path)
    {
		ArrayList<BlockId> returnList  = new ArrayList<BlockId>();
		try{
			String tmp_str1 = config.getString(path, "").trim();
			String[] split = tmp_str1.split(",");
			for(String elem : split) {
				returnList.add(new BlockId(elem));
			}
		}
		catch (NumberFormatException e) {
			log.warning("[CreeperHeal] Wrong values for " + path + " field for world " + name);
			returnList.clear();
			returnList.add(new BlockId(0));
		}
		return returnList;
    }

	private int getInt(String path, int def)
    {
	    int tmp;
		try {
			tmp = config.getInt(path, def);
		}
		catch(Exception e) {
			log.warning("[CreeperHeal] Wrong value for " + path + " field in world " + name + ". Defaulting to " + Integer.toString(def));
			tmp = def;
		}
		return tmp;
    }

	private boolean getBoolean(String path, boolean def)
    {
		boolean tmp;
		try {
			tmp = config.getBoolean(path, def);
		}
		catch(Exception e) {
			log.warning("[CreeperHeal] Wrong value for " + path + " field in world " + name + ". Defaulting to " + Boolean.toString(def));
			tmp = def;
		}
		return tmp;
    }

}