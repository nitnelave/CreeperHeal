package com.nitnelave.CreeperHeal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.yi.acru.bukkit.Lockette.Lockette;

import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.FactionHandler;
import com.nitnelave.CreeperTrap.CreeperTrap;

public class PluginHandler {


	private static MobArenaHandler maHandler = null;		//handler to detect mob arenas
	private static LWC lwc = null;			//handler for LWC protection

	private static FactionHandler factionHandler;
	private static boolean playerHeads = false;

	public static void init() {

		/*
		 * Connection with the other plugins
		 */

		CreeperLog.logInfo("Connection with other plugins", 3);

		PluginManager pm = Bukkit.getServer().getPluginManager(); 



		Plugin lwcPlugin = pm.getPlugin("LWC");
		if(lwcPlugin != null) {
			lwc = ((LWCPlugin) lwcPlugin).getLWC();
			CreeperLog.logInfo("Successfully hooked in LWC", 1);
		}

		Plugin lockettePlugin = pm.getPlugin("Lockette");
		if(lockettePlugin!=null){
			CreeperConfig.lockette  = true;
			CreeperLog.logInfo("Successfully detected Lockette", 1);
		}


		Plugin mobArena = pm.getPlugin("MobArena");
		if(mobArena != null) {
			maHandler = new MobArenaHandler();
			CreeperLog.logInfo("Successfully hooked in MobArena", 1);
		}

		Plugin cTrap = pm.getPlugin("CreeperTrap");
		if(cTrap != null)
		{
			new CreeperTrapHandler(CreeperHeal.getInstance(), (CreeperTrap) cTrap);
			CreeperLog.logInfo("Successfully hooked in CreeperTrap", 1);
		}
		else
			new CreeperTrapHandler(CreeperHeal.getInstance());

		factionHandler = new FactionHandler(pm);
		if (factionHandler.isFactionsEnabled()) {
			CreeperLog.logInfo("Successfully hooked in Factions", 1);
		}

		Plugin playerHeadsPlugin = pm.getPlugin("PlayerHeads");
		if(playerHeadsPlugin!=null){
			playerHeads   = true;
			CreeperLog.logInfo("Successfully detected PlayerHeads", 1);
		}
	}



	public static boolean isProtected(Block block){       //is the block protected?
		if(lwc!=null){                      //lwc gets the priority. BECAUSE!
			boolean protect = (lwc.findProtection(block)!=null);
			if(protect)
				CreeperLog.logInfo("protected block : " + block.getType(), 1);
			return protect;
		}
		else if(CreeperConfig.lockette){                  //and then lockette
			return Lockette.isProtected(block);
		}
		else return false;
	}


	public static FactionHandler getFactionHandler() {
		return factionHandler;
	}

	public static boolean isInArena(Location location) {
		if(maHandler != null)
		{
			if (maHandler.inRegion(location)) 
				return true;		//Explosion inside a mob arena
		}
		return false;
	}



	public static boolean isPlayerHeadsActivated() {
		return playerHeads;
	}



}
