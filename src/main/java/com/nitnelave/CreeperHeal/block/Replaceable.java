package com.nitnelave.CreeperHeal.block;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.nitnelave.CreeperHeal.events.CHBlockHealEvent.CHBlockHealReason;

/**
 * Interface for replaceable items (blocks, paintings).
 * 
 * @author nitnelave
 * 
 */
public interface Replaceable
{

    /**
     * Replace the block in the world. In case of a dependent item whose support
     * is absent, the boolean shouldDrop determines whether the replacement is
     * postponed or if the item should be dropped.
     * 
     * @param shouldDrop
     *            If true, the item will drop, otherwise its replacement will be
     *            postponed.
     * @return False if the replacement was postponed.
     */
    public boolean replace(boolean shouldDrop);

    /**
     * Get the block represented.
     * 
     * @return The block represented.
     */
    public Block getBlock();

    /**
     * Get the world containing the block.
     * 
     * @return The world containing the block.
     */
    public World getWorld();

    /**
     * Get the type id of the block represented.
     * 
     * @return The type id of the block represented.
     */
    public int getTypeId();

    /**
     * Get the face the block is attached by. SELF if the block is not
     * dependent.
     * 
     * @return The blockFace the block is attached by, SELF if the block is not
     *         dependent.
     */
    public BlockFace getAttachingFace();

    /**
     * Get the block's location.
     * 
     * @return The block's location.
     */
    public Location getLocation();

    /**
     * Get whether the block depends on another block.
     * 
     * @return Whether the block depends on another block.
     */
    public boolean isDependent();

    /**
     * Drop the item on the ground.
     * 
     * @param forced
     *            If false, the block is dropped only according to the drop
     *            chance in the config.
     * @return True if the block was dropped.
     */
    public boolean drop(boolean forced);

    /**
     * Remove the block from the world.
     */
    public void remove();

    /**
     * Delay the block's replacement until it is possible for it to spawn, or
     * drop it after a reasonable amount of tries.
     * 
     * @param reason
     *            How was this block replaced
     */
    public void delayReplacement(CHBlockHealReason reason);
}
