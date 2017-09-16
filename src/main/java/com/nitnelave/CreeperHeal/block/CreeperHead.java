package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Skull implementation of CreeperBlock, to store and replace the orientation,
 * the owner, etc...
 * 
 * @author nitnelave
 * 
 */
class CreeperHead extends CreeperBlock
{

    /*
     * Constructor.
     */
    protected CreeperHead(BlockState blockState)
    {
        super(blockState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update(boolean)
     */
    @Override
    public void update()
    {
        super.update();
        Skull skull = (Skull) blockState;
        Skull newSkull = ((Skull) blockState.getBlock().getState());
        newSkull.setRotation(skull.getRotation());
        newSkull.setSkullType(skull.getSkullType());
        if (skull.hasOwner())
            newSkull.setOwningPlayer(skull.getOwningPlayer());
        newSkull.update(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#drop(boolean)
     */
    @Override
    public boolean drop(boolean forced)
    {
        if (forced || CreeperConfig.shouldDrop())
        {
            Location loc = blockState.getBlock().getLocation();
            World w = loc.getWorld();

            Skull skull = (Skull) blockState;
            ItemStack s = new ItemStack(Material.SKULL_ITEM);
            SkullMeta m = (SkullMeta) s.getItemMeta();
            if (skull.hasOwner())
                // Should be skull.getOwningPlayer().getName() instead of skull.getOwner();
                // but somehow always returns null, so we need to use the deprecated method
                m.setOwner(skull.getOwner());
            s.setItemMeta(m);
            s.setDurability((short) skull.getSkullType().ordinal());

            w.dropItemNaturally(loc, s);

            return true;
        }
        return false;
    }

}
