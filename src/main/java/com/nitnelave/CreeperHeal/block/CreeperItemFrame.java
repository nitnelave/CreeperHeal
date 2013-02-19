package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

/**
 * ItemFrame implementation of the CreeperHanging. Represents an ItemFrame.
 * 
 * @author nitnelave
 * 
 */
public class CreeperItemFrame extends CreeperHanging {

    /**
     * Constructor.
     * 
     * @param frame
     *            The item frame represented.
     * @param time
     *            The time of the explosion.
     * @param fire
     *            Whether the frame was destroyed by fire.
     */
    public CreeperItemFrame (ItemFrame frame, Date time, boolean fire) {
        super (frame, time, fire);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperHanging#computeLocation()
     */
    @Override
    protected Location computeLocation () {
        BlockFace face = hanging.getAttachedFace ();
        Location loc = hanging.getLocation ().getBlock ().getRelative (face).getLocation ();
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
        ItemFrame f = (ItemFrame) hanging;
        net.minecraft.server.v1_4_R1.EntityItemFrame frame = new net.minecraft.server.v1_4_R1.EntityItemFrame (w.getHandle (), block.getX (), block.getY (),
                block.getZ (), dir);
        net.minecraft.server.v1_4_R1.ItemStack stack = new net.minecraft.server.v1_4_R1.ItemStack (f.getItem ().getTypeId (), 1, 0);
        frame.a (stack);
        //TODO: set item rotation, direction
        if (!frame.survives ())
        {
            frame = null;
            return postpone ();
        }
        w.getHandle ().addEntity (frame);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#drop()
     */
    @Override
    public void drop () {
        ItemFrame f = (ItemFrame) hanging;
        ItemStack s = f.getItem ();
        if (s.getType () != Material.AIR)
            getWorld ().dropItemNaturally (getLocation (), s);
        getWorld ().dropItemNaturally (getLocation (), new ItemStack (389, 1));
    }
}
