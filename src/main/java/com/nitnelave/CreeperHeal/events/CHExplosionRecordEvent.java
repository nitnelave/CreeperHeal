package com.nitnelave.CreeperHeal.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CHExplosionRecordEvent extends Event implements Cancellable {
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList ();
    private List<Block> blocks;
    private final Location location;

    public CHExplosionRecordEvent (List<Block> blocks, Location location) {
        this.blocks = blocks;
        this.location = location;
    }

    @Override
    public boolean isCancelled () {
        return cancelled;
    }

    @Override
    public void setCancelled (boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers () {
        return handlers;
    }

    public static HandlerList getHandlerList () {
        return handlers;
    }

    public List<Block> getBlocks () {
        return blocks;
    }

    public void setBlocks (List<Block> blocks) {
        this.blocks = blocks;
    }

    public Location getLocation () {
        return location;
    }

}
