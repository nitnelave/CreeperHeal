package com.nitnelave.CreeperHeal.block;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;

/**
 * Represents a hanging, i.e. either a painting or an ItemFrame.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperHanging implements Replaceable {
    protected final Hanging hanging;
    protected final Location location;

    /**
     * Constructor. The date is the time the hanging was destroyed, and fire is
     * whether the hanging was destroyed by fire, or an explosion.
     * 
     * @param hanging
     *            The hanging destroyed.
     * @param time
     *            The time of the destruction.
     * @param fire
     *            Whether the hanging was destroyed by fire.
     */
    protected CreeperHanging (Hanging hanging) {
        this.hanging = hanging;
        location = computeLocation ();
    }

    public static CreeperHanging newHanging (Hanging hanging) {
        if (hanging instanceof Painting)
            return new CreeperPainting ((Painting) hanging);
        if (hanging instanceof ItemFrame)
            return new CreeperItemFrame ((ItemFrame) hanging);
        return null;
    }

    /**
     * Get the location of the painting, so that it is right in front of the
     * block it should be attached to.
     * 
     * @return The location of the painting.
     */
    protected abstract Location computeLocation ();

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getWorld()
     */
    @Override
    public World getWorld () {
        return hanging.getWorld ();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getLocation()
     */
    @Override
    public Location getLocation () {
        return location;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getBlock()
     */
    @Override
    public Block getBlock () {
        return hanging.getLocation ().getBlock ();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getAttachingFace()
     */
    @Override
    public BlockFace getAttachingFace () {
        return hanging.getAttachedFace ();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#isDependent()
     */
    @Override
    public boolean isDependent () {
        return true;
    }

    @Override
    public void remove () {
        hanging.remove ();
    }

}
