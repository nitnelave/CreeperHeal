package com.nitnelave.CreeperHeal.block;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * ItemFrame implementation of the CreeperHanging. Represents an ItemFrame.
 * 
 * @author nitnelave
 * 
 */
class CreeperItemFrame extends CreeperHanging
{

    /**
     * Constructor.
     * 
     * @param frame
     *            The item frame represented.
     */
    protected CreeperItemFrame(ItemFrame frame)
    {
        super(frame);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperHanging#computeLocation()
     */
    @Override
    protected Location computeLocation()
    {
        BlockFace face = hanging.getAttachedFace();
        Location loc = hanging.getLocation().getBlock().getRelative(face).getLocation();
        return loc.getBlock().getRelative(face.getOppositeFace()).getLocation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.Replaceable#replace(boolean)
     */
    //TODO : frame position is not updated.
    @Override
    public boolean replace(boolean shouldDrop)
    {
        try
        {
            ItemFrame f = getWorld().spawn(location.getBlock().getRelative(hanging.getAttachedFace()).getLocation(), ItemFrame.class);
            f.teleport(location);
            f.setItem(((ItemFrame) hanging).getItem());
            f.setRotation(((ItemFrame) hanging).getRotation());
            f.setFacingDirection(hanging.getFacing(), true);
        } catch (IllegalArgumentException e)
        {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.Replaceable#drop()
     */
    @Override
    public boolean drop(boolean forced)
    {
        if (forced || new Random().nextInt(100) < CreeperConfig.getInt(CfgVal.DROP_CHANCE))
        {
            ItemFrame f = (ItemFrame) hanging;
            ItemStack s = f.getItem();
            if (s.getType() != Material.AIR)
                getWorld().dropItemNaturally(getLocation(), s);
            getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.ITEM_FRAME, 1));
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getTypeId()
     */
    @Override
    public int getTypeId()
    {
        return 389;
    }
}
