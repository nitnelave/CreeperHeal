package com.nitnelave.CreeperHeal.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.nitnelave.CreeperHeal.utils.CreeperLog;

/**
 * The listener for CreatureSpawnEvents.
 */
public class CreatureSpawnListener implements Listener {

    //TODO: silverfish
    /**
     * Listen for silverfish spawning, and store the block to be replaced.
     * 
     * @param event
     *            The CreatureSpawn event.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreatureSpawn (CreatureSpawnEvent event) {
        if (event.isCancelled ())
            return;

        if (event.getEntityType () == EntityType.SILVERFISH)
            CreeperLog.debug ("silverfish spawned");
    }
}
