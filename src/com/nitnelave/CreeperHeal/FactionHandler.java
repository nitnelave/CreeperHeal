package com.nitnelave.CreeperHeal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;

public class FactionHandler {

	private boolean isFactionsEnabled = false;

	public FactionHandler(PluginManager pluginManager) {
		P factions = (P) pluginManager.getPlugin("Factions");
		isFactionsEnabled = factions != null;
	}
	
	public boolean shouldIgnore(List<Block> blockList, WorldConfig world) {
		if (!isFactionsEnabled || !world.ignoreFactionsWilderness) {
			return false;
		}
		
		Set<FLocation> explosionLocs = new HashSet<FLocation>();
		for (Block block : blockList)
		{
			explosionLocs.add(new FLocation(block));
		}
		for (FLocation loc : explosionLocs)
		{
			Faction faction = Board.getFactionAt(loc);
			if (!faction.isNone()) {
				return false;
			}
		}
		return true;
	}

	public boolean isFactionsEnabled() {
		return isFactionsEnabled;
	}

}
