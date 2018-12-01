package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

/**
 * Shulker box implementation of CreeperBlock.
 *
 * @author Jikoo
 */
public class CreeperShulkerBox extends CreeperBlock
{

    CreeperShulkerBox(ShulkerBox blockState)
    {
        super(blockState);
    }

    /*
     * @see com.nitnelave.CreeperHeal.block.Replaceable#drop(boolean)
     */
    @Override
    public boolean drop(boolean forced)
    {
        if (forced || CreeperConfig.shouldDrop())
        {
            // Drop shulker with contents inside
            ItemStack itemStack = new ItemStack(blockState.getType());
            BlockStateMeta blockStateMeta = ((BlockStateMeta) Bukkit.getItemFactory().getItemMeta(Material.PURPLE_SHULKER_BOX));
            blockStateMeta.setBlockState(blockState);
            itemStack.setItemMeta(blockStateMeta);
            blockState.getWorld().dropItemNaturally(blockState.getLocation().add(0.5, 0.5, 0.5), itemStack);
            return true;
        }
        else
        {
            // Always drop container contents
            Location location = blockState.getLocation().add(0.5, 0.5, 0.5);
            for (ItemStack itemStack : ((ShulkerBox) blockState).getInventory().getContents()) {
                blockState.getWorld().dropItemNaturally(location, itemStack);
            }
        }
        return false;
    }

}
