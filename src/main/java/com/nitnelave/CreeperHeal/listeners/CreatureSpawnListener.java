package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.nitnelave.CreeperHeal.utils.CreeperLog;

public class CreatureSpawnListener implements Listener {


	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if(event.isCancelled())
			return;
		
		if (event.getEntityType() == EntityType.SILVERFISH)
			CreeperLog.debug("silverfish spawned");
	}
}
