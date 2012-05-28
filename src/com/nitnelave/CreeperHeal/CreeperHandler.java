package com.nitnelave.CreeperHeal;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CreeperHandler
{

	private CreeperHeal plugin;
	
	
	public CreeperHandler(CreeperHeal instance)
    {
	   plugin = instance;
    }
	
	public void recordBlocks(List<Block> list)
	{
		recordBlocks(list, list.get(0).getLocation());
	}
	
	public void recordBlocks(List<Block> list, Location location)
	{
		plugin.recordBlocks(list, location);
	}

	public void recordBlocks(EntityExplodeEvent event) 
	{
		plugin.recordBlocks(event, plugin.loadWorld(event.getLocation().getWorld()));
	}
	
	public boolean shouldRemoveLWCProtection(Entity entity)
	{
		return !CreeperUtils.shouldReplace(entity, plugin.loadWorld(entity.getWorld()));
	}

}
