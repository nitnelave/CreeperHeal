package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;

/**
 * Painting implementation of the CreeperHanging. Represents a painting.
 * 
 * @author nitnelave
 * 
 */
public class CreeperPainting extends CreeperHanging {

    /**
     * Constructor.
     * 
     * @param painting
     *            The painting represented.
     * @param time
     *            The time of the explosion.
     * @param fire
     *            Whether the painting was burnt.
     */
    public CreeperPainting (Painting painting, Date time, boolean fire) {
        super (painting, time, fire);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperHanging#computeLocation()
     */
    @Override
    protected Location computeLocation () {
        BlockFace face = hanging.getAttachedFace ();
        Location loc = hanging.getLocation ().getBlock ().getRelative (face).getLocation ();
        Art art = ((Painting) hanging).getArt ();

        if (art.getBlockHeight () + art.getBlockWidth () < 5)
        {
            int i = 0, j = 0, k = art.getBlockWidth () - 1;
            switch (face)
            {
                case EAST:
                    j = -k;
                    break;
                case NORTH:
                    i = -k;
                default:
                    break;
            }
            loc.add (i, 1 - art.getBlockHeight (), j);
        }
        else
        {

            if (art.getBlockHeight () != 3)
                loc.add (0, -1, 0);
            switch (face)
            {
                case EAST:
                    loc.add (0, 0, -1);
                    break;
                case NORTH:
                    loc.add (-1, 0, 0);
                default:
                    break;
            }

        }
        return loc.getBlock ().getRelative (face.getOppositeFace ()).getLocation ();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#replace(boolean)
     */
    @Override
    public boolean replace (boolean shouldDrop) {

        Painting p = getWorld ().spawn (location.getBlock ().getRelative (hanging.getAttachedFace ()).getLocation (), Painting.class);
        p.teleport (location);
        p.setFacingDirection (hanging.getFacing (), true);
        p.setArt (((Painting) hanging).getArt ());
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#drop()
     */
    @Override
    public void drop () {
        getWorld ().dropItemNaturally (getLocation (), new ItemStack (321, 1));
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getTypeId()
     */
    @Override
    public int getTypeId () {
        return 321;
    }

}
