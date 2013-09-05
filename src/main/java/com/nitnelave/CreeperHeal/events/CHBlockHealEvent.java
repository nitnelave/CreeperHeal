package com.nitnelave.CreeperHeal.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.nitnelave.CreeperHeal.block.Replaceable;

public class CHBlockHealEvent extends Event implements Cancellable {
    private boolean cancelled = false, shouldDrop;
    private static final HandlerList handlers = new HandlerList ();
    private final CHBlockHealReason reason;
    private final Replaceable block;

    public CHBlockHealEvent (Replaceable block, boolean shouldDrop, CHBlockHealReason reason) {
        this.block = block;
        this.shouldDrop = shouldDrop;
        this.reason = reason;
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

    public boolean shouldDrop () {
        return shouldDrop;
    }

    public void setShouldDrop (boolean shouldDrop) {
        this.shouldDrop = shouldDrop;
    }

    public CHBlockHealReason getReason () {
        return reason;
    }

    public Replaceable getBlock () {
        return block;
    }

    public enum CHBlockHealReason {
        BURNT,
        BLOCK_BY_BLOCK,
        EXPLOSION,
        FORCED,
        PROTECTED,
        OTHER,
        CUSTOM
    }
}
