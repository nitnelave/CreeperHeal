package com.nitnelave.CreeperHeal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yi.acru.bukkit.Lockette.Lockette;

import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.nitnelave.CreeperHeal.block.BlockManager;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.command.CreeperCommand;
import com.nitnelave.CreeperHeal.command.CreeperCommandManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.economy.CreeperEconomy;
import com.nitnelave.CreeperHeal.listeners.CreeperListener;
import com.nitnelave.CreeperHeal.listeners.FancyListener;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;
import com.nitnelave.CreeperHeal.utils.FactionHandler;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer.WarningCause;
import com.nitnelave.CreeperTrap.CreeperTrap;



public class CreeperHeal extends JavaPlugin {
	

	/**
	 * Listeners
	 */

	protected CreeperListener listener = new CreeperListener(this);                        //listener for explosions
	private FancyListener fancyListener = new FancyListener(this);

	
	private Map<BlockState, Date> preventUpdate = Collections.synchronizedMap(new HashMap<BlockState, Date>());
	private Map<Location, Date> fireList = Collections.synchronizedMap(new HashMap<Location, Date>());
	private Map<Location, Date> preventBlockFall = Collections.synchronizedMap(new HashMap<Location, Date>());
	private static List<CreeperPlayer> warnList = Collections.synchronizedList(new LinkedList<CreeperPlayer>()); 

	/**
	 * Handlers for misc. plugins
	 */

	private MobArenaHandler maHandler = null;		//handler to detect mob arenas
	private static LWC lwc = null;			//handler for LWC protection




	public final static Logger log = Logger.getLogger("Minecraft");            //to output messages to the console/log
	protected CreeperConfig config;
	protected CreeperCommandManager commandExecutor;
	private CreeperHandler handler;
	private CreeperPermissionManager perms;
	private static CreeperLog warningLog;
	protected CreeperMessenger messenger;
	private FactionHandler factionHandler;
	private BlockManager blockManager;




	public void onEnable() {

		config = new CreeperConfig(this);
		
		blockManager = new BlockManager(this);

		messenger = new CreeperMessenger(getDataFolder(), this);

		commandExecutor = new CreeperCommandManager(this);
		perms = new CreeperPermissionManager(this);
		CommandMap commandMap = null;
		try{
			Field field = SimplePluginManager.class.getDeclaredField("commandMap");
			field.setAccessible(true);
			commandMap = (CommandMap)(field.get(getServer().getPluginManager()));
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}
		catch(IllegalAccessException e){
			e.printStackTrace();
		}

		String[] aliases = {"CreeperHeal",CreeperConfig.alias};
		CreeperCommand com = new CreeperCommand(aliases, "", "", commandExecutor);

		commandMap.register("_", com);


		handler = new CreeperHandler(this);

		new CreeperEconomy(this);

		File warningLogFile = new File(getDataFolder()+"/log.txt");
		if(!warningLogFile.exists())
			try
		{
				warningLogFile.createNewFile();
		}
		catch (IOException e)
		{
			log.log(Level.WARNING, e.getMessage());
		}

		warningLog = new CreeperLog(warningLogFile);



		/*
		 * Recurrent tasks
		 */

		int tmp_period = 20;        //register the task to go every "period" second if all at once
		if(CreeperConfig.blockPerBlock)                    //or every "block_interval" ticks if block_per_block
			tmp_period = CreeperConfig.blockPerBlockInterval;
		if( getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				ExplodedBlockManager.checkReplace(CreeperConfig.blockPerBlock);        //check to replace explosions/blocks
			}}, 200, tmp_period) == -1)
			log.warning("[CreeperHeal] Impossible to schedule the re-filling task. Auto-refill will not work");

		if( getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				BurntBlockManager.replaceBurnt();
			}}, 200, 20) == -1)
			log.warning("[CreeperHeal] Impossible to schedule the replace-burnt task. Burnt blocks replacement will not work");

		if( getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				cleanMaps();
			}}, 200, 200) == -1)
			log.warning("[CreeperHeal] Impossible to schedule the map-cleaning task. Map cleaning will not work");

		/*
		 * Connection with the other plugins
		 */

		PluginManager pm = getServer().getPluginManager(); 



		Plugin lwcPlugin = pm.getPlugin("LWC");
		if(lwcPlugin != null) {
			lwc = ((LWCPlugin) lwcPlugin).getLWC();
			log_info("Successfully hooked in LWC",0);
		}

		Plugin lockettePlugin = pm.getPlugin("Lockette");
		if(lockettePlugin!=null){
			CreeperConfig.lockette  = true;
			log_info("Successfully detected Lockette",0);
		}


		Plugin mobArena = pm.getPlugin("MobArena");
		if(mobArena != null) {
			maHandler = new MobArenaHandler();
			log_info("Successfully hooked in MobArena",0);
		}

		Plugin cTrap = pm.getPlugin("CreeperTrap");
		if(cTrap != null)
		{
			new CreeperTrapHandler(this, (CreeperTrap) cTrap);
			log_info("Successfully hooked in CreeperTrap", 0);
		}
		else
			new CreeperTrapHandler(this);

		factionHandler = new FactionHandler(pm);
		if (factionHandler.isFactionsEnabled()) {
			log_info("Successfully hooked in Factions",0);
		}

		pm.registerEvents(listener, this);

		if(!(CreeperConfig.lightweightMode))
			pm.registerEvents(fancyListener, this);

		populateWarnList();

	}

	private void populateWarnList()
	{
		getWarnList().clear();
		for(Player p : getServer().getOnlinePlayers())
		{
			if(CreeperPermissionManager.checkPermissions(p, false, "grief.warn.*", "grief.warn.lava", "grief.warn.fire", "grief.warn.tnt", "grief.warn.blacklist"))
			{
				getWarnList().add(new CreeperPlayer(p, this));
			}
		}
	}

	public void scheduleTimeRepairs()
	{
		if(getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				blockManager.checkReplaceTime();
			}}, 200, 1200) == -1)

			log.warning("[CreeperHeal] Impossible to schedule the time-repair task. Time repairs will not work");

	}



	protected void cleanMaps()
	{
		Date now = new Date();
		Date delay = new Date(now.getTime() - 7500*CreeperConfig.waitBeforeHeal);
		Iterator<Date> iter;
		synchronized (getPreventUpdate())
		{
			iter = getPreventUpdate().values().iterator();
			while(iter.hasNext())
			{
				Date date = iter.next();
				if(date.before(delay))
					iter.remove();
				else
					break;
			}
		}
		synchronized (getPreventBlockFall())
		{
			iter = getPreventBlockFall().values().iterator();
			while(iter.hasNext())
			{
				Date date = iter.next();
				if(date.before(delay))
					iter.remove();
				else
					break;
			}
		}
		if(!(CreeperConfig.lightweightMode))
		{
			synchronized (getFireList())
			{
				iter = getFireList().values().iterator();
				delay = new Date(now.getTime() - 1000 * CreeperConfig.waitBeforeHealBurnt);
				while(iter.hasNext())
				{

					Date date = iter.next();
					if(date.before(delay))
						iter.remove();
					else
						break;

				}

			}
		}
	}


	public void onDisable() {
		for(WorldConfig w : CreeperConfig.world_config.values()) {
			ExplodedBlockManager.forceReplace(0, w);        //replace blocks still in memory, so they are not lost
			BurntBlockManager.forceReplaceBurnt(0, w);    //same for burnt_blocks
		}
		log.info("[CreeperHeal] Disabled");
	}



	public static void log_info(String msg, int level) {        //logs a message, according to the log_level
		CreeperConfig.log_info(msg, level);
	}




	public static boolean isProtected(Block block){       //is the block protected?
		if(lwc!=null){                      //lwc gets the priority. BECAUSE!
			boolean protect = (lwc.findProtection(block)!=null);
			if(protect)
				log_info("protected block : " + block.getType(), 1);
			return protect;
		}
		else if(CreeperConfig.lockette){                  //and then lockette
			return Lockette.isProtected(block);
		}
		else return false;
	}


	public WorldConfig loadWorld(World w)
	{
		return CreeperConfig.loadWorld(w);
	}






	public CreeperHandler getHandler()
	{
		return handler;
	}

	protected CreeperPermissionManager getPermissionManager()
	{
		return perms;
	}



	public static void warn(WarningCause cause, Player offender,boolean blocked, String material)
	{
		String message = CreeperMessenger.getMessage(cause, offender.getName(), offender.getWorld().getName(), blocked, material, false);
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		if(CreeperConfig.logWarnings)
			warningLog.record("[" + f.format(new Date()) + "] " + ChatColor.stripColor(message));
		message = ChatColor.RED + message;
		offender.sendMessage(CreeperMessenger.getMessage(cause, offender.getName(), offender.getWorld().getName(), blocked, material, true));
		for(CreeperPlayer cp : getWarnList())
		{
			cp.warnPlayer(cp.getPlayer(), cause, message);	
		}

	}

	public FactionHandler getFactionHandler() {
		return factionHandler;
	}

	public boolean isInArena(Location location) {
		if(maHandler != null)
		{
			if (maHandler.inRegion(location)) 
				return true;		//Explosion inside a mob arena
		}
		return false;
	}

	public Map<Location, Date> getPreventBlockFall() {
		return preventBlockFall;
	}

	public Map<BlockState, Date> getPreventUpdate() {
		return preventUpdate;
	}

	public Map<Location, Date> getFireList() {
		return fireList;
	}

	public static List<CreeperPlayer> getWarnList() {
		return warnList;
	}

}