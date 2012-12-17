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
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nitnelave.CreeperHeal.block.BlockManager;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.CreeperBlock;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.command.CreeperCommand;
import com.nitnelave.CreeperHeal.command.CreeperCommandManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.economy.CreeperEconomy;
import com.nitnelave.CreeperHeal.listeners.CreeperBlockListener;
import com.nitnelave.CreeperHeal.listeners.CreeperListener;
import com.nitnelave.CreeperHeal.listeners.FancyListener;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer.WarningCause;



public class CreeperHeal extends JavaPlugin {
	

	/**
	 * Listeners
	 */

	protected CreeperListener listener = new CreeperListener(this);                        //listener for explosions
	private FancyListener fancyListener = new FancyListener();
	private CreeperBlockListener blockListener = new CreeperBlockListener();

	
	private static Map<CreeperBlock, Date> preventUpdate = Collections.synchronizedMap(new HashMap<CreeperBlock, Date>());
	private static Map<Location, Date> preventBlockFall = Collections.synchronizedMap(new HashMap<Location, Date>());
	private static List<CreeperPlayer> warnList = Collections.synchronizedList(new LinkedList<CreeperPlayer>()); 

	/**
	 * Handlers for misc. plugins
	 */



	protected CreeperCommandManager commandExecutor;
	private CreeperHandler handler;
	private CreeperPermissionManager perms;
	private static CreeperHeal instance;
	private BlockManager blockManager;




	public void onEnable() {

		instance = this;

		File warningLogFile = new File(getDataFolder()+"/log.txt");
		if(!warningLogFile.exists())
			try
		{
				warningLogFile.createNewFile();
		}
		catch (IOException e)
		{
			Logger.getLogger("Minecraft").warning(e.getMessage());
		}
		
		new CreeperConfig(this);

		new CreeperLog(warningLogFile);


		
		File file = new File(getDataFolder() + "/drops.yml");		//get the trap file
		file.delete();


		logInfo("Loaded config", 3);
		
		blockManager = new BlockManager(this);

		new CreeperMessenger(getDataFolder(), this);
		
		logInfo("Registering commands", 3);

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

		if(commandMap != null)
			commandMap.register("_", com);


		handler = new CreeperHandler();

		new CreeperEconomy(this);

		

		/*
		 * Recurrent tasks
		 */
		
		logInfo("Starting recurrent tasks", 3);

		int tmp_period = 20;        //register the task to go every "period" second if all at once
		if(CreeperConfig.blockPerBlock)                    //or every "block_interval" ticks if block_per_block
			tmp_period = CreeperConfig.blockPerBlockInterval;
		if( getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				ExplodedBlockManager.checkReplace(CreeperConfig.blockPerBlock);        //check to replace explosions/blocks
			}}, 200, tmp_period) == -1)
			CreeperLog.warning("[CreeperHeal] Impossible to schedule the re-filling task. Auto-refill will not work");

		if( getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				BurntBlockManager.replaceBurnt();
			}}, 200, 20) == -1)
			CreeperLog.warning("[CreeperHeal] Impossible to schedule the replace-burnt task. Burnt blocks replacement will not work");

		if( getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				cleanMaps();
			}}, 200, 2000) == -1)
			CreeperLog.warning("[CreeperHeal] Impossible to schedule the map-cleaning task. Map cleaning will not work");

		PluginManager pm = getServer().getPluginManager(); 

		PluginHandler.init();
		
		logInfo("Loading listeners", 3);

		pm.registerEvents(listener, this);
		pm.registerEvents(blockListener, this);

		if(!(CreeperConfig.lightweightMode))
			pm.registerEvents(fancyListener, this);
		
		populateWarnList();
		
		logInfo("CreeperHeal v" + getDescription().getVersion() + " enabled", 0);
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

			CreeperLog.warning("[CreeperHeal] Impossible to schedule the time-repair task. Time repairs will not work");

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
			BurntBlockManager.cleanIndex();
			
		}
	}


	public void onDisable() {
		for(WorldConfig w : CreeperConfig.world_config.values()) {
			ExplodedBlockManager.forceReplace(0, w);        //replace blocks still in memory, so they are not lost
			BurntBlockManager.forceReplaceBurnt(0, w);    //same for burnt_blocks
		}
		logInfo("CreeperHeal Disabled", 0);
	}



	public static void logInfo(String msg, int level) {        //logs a message, according to the log_level
		CreeperLog.logInfo(msg, level);
	}




	@Deprecated //Use static acces instead
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
			CreeperLog.record("[" + f.format(new Date()) + "] " + ChatColor.stripColor(message));
		message = ChatColor.RED + message;
		offender.sendMessage(CreeperMessenger.getMessage(cause, offender.getName(), offender.getWorld().getName(), blocked, material, true));
		for(CreeperPlayer cp : getWarnList())
		{
			cp.warnPlayer(cp.getPlayer(), cause, message);	
		}

	}

	public static Map<Location, Date> getPreventBlockFall() {
		return preventBlockFall;
	}

	public static Map<CreeperBlock, Date> getPreventUpdate() {
		return preventUpdate;
	}

	public static List<CreeperPlayer> getWarnList() {
		return warnList;
	}

	public static CreeperHeal getInstance() {
		return instance;
	}

}