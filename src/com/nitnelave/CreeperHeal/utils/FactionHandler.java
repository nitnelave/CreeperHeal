package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.nitnelave.CreeperHeal.config.WorldConfig;

public class FactionHandler {

	private boolean isFactionsEnabled = false;

	public FactionHandler(PluginManager pluginManager) {
		P factions = (P) pluginManager.getPlugin("Factions");
		isFactionsEnabled = factions != null;
	}
	
	public boolean shouldIgnore(Location location, WorldConfig world) {
		if (!isFactionsEnabled || !world.ignoreFactionsWilderness) {
			return false;
		}
		
		return Board.getFactionAt(new FLocation(location)).isNone();	//don't replace if in Wilderness
	}

	public boolean isFactionsEnabled() {
		return isFactionsEnabled;
	}

}
