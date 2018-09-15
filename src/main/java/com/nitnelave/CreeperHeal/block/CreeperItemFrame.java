package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

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
    CreeperItemFrame(ItemFrame frame)
    {
        super(frame);
        CreeperLog.debug("Registered item frame");
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
            ItemFrame f = getWorld().spawn(location, ItemFrame.class);
            f.teleport(location);
            f.setItem(((ItemFrame) hanging).getItem());
            f.setRotation(((ItemFrame) hanging).getRotation());
            f.setFacingDirection(hanging.getFacing(), true);
        } catch (IllegalArgumentException e) // Could not place the item frame
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
        if (forced || CreeperConfig.shouldDrop())
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
    public Material getType()
    {
        return Material.ITEM_FRAME;
    }
}
