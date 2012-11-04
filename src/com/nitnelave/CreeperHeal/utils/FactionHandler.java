package com.nitnelave.CreeperHeal.utils;

import java.util.List;

import org.bukkit.block.Block;
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
	
	public boolean shouldIgnore(List<Block> list, WorldConfig world) {
		if (!isFactionsEnabled || !world.ignoreFactionsWilderness) {
			return false;
		}
		
		for(Block block: list) {
			if (!Board.getFactionAt(new FLocation(block.getLocation())).isNone()) {
				return false;
			}
		}
		return true;
	}

	public boolean isFactionsEnabled() {
		return isFactionsEnabled;
	}

}
