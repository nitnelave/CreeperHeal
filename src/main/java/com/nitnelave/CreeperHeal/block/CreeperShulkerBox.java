package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WCfgVal;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Shulker box implementation of CreeperBlock.
 *
 * @author Jikoo
 */
public class CreeperShulkerBox extends CreeperBlock
{

    private final ItemStack[] contents;

    CreeperShulkerBox(ShulkerBox blockState)
    {
        super(blockState);
        this.contents = blockState.getInventory().getContents();
    }

    /*
     * @see com.nitnelave.CreeperHeal.block.Replaceable#drop(boolean)
     */
    @Override
    public boolean drop(boolean forced)
    {
        Location location = blockState.getLocation().add(0.5, 0.5, 0.5);
        if (forced || CreeperConfig.shouldDrop())
        {
            // Drop shulker with contents inside
            ItemStack itemStack = new ItemStack(blockState.getType());
            blockState.getWorld().dropItemNaturally(location, itemStack);
        }
        // Always drop container contents
        for (ItemStack itemStack : contents) {
            if (itemStack == null)
                continue;
            blockState.getWorld().dropItemNaturally(location, itemStack);
        }
        return false;
    }

    @Override
    public void update()
    {
        super.update();

        if (CreeperConfig.getWorld(getWorld()).getBool(WCfgVal.DROP_CHEST_CONTENTS))
            return;

        BlockState newState = blockState.getBlock().getState();
        if (newState instanceof InventoryHolder)
            ((InventoryHolder) newState).getInventory().setContents(contents);
    }

}
