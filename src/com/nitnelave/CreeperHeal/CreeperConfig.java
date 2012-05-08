package com.nitnelave.CreeperHeal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;



public class CreeperConfig
{

	private final static String[] world_config_nodes = {"replace.Creepers", "replace.TNT", "replace.Ghast", "replace.Dragons", "replace.Magical", "replace.Fire", "replace.Enderman",
		"replace.replace-all-TNT-blocks", "replace.replace-above-limit-only", "replace.replace-limit", "replace.use-restrict-list",
		"replace.restrict-list", "replace.repair-time-of-day", "grief.block.lava", "grief.block.TNT", "grief.block.flint-and-steel", 
		"grief.block.blacklist", "grief.block.spawn-eggs", "grief.warn.lava", "grief.warn.TNT", "grief.warn.flint-and-steel", "grief.warn.blacklist",
		"grief.warn.spawn-eggs", "grief.prevent-fire-spread.fire", "grief.prevent-fire-spread.lava", "grief.blacklist"}; //list of properties for the world config
	//private final static String[] world_config_nodes4 = {"Creepers", "TNT", "Ghast", "Magical", "Fire", "restrict-blocks", "restrict-list", "replace-all-tnt", "replace-above-limit-only", "replace-limit", "block-enderman-pickup", "dragons", "repair-time"}; //list of properties for the world config
	protected final Logger log = Logger.getLogger("Minecraft");            //to output messages to the console/log
	private final static String[] STRING_BOOLEAN_OPTIONS = {"true", "false", "time"};

	/**
	 * Config settings
	 */

	protected int waitBeforeHeal, logLevel, blockPerBlockInterval, waitBeforeHealBurnt, dropChance, distanceNear;
	protected boolean dropReplacedBlocks, blockPerBlock, teleportOnSuffocate, dropDestroyedBlocks, crackDestroyedBricks,
	lockette, replaceAllChests, replaceProtectedChests, overwriteBlocks, preventBlockFall, lightweightMode, opEnforce;

	protected String chestProtection, alias;		//no, lwc or lockette
	protected double configVersion;

	private CreeperHeal plugin;
	protected Map<String, WorldConfig> world_config = Collections.synchronizedMap(new HashMap<String, WorldConfig>());		//config for each world
	private FileConfiguration configFile;

	private File yml;

	public CreeperConfig(CreeperHeal instance)
	{
		plugin = instance;
		yml =  new File(getDataFolder()+"/config.yml");
		configFile = plugin.getConfig();

		if (!new File(getDataFolder().toString()).exists() ) {		//create the /CreeperHeal folder
			new File(getDataFolder().toString()).mkdir();
		}

		//File yml = new File(getDataFolder()+"/config.yml");

		if (!yml.exists()) {
			log.warning("[CreeperHeal] Config file not found, creating default.");
			copyJarConfig(yml);        //write the config with the default values.
		}

		load();
		write();

	}

	private void importFrom4()
	{
		log_info("Importing config from version 4",1);
		waitBeforeHeal = getInt("wait-before-heal-explosions", 60);        //tries to read the value directly from the config
		logLevel = getInt("verbose-level", 1);
		dropReplacedBlocks = getBoolean("drop-overwritten-blocks", true);
		String tmp_str;
		try{
			tmp_str = configFile.getString("replacement-method", "block-per-block").trim();
		}
		catch (Exception e) {
			log.warning("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
			log_info(e.getLocalizedMessage(), 1);
			tmp_str = "block-per-block";
		}
		if(!tmp_str.equalsIgnoreCase("all-at-once") && !tmp_str.equalsIgnoreCase("block-per-block"))
			log.warning("[CreeperHeal] Wrong value for replacement method field. Defaulting to block-per-block.");
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
		set("config-version", 5);
		try{
			tmp_str = configFile.getString("chest-protection", "no").trim().toLowerCase();
		}
		catch (Exception e) {
			log.warning("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
			log_info(e.getLocalizedMessage(), 1);
			tmp_str = "no";
		}

		if(!tmp_str.equalsIgnoreCase("no") && !tmp_str.equalsIgnoreCase("lwc") && !tmp_str.equalsIgnoreCase("all") && !tmp_str.equalsIgnoreCase("lockette"))
			log.warning("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
		else {
			replaceAllChests = replaceProtectedChests = false;

			if(tmp_str.equals("all"))
				replaceAllChests = true;
			else if(tmp_str.equals("lwc") || tmp_str.equals("lockette"))
				replaceProtectedChests = true;
		}
		chestProtection = tmp_str;
		boolean timeRepairs = false;
		world_config.clear();
		for(World w : plugin.getServer().getWorlds()) {
			String name = w.getName();
			timeRepairs = timeRepairs || importWorld(name).repairTime > -1;
		}
		if(timeRepairs)
			plugin.scheduleTimeRepairs();
	}

	private WorldConfig importWorld(String name)
	{
		WorldConfig returnValue = world_config.get(name);   

		if(returnValue == null){
			log_info("Importing settings for world: "+name, 1);
			boolean creeper = !getStringBoolean(name + ".Creepers", "true").equalsIgnoreCase("false");
			boolean tnt = !getStringBoolean(name + ".TNT", "true").equalsIgnoreCase("false");
			boolean fire = !getStringBoolean(name + ".Fire", "true").equalsIgnoreCase("false");
			boolean ghast = !getStringBoolean(name + ".Ghast", "true").equalsIgnoreCase("false");
			boolean magical = !getStringBoolean(name + ".Magical", "false" ).equalsIgnoreCase("false");
			boolean replace_tnt = getBoolean(name + ".replace-all-tnt", false);
			boolean replaceAbove = getBoolean(name + ".replace-above-limit-only", false);
			int replaceLimit = getInt(name + ".replace-limit", 64);
			boolean enderman = getBoolean(name + ".block-enderman-pickup", false);
			boolean dragons = !getStringBoolean(name + ".dragons", "false").equalsIgnoreCase("false");
			int wRepairTime = getInt(name + ".repair-time", -1);
			
			String restrict_blocks;
			restrict_blocks = configFile.getString(name + ".restrict-blocks", "false");
			if(!restrict_blocks.equalsIgnoreCase("false") && !restrict_blocks.equalsIgnoreCase("whitelist") && !restrict_blocks.equalsIgnoreCase("blacklist")) {
				log.warning("[CreeperHeal] Wrong value for " + name + ".restrict-blocks field. Defaulting to false.");
				restrict_blocks = "false";
			}
			
			ArrayList<BlockId> restrict_list  = new ArrayList<BlockId>();
			try{
				String tmp_str1 = configFile.getString(name + ".restrict-list", "").trim();
				String[] split = tmp_str1.split(",");
				for(String elem : split) {
					restrict_list.add(new BlockId(elem));
				}
			}
			catch (NumberFormatException e) {
				log.warning("[CreeperHeal] Wrong values for restrict-list field for world " + name);
				restrict_list.clear();
				restrict_list.add(new BlockId(0));
			}

			returnValue = new WorldConfig(name, creeper, tnt, ghast, dragons, magical, fire, enderman, replace_tnt, replaceAbove, replaceLimit, restrict_blocks, restrict_list, wRepairTime, false, false, false, false, false, false, false, false, false, false, false, false, new ArrayList<BlockId>());
			world_config.put(name, returnValue);
			return returnValue;
		}

		return returnValue;
	}

	public void load(){            //reads the config
		log_info("Loading config",1);
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

			String tmp_str;
			try{
				tmp_str = configFile.getString("replacement.chest-protection", "no").trim().toLowerCase();
			}
			catch (Exception e) {
				log.warning("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
				log_info(e.getLocalizedMessage(), 1);
				tmp_str = "no";
			}
			if(!tmp_str.equalsIgnoreCase("no") && !tmp_str.equalsIgnoreCase("lwc") && !tmp_str.equalsIgnoreCase("all") && !tmp_str.equalsIgnoreCase("lockette"))
				log.warning("[CreeperHeal] Wrong value for chest protection field. Defaulting to no.");
			else {
				replaceAllChests = replaceProtectedChests = false;

				if(tmp_str.equals("all"))
					replaceAllChests = true;
				else if(tmp_str.equals("lwc") || tmp_str.equals("lockette"))
					replaceProtectedChests = true;
			}
			chestProtection = tmp_str;
		}

		boolean timeRepairs = false;
		world_config.clear();
		for(World w : plugin.getServer().getWorlds()) {
			String name = w.getName();
			timeRepairs = timeRepairs || loadWorld(name).repairTime > -1;
		}

		if(timeRepairs)
			plugin.scheduleTimeRepairs();


	}

	public boolean getBoolean(String path, boolean def) {        //read a boolean from the config
		boolean tmp;
		try {
			tmp = configFile.getBoolean(path, def);
		}
		catch(Exception e) {
			log.warning("[CreeperHeal] Wrong value for " + path + " field. Defaulting to " + Boolean.toString(def));
			tmp = def;
		}
		return tmp;
	}

	public int getInt(String path, int def) {
		int tmp;
		try {
			tmp = configFile.getInt(path, def);
		}
		catch(Exception e) {
			log.warning("[CreeperHeal] Wrong value for " + path + " field. Defaulting to " + Integer.toString(def));
			tmp = def;
		}
		return tmp;
	}








	public void write(){            //write the config to a file, with the values used, or the default ones
		File yml = new File(getDataFolder()+"/config.yml");

		if(!yml.exists()){
			new File(getDataFolder().toString()).mkdir();
			try {
				yml.createNewFile();
			}
			catch (IOException ex) {
				log.warning("[CreeperHeal] Cannot create file "+yml.getPath());
			}
		}


		set("replacement.wait-before-heal.explosions", waitBeforeHeal);
		set("replacement.wait-before-heal.fire", waitBeforeHealBurnt);
		set("replacement.block-per-block.enabled", blockPerBlock);
		set("replacement.block-per-block.interval", blockPerBlockInterval);
		set("replacement.chest-protection", chestProtection );
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
		set("config-version", configVersion);




		for(WorldConfig w : world_config.values()) {
			String name = w.getName();

			int k = 0;

			ArrayList<Object> node_list = w.getConfig();

			for(String property : world_config_nodes)
				set( name + "." + property, node_list.get(k++));
		}

		try
		{
			configFile.save(yml);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void set(String string, Object o)
	{
		configFile.set(string, o);
	}

	public WorldConfig loadWorld(World world) {

		return loadWorld(world.getName());
	}


	public WorldConfig loadWorld(String name) {      //loads the world (for example, the first we need it)

		WorldConfig returnValue = world_config.get(name);   

		if(returnValue == null){
			log_info("Loading world: "+name, 2);
			boolean creeper = getBoolean(name + ".replace.Creepers", true);
			boolean tnt = getBoolean(name + ".replace.TNT", true);
			boolean ghast = getBoolean(name + ".replace.Ghast", true);
			boolean dragons = getBoolean(name + ".replace.Dragons", false);
			boolean magical = getBoolean(name + ".replace.Magical", false);
			boolean fire = getBoolean(name + ".replace.Fire", true);
			boolean enderman = getBoolean(name + ".replace.Enderman", false);

			String restrictBlocks = configFile.getString(name + ".replace.use-restrict-list", "false");
			if(!restrictBlocks.equalsIgnoreCase("false") && !restrictBlocks.equalsIgnoreCase("whitelist") && !restrictBlocks.equalsIgnoreCase("blacklist")) {
				log.warning("[CreeperHeal] Wrong value for use-restrict-list field for world " + name + ". Defaulting to false.");
				restrictBlocks = "false";
			}
			
			ArrayList<BlockId> restrictList  = new ArrayList<BlockId>();
			try{
				String tmp_str1 = configFile.getString(name + ".replace.restrict-list", "0");
				String[] split = tmp_str1.split(",");
				if(split!=null){        //split the list into single strings of integer
					for(String elem : split) {
						restrictList.add(new BlockId(elem));
					}
				}
			}
			catch (NumberFormatException e) {
				log.warning("[CreeperHeal] Wrong values for restrict-list field for world " + name);
				restrictList.clear();
				restrictList.add(new BlockId(0));
			}

			boolean replaceTNT = getBoolean(name + ".replace.replace-all-TNT-blocks", false);
			boolean replaceAbove = getBoolean(name + ".replace.replace-above-limit-only", false);
			int replaceLimit = getInt(name + ".replace.replace-limit", 64);
			int repairTime = getInt(name + ".replace.repair-time-of-day", -1);
			boolean preventFireSpread = getBoolean(name + ".grief.prevent-fire-spread.fire", false);
			boolean preventFireLava = getBoolean(name + ".grief.prevent-fire-spread.lava", false);
			boolean blockLava = getBoolean(name + ".grief.block.lava", false);
			boolean warnLava = getBoolean(name + ".grief.warn.lava", false);
			boolean blockTNT = getBoolean(name + ".grief.block.TNT", false);
			boolean warnTNT = getBoolean(name + ".grief.warn.TNT", false);
			boolean blockIgnite = getBoolean(name + ".grief.block.flint-and-steel", false);
			boolean warnIgnite = getBoolean(name + ".grief.warn.flint-and-steel", false);
			boolean blockBlackList = getBoolean(name + ".grief.block.blacklist", false);
			boolean warnBlackList = getBoolean(name + ".grief.warn.blacklist", false);
			boolean blockSpawnEggs = getBoolean(name + ".grief.block.spawn-eggs", false);
			boolean warnSpawnEggs = getBoolean(name + ".grief.warn.spawn-eggs", false);

			ArrayList<BlockId> placeList  = new ArrayList<BlockId>();
			try{
				String tmp_str1 = configFile.getString(name + ".grief.blacklist", "");
				String[] split = tmp_str1.split(",");
				if(split!=null){        //split the list into single strings of integer
					for(String elem : split) {
						placeList.add(new BlockId(elem));
					}
				}
			}
			catch (NumberFormatException e) {
				log.warning("[CreeperHeal] Wrong values for restrict-list field for world " + name);
				placeList.clear();
				placeList.add(new BlockId(0));
			}
			
			returnValue = new WorldConfig(name, creeper, tnt, ghast, dragons, magical, fire, enderman, replaceTNT, replaceAbove, replaceLimit, 
					restrictBlocks, restrictList, repairTime, blockLava, blockTNT, blockIgnite, blockBlackList, blockSpawnEggs, 
					warnLava, warnTNT, warnIgnite, warnBlackList, warnSpawnEggs, preventFireSpread, preventFireLava, placeList);

			world_config.put(name, returnValue);
			return returnValue;
		}

		return returnValue;
	}

	private String getStringBoolean(String path, String defaultValue)
	{
		String result = new String();
		try{
			result = configFile.getString(path, defaultValue).trim().toLowerCase();
		}
		catch (Exception e) {
			log.warning("[CreeperHeal] Wrong value for "+path+" field. Defaulting to "+defaultValue+".");
			log_info(e.getLocalizedMessage(), 1);
			result = defaultValue;
		}

		boolean correct = false;
		for(int i = 0; i<= 2; i++)
			correct = correct || STRING_BOOLEAN_OPTIONS[i].equalsIgnoreCase(result);

		if(!correct)
		{
			log.warning("[CreeperHeal] Wrong value for "+path+" field. Defaulting to "+defaultValue+".");
			return defaultValue;
		}
		return result;
	}

	private File getDataFolder()
	{
		return plugin.getDataFolder();
	}

	public void log_info(String msg, int level)
	{
		if(level<=logLevel)
			log.info("[CreeperHeal] "+msg);
	}

	private void copyJarConfig(File file)
	{
		try {
			file.createNewFile();
            boolean success = false;
            InputStream templateIn = plugin.getResource("config.yml");
            OutputStream outStream = new FileOutputStream(file);
           
            int read = 0;
            byte[] bytes = new byte[1024];
       
            while ((read = templateIn.read(bytes)) != -1) {
                outStream.write(bytes, 0, read);
            }
       
            templateIn.close();
            outStream.flush();
            outStream.close();
            if (success) {
                log.info("[CreeperHeal] Default config created");
            }
        } catch (Exception e) {
        	log.warning("[CreeperHeal] Failed to create file: config.yml");
        	log.warning(e.getMessage());
        }
	}



}
