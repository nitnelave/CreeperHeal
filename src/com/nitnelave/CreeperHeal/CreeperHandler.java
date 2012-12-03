package com.nitnelave.CreeperHeal;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class CreeperHandler
{	
	
	public static void recordBlocks(List<Block> list)
	{
		recordBlocks(list, list.get(0).getLocation());
	}
	
	public static void recordBlocks(List<Block> list, Location location)
	{
		ExplodedBlockManager.recordBlocks(list, location);
	}

	public static void recordBlocks(EntityExplodeEvent event) 
	{
		ExplodedBlockManager.recordBlocks(event, CreeperConfig.loadWorld(event.getLocation().getWorld()));
	}
	
	public static boolean shouldRemoveLWCProtection(Entity entity)
	{
		return !CreeperConfig.loadWorld(entity.getWorld()).shouldReplace(entity);
	}

}
