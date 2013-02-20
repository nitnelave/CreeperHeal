package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Represents a hanging, i.e. either a painting or an ItemFrame.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperHanging implements Replaceable {
    protected final Hanging hanging;
    private Date date;
    private final boolean fire;
    private boolean postPoned = false;
    private final boolean timed;
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
    protected CreeperHanging (Hanging hanging, Date time, boolean fire) {
        this.hanging = hanging;
        date = new Date (time.getTime () + 1000 * CreeperConfig.waitBeforeHealBurnt);
        this.fire = fire;
        location = computeLocation ();
        timed = CreeperConfig.loadWorld (getWorld ()).isRepairTimed ();
    }

    public static CreeperHanging newHanging (Hanging hanging, Date time, boolean fire) {
        if (hanging instanceof Painting)
            return new CreeperPainting ((Painting) hanging, time, fire);
        if (hanging instanceof ItemFrame)
            return new CreeperItemFrame ((ItemFrame) hanging, time, fire);
        return null;
    }

    /**
     * Get the location of the painting, so that it is right in front of the
     * block it should be attached to.
     * 
     * @return The location of the painting.
     */
    protected abstract Location computeLocation ();

    /**
     * Get the time the hanging was destroyed, or later if the replacement is
     * postponed.
     * 
     * @return
     */
    public Date getDate () {
        return date;
    }

    /**
     * Get whether the hanging was destroyed by fire.
     * 
     * @return Whether the hanging was destroyed by fire.
     */
    public boolean isBurnt () {
        return fire;
    }

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
     * Push back the date, and set the postponed flag to true.
     */
    private void postPone (int delay) {
        date = new Date (date.getTime () + 1000 * delay);
        postPoned = true;
    }

    /*
     * Postpone the replacement of the hanging to a later time, when the
     * block(s) supporting it may be present. Drop it if it has already been
     * postponed.
     */
    protected boolean postpone () {
        if (postPoned || (fire && !CreeperConfig.lightweightMode && BurntBlockManager.isIndexEmpty ()))
        {
            drop ();
            return true;
        }
        postPone (CreeperConfig.waitBeforeHealBurnt);
        return false;
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
    }

    public boolean checkReplace () {
        if (timed)
            return true;
        if (date.before (new Date ()))
        {
            replace (false);
            return true;
        }
        return false;
    }

}
