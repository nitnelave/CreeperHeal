package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
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

        Block block = location.getBlock ().getRelative (hanging.getAttachedFace ());
        CraftWorld w = (CraftWorld) block.getWorld ();

        int dir = getIntDirection ();
        Painting p = (Painting) hanging;
        net.minecraft.server.v1_4_R1.EntityPainting paint = new net.minecraft.server.v1_4_R1.EntityPainting (w.getHandle (), block.getX (), block.getY (),
                block.getZ (), dir);
        net.minecraft.server.v1_4_R1.EnumArt[] array = net.minecraft.server.v1_4_R1.EnumArt.values ();
        paint.art = array[p.getArt ().getId ()];
        paint.setDirection (dir);
        if (!paint.survives ())
        {
            paint = null;
            return postpone ();
        }
        w.getHandle ().addEntity (paint);
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

}
