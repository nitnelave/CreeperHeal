package com.nitnelave.CreeperHeal.block;

import java.util.Random;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Painting implementation of the CreeperHanging. Represents a painting.
 * 
 * @author nitnelave
 * 
 */
class CreeperPainting extends CreeperHanging
{

    /**
     * Constructor.
     * 
     * @param painting
     *            The painting represented.
     */
    protected CreeperPainting(Painting painting)
    {
        super(painting);
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
        Art art = ((Painting) hanging).getArt();

        if (art.getBlockHeight() + art.getBlockWidth() < 5)
        {
            int i = 0, j = 0, k = art.getBlockWidth() - 1;
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
            loc.add(i, 1 - art.getBlockHeight(), j);
        }
        else
        {

            if (art.getBlockHeight() != 3)
                loc.add(0, -1, 0);
            switch (face)
            {
            case EAST:
                loc.add(0, 0, -1);
                break;
            case NORTH:
                loc.add(-1, 0, 0);
            default:
                break;
            }

        }
        return loc.getBlock().getRelative(face.getOppositeFace()).getLocation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.Replaceable#replace(boolean)
     */
    @Override
    public boolean replace(boolean shouldDrop)
    {

        try
        {
            Painting p = getWorld().spawn(location.getBlock().getRelative(hanging.getAttachedFace()).getLocation(), Painting.class);
            p.teleport(location);
            p.setFacingDirection(hanging.getFacing(), true);
            p.setArt(((Painting) hanging).getArt());
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
            getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.PAINTING, 1));
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
        return Material.PAINTING.getId();
    }

}
