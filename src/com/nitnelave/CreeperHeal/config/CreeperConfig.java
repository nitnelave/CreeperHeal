package com.nitnelave.CreeperHeal.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.BlockId;
import com.nitnelave.CreeperHeal.utils.CreeperLog;



public class CreeperConfig
{

	private final static String[] STRING_BOOLEAN_OPTIONS = {"true", "false", "time"};

	/**
	 * Config settings
	 */

	public static int waitBeforeHeal, logLevel = -42, blockPerBlockInterval, waitBeforeHealBurnt, dropChance, distanceNear, obsidianChance, obsidianRadius;
	public static boolean dropReplacedBlocks, blockPerBlock, teleportOnSuffocate, dropDestroyedBlocks, crackDestroyedBricks,
		lockette, replaceAllChests, replaceProtectedChests, overwriteBlocks, preventBlockFall, lightweightMode, opEnforce, logWarnings, preventChainReaction, explodeObsidian, debug, playerHeads;

	public static String alias;		//no, lwc or lockette
	public static double configVersion;

	private static CreeperHeal plugin;
	public static Map<String, WorldConfig> world_config = Collections.synchronizedMap(new HashMap<String, WorldConfig>());		//config for each world
	private static FileConfiguration configFile;

	private static File yml;

	public CreeperConfig(CreeperHeal instance)
	{
		plugin = instance;
		yml =  new File(getDataFolder()+"/config.yml");
		configFile = plugin.getConfig();

		if (!new File(getDataFolder().toString()).exists() ) {		//create the /CreeperHeal folder
			new File(getDataFolder().toString()).mkdir();
		}

		if (!yml.exists()) {
			CreeperLog.warning("[CreeperHeal] Config file not found, creating default.");
			copyJarConfig(yml, "config.yml");        //write the config with the default values.
		}

		load();
		write();

	}

	private static void importFrom4()
	{
		CreeperLog.logInfo("Importing config from version 4",1);
		waitBeforeHeal = getInt("wait-before-heal-explosions", 60);        //tries to read the value directly from the config
		logLevel = getInt("verbose-level", 1);
		dropReplacedBlocks = getBoolean("drop-overwritten-blocks", true);
		String tmp_str;
		try{
			tmp_str = configFile.getString("replacement-method", "block-per-block").trim();
		}
		catch (Exception e) {
			CreeperLog.warning("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
			CreeperLog.logInfo(e.getLocalizedMessage(), 1);
			tmp_str = "block-per-block";
		}
		if(!tmp_str.equalsIgnoreCase("all-at-once") && !tmp_str.equalsIgnoreCase("block-per-block"))
			CreeperLog.warning("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
		blockPerBlock = (tmp_str.equalsIgnoreCase("all-at-once"))?false:true;
		teleportOnSuffocate = getBoolean("teleport-when-buried", true);
		waitBeforeHealBurnt = getInt("wait-before-heal-fire", 45);
		dropDestroyedBlocks = getBoolean("drop-destroyed-blocks", true);
		dropChance = getInt("drop-destroyed-blocks-chance", 100);
		opEnforce = getBoolean("op-have-all-permissions", true);
		crackDestroyedBricks = getBoolean("crack-destroyed-bricks", false);
		overwriteBlocks = getBoolean("overwrite-blocks", true);
		preventBlockFall = getBoolean("prevent-block-fall", true);
		distanceNear = getInt("distance-near", 20);
		lightweightMode = getBoolean("lightweight-mode", false);
		alias = configFile.getString("command-alias", "ch");
		configVersion = 5;
		logWarnings = true;
		debug = preventChainReaction = false;
		obsidianChance = 20;
		obsidianRadius = 5;
		explodeObsidian = false;
		set("advanced.log-warnings", true);
		set("config-version", 5);
		try{
			tmp_str = configFile.getString("chest-protection", "no").trim().toLowerCase();
		}
		catch (Exception e) {
			CreeperLog.warning("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
			CreeperLog.logInfo(e.getLocalizedMessage(), 1);
			tmp_str = "no";
		}

		if(!tmp_str.equalsIgnoreCase("no") && !tmp_str.equalsIgnoreCase("lwc") && !tmp_str.equalsIgnoreCase("all") && !tmp_str.equalsIgnoreCase("lockette"))
			CreeperLog.warning("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
		else {
			replaceAllChests = replaceProtectedChests = false;

			if(tmp_str.equals("all"))
				replaceAllChests = true;
			else if(tmp_str.equals("lwc") || tmp_str.equals("lockette"))
				replaceProtectedChests = true;
		}
		boolean timeRepairs = false;
		world_config.clear();
		for(World w : plugin.getServer().getWorlds()) {
			String name = w.getName();
			timeRepairs = timeRepairs || importWorld(name).repairTime > -1;
		}
		if(timeRepairs)
			plugin.scheduleTimeRepairs();
	}

	private static WorldConfig importWorld(String name)
	{
		WorldConfig returnValue = world_config.get(name);   

		if(returnValue == null){
			CreeperLog.logInfo("Importing settings for world: "+name, 1);
			boolean creeper = !getStringBoolean(name + ".Creepers", "true").equalsIgnoreCase("false");
			boolean tnt = !getStringBoolean(name + ".TNT", "true").equalsIgnoreCase("false");
			boolean fire = !getStringBoolean(name + ".Fire", "true").equalsIgnoreCase("false");
			boolean ghast = !getStringBoolean(name + ".Ghast", "true").equalsIgnoreCase("false");
			boolean magical = !getStringBoolean(name + ".Magical", "false" ).equalsIgnoreCase("false");
			boolean replaceAbove = getBoolean(name + ".replace-above-limit-only", false);
			int replaceLimit = getInt(name + ".replace-limit", 64);
			boolean enderman = getBoolean(name + ".block-enderman-pickup", false);
			boolean dragons = !getStringBoolean(name + ".dragons", "false").equalsIgnoreCase("false");
			int wRepairTime = getInt(name + ".repair-time", -1);

			HashSet<BlockId> restrict_list  = new HashSet<BlockId>();
			try{
				String tmp_str1 = configFile.getString(name + ".restrict-list", "").trim();
				String[] split = tmp_str1.split(",");
				for(String elem : split) {
					restrict_list.add(new BlockId(elem));
				}
			}
			catch (NumberFormatException e) {
				CreeperLog.warning("[CreeperHeal] Wrong values for restrict-list field for world " + name);
				restrict_list.clear();
				restrict_list.add(new BlockId(0));
			}

			returnValue = new WorldConfig(name, creeper, tnt, ghast, dragons, magical, fire, enderman, replaceAbove, replaceLimit, restrict_list, wRepairTime);
			world_config.put(name, returnValue);
			return returnValue;
		}

		return returnValue;
	}

	public static void load(){            //reads the config
		CreeperLog.logInfo("Loading config",2);
		try
		{
			configFile.load(new File(getDataFolder()+"/config.yml"));
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		catch (InvalidConfigurationException e1)
		{
			e1.printStackTrace();
		}

		configVersion = configFile.getDouble("config-version", 4);
		if(configVersion == 4)
			importFrom4();
		else
		{
			blockPerBlockInterval = getInt("replacement.block-per-block.interval", 20);
			waitBeforeHeal = getInt("replacement.wait-before-heal.explosions", 60);        //tries to read the value directly from the config
			logLevel = getInt("advanced.verbose-level", 1);
			dropReplacedBlocks = getBoolean("advanced.replacement-conflict.drop-overwritten-blocks", true);
			blockPerBlock = getBoolean("replacement.block-per-block", true);
			teleportOnSuffocate = getBoolean("advanced.teleport-when-buried", true);
			waitBeforeHealBurnt = getInt("replacement.wait-before-heal.fire", 45);
			dropDestroyedBlocks = getBoolean("advanced.drop-destroyed-blocks.enabled", true);
			dropChance = getInt("advanced.drop-destroyed-blocks.chance", 100);
			opEnforce = getBoolean("advanced.op-have-all-permissions", true);
			crackDestroyedBricks = getBoolean("replacement.crack-destroyed-bricks", false);
			overwriteBlocks = getBoolean("overwrite-blocks", true);
			preventBlockFall = getBoolean("advanced.prevent-block-fall", true);
			distanceNear = getInt("advanced.distance-near", 20);
			lightweightMode = getBoolean("advanced.lightweight-mode", false);
			alias = configFile.getString("advanced.command-alias", "ch");
			logWarnings =  getBoolean("advanced.log-warnings", true);
			replaceAllChests = getBoolean("replacement.ignore-chests.all", false);
			replaceProtectedChests = getBoolean("replacement.ignore-chests.public", false);
			preventChainReaction = getBoolean("advanced.prevent-chain-reaction", false);
			explodeObsidian = getBoolean("advanced.obsidian.explode", false);
			obsidianRadius = getInt("advanced.obsidian.radius", 5);
			obsidianChance = getInt("advanced.obsidian.chance", 20);
			debug = getBoolean("advanced.debug-messages", false);

		}

		boolean timeRepairs = false;
		world_config.clear();
		try
		{
			for(World w : plugin.getServer().getWorlds()) {
				String name = w.getName();
				WorldConfig world = new WorldConfig(name, getDataFolder());
				world_config.put(name, world);
				timeRepairs = timeRepairs || world.repairTime > -1;
			}
		}catch(Exception e)
		{
			CreeperLog.severe("[CreeperHeal] Could not load world configurations");
			CreeperLog.severe(e.getMessage());
		}

		if(timeRepairs)
			plugin.scheduleTimeRepairs();


	}

	public static boolean getBoolean(String path, boolean def) {        //read a boolean from the config
		boolean tmp;
		try {
			tmp = configFile.getBoolean(path, def);
		}
		catch(Exception e) {
			CreeperLog.warning("[CreeperHeal] Wrong value for " + path + " field. Defaulting to " + Boolean.toString(def));
			tmp = def;
		}
		return tmp;
	}

	public static int getInt(String path, int def) {
		int tmp;
		try {
			tmp = configFile.getInt(path, def);
		}
		catch(Exception e) {
			CreeperLog.warning("[CreeperHeal] Wrong value for " + path + " field. Defaulting to " + Integer.toString(def));
			tmp = def;
		}
		return tmp;
	}








	public static void write(){            //write the config to a file, with the values used, or the default ones
		File yml = new File(getDataFolder()+"/config.yml");

		if(!yml.exists()){
			new File(getDataFolder().toString()).mkdir();
			try {
				yml.createNewFile();
			}
			catch (IOException ex) {
				CreeperLog.warning("[CreeperHeal] Cannot create file "+yml.getPath());
			}
		}


		set("replacement.wait-before-heal.explosions", waitBeforeHeal);
		set("replacement.wait-before-heal.fire", waitBeforeHealBurnt);
		set("replacement.block-per-block.enabled", blockPerBlock);
		set("replacement.block-per-block.interval", blockPerBlockInterval);
		set("replacement.ignore-chests.all", replaceAllChests);
		set("replacement.ignore-chests.public", replaceProtectedChests);
		set("replacement.crack-destroyed-bricks", crackDestroyedBricks);
		set("advanced.replacement-conflict.overwrite", overwriteBlocks);
		set("advanced.replacement-conflict.drop-overwritten-blocks", dropReplacedBlocks);
		set("advanced.drop-destroyed-blocks.enabled", dropDestroyedBlocks);
		set("advanced.drop-destroyed-blocks.chance", dropChance);
		set("advanced.teleport-when-buried", teleportOnSuffocate);
		set("advanced.verbose-level", logLevel);
		set("advanced.op-have-all-permissions", opEnforce);
		set("advanced.prevent-block-fall", preventBlockFall);
		set("advanced.distance-near", distanceNear);
		set("advanced.lightweight-mode", lightweightMode);
		set("advanced.command-alias", alias);
		set("advanced.prevent-chain-reaction", preventChainReaction);
		set("advanced.log-warnings", logWarnings);
		set("config-version", configVersion);
		set("advanced.obsidian.explode", explodeObsidian);
		set("advanced.obsidian.radius", obsidianRadius);
		set("advanced.obsidian.chance", obsidianChance);
		set("advanced.debug-messages", debug);
		removeOldWorldConfig();


		try
		{
			for(WorldConfig w : world_config.values()) {
				w.save();
			}
			configFile.save(yml);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static void set(String string, Object o)
	{
		configFile.set(string, o);
	}

	public static WorldConfig loadWorld(World world) {

		String name = world.getName();
		WorldConfig returnValue = world_config.get(name);
		if(returnValue == null)
		{
			try
			{
				returnValue = new WorldConfig(name, getDataFolder());
			}
			catch (Exception e)
			{
				CreeperLog.severe("[CreeperHeal] Could not load configuration for world : " + name);
				CreeperLog.severe(e.getMessage());
			}
		}
		return returnValue;
	}



	private static String getStringBoolean(String path, String defaultValue)
	{
		String result = "";
		try{
			result = configFile.getString(path, defaultValue).trim().toLowerCase();
		}
		catch (Exception e) {
			CreeperLog.warning("[CreeperHeal] Wrong value for "+path+" field. Defaulting to "+defaultValue+".");
			CreeperLog.logInfo(e.getLocalizedMessage(), 1);
			result = defaultValue;
		}

		boolean correct = false;
		for(int i = 0; i<= 2; i++)
			correct = correct || STRING_BOOLEAN_OPTIONS[i].equalsIgnoreCase(result);

		if(!correct)
		{
			CreeperLog.warning("[CreeperHeal] Wrong value for "+path+" field. Defaulting to "+defaultValue+".");
			return defaultValue;
		}
		return result;
	}

	private static File getDataFolder()
	{
		return plugin.getDataFolder();
	}


	public static void copyJarConfig(File file, String resource)
	{
		OutputStream outStream = null;
		try {
			file.createNewFile();
			InputStream templateIn = plugin.getResource(resource);
			outStream = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = templateIn.read(bytes)) != -1) {
				outStream.write(bytes, 0, read);
			}

			templateIn.close();
			outStream.flush();
			outStream.close();
			CreeperLog.logInfo("[CreeperHeal] Default config created", 1);

		} catch (Exception e) {
			CreeperLog.warning("[CreeperHeal] Failed to create file: " + file.getName());
			CreeperLog.warning(e.getMessage());
			if(outStream != null)
			{
				try {
					outStream.flush();
					outStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private static void removeOldWorldConfig() {
		set("world", null);
	}



}
