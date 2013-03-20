package com.nitnelave.CreeperHeal.listeners;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.CreeperBurntBlock;
import com.nitnelave.CreeperHeal.block.CreeperHanging;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WCfgVal;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.FactionHandler;
import com.nitnelave.CreeperHeal.utils.Suffocating;

/**
 * Listener for the entity events.
 * 
 * @author nitnelave
 * 
 */
public class CreeperListener implements Listener {

    /**
     * Listener for the EntityExplodeEvent. Record when appropriate the
     * explosion for later replacement.
     * 
     * @param event
     *            The EntityExplode event.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode (EntityExplodeEvent event) {
        WorldConfig world = CreeperConfig.getWorld (event.getLocation ().getWorld ());

        if (!FactionHandler.shouldIgnore (event.blockList (), world))
        {
            Entity entity = event.getEntity ();
            if (world.shouldReplace (entity))
                ExplodedBlockManager.processExplosion (event, world);
        }
    }

    /**
     * Listener for the HangingBreakEvent. If appropriate, the hanging is
     * recorded to be replaced later on.
     * 
     * @param event
     *            The HangingBreakEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHangingBreak (HangingBreakEvent event) {
        Hanging h = event.getEntity ();
        WorldConfig world = CreeperConfig.getWorld (h.getWorld ());
        switch (event.getCause ())
        {
            case EXPLOSION:
                ExplodedBlockManager.recordHanging (h);
                break;
            case PHYSICS:
            case OBSTRUCTION:
                if (BurntBlockManager.isNextToFire (h.getLocation ()) && world.getBool (WCfgVal.FIRE))
                    BurntBlockManager.recordBurntBlock (new CreeperBurntBlock (new Date (), CreeperHanging.newHanging (h)));
                break;
            default:
        }

    }

    /**
     * Listener for the EntityChangeBlockEvent. Check for Endermen picking up
     * blocks.
     * 
     * @param event
     *            The EntityChangeBlock event.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock (EntityChangeBlockEvent event) {
        if (event.getEntityType () == EntityType.SILVERFISH && event.getBlock ().getType () == Material.MONSTER_EGGS
                && CreeperConfig.getBool (CfgVal.REPLACE_SILVERFISH_BLOCKS))
            Bukkit.getScheduler ().runTask (CreeperHeal.getInstance (), new ReplaceMonsterEgg (event.getBlock ()));
        else if (event.getEntity () instanceof Enderman)
        {
            WorldConfig world = CreeperConfig.getWorld (event.getBlock ().getWorld ());
            if (world.getBool (WCfgVal.ENDERMAN))
                event.setCancelled (true);
        }
    }

    class ReplaceMonsterEgg implements Runnable {
        private final Block block;
        private final Material type;

        public ReplaceMonsterEgg (Block block) {
            switch (block.getData ())
            {
                case 0:
                    type = Material.STONE;
                    break;
                case 1:
                    type = Material.COBBLESTONE;
                    break;
                default:
                    type = Material.SMOOTH_BRICK;
            }
            this.block = block;
        }

        @Override
        public void run () {
            block.setType (type);
            Suffocating.checkPlayerOneBlock (block.getLocation ());
        }
    }

}
