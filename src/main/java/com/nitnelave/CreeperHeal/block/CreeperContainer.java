package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WCfgVal;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * InventoryHolder implementation of a CreeperMultiblock.
 *
 * @author Jikoo
 */
class CreeperContainer extends CreeperMultiblock
{

    private ItemStack[] storedInventory, neighborInventory = null;

    CreeperContainer(BlockState blockState)
    {
        super(blockState);

        Inventory inv = ((InventoryHolder) blockState).getInventory();
        storedInventory = inv.getContents();

        if ((inv instanceof DoubleChestInventory))
        {

            DoubleChestInventory doubleChest = ((DoubleChestInventory) inv);

            BlockState right = doubleChest.getRightSide().getLocation().getBlock().getState();

            // Left side is primary chest inventory
            this.blockState = doubleChest.getLeftSide().getLocation().getBlock().getState();
            this.dependents.add(right);

            this.storedInventory = doubleChest.getLeftSide().getContents();
            this.neighborInventory = doubleChest.getRightSide().getContents();
        }

    }

    @Override
    public void remove()
    {
        if (CreeperConfig.getWorld(getWorld()).getBool(WCfgVal.DROP_CHEST_CONTENTS))
        {
            World world = getWorld();
            Location location = getLocation();
            for (ItemStack itemStack : ((InventoryHolder) blockState).getInventory().getContents())
                if (itemStack != null)
                    world.dropItemNaturally(location, itemStack);
            ((InventoryHolder) blockState).getInventory().clear();
        }

        ((InventoryHolder) blockState).getInventory().clear();
        for (BlockState dependent : dependents)
            if (dependent instanceof InventoryHolder)
                ((InventoryHolder) dependent).getInventory().clear();

        super.remove();
    }

    @Override
    public boolean drop(boolean forced)
    {
        ItemStack[] inventory = getTotalInventory();
        if (inventory != null)
            for (ItemStack itemStack : inventory)
                if (itemStack != null)
                    getWorld().dropItemNaturally(getLocation(), itemStack);
        return super.drop(forced);
    }

    private ItemStack[] getTotalInventory()
    {
        if (neighborInventory == null)
            return storedInventory;

        return CreeperUtils.concat(storedInventory, neighborInventory);
    }

    @Override
    public void update()
    {
        super.update();

        if (CreeperConfig.getWorld(getWorld()).getBool(WCfgVal.DROP_CHEST_CONTENTS))
            return;

        Inventory inv = ((InventoryHolder) blockState).getInventory();
        if (!(inv instanceof DoubleChestInventory))
        {
            inv.setContents(this.storedInventory);
            return;
        }

        DoubleChestInventory doubleChest = ((DoubleChestInventory) inv);
        doubleChest.setContents(getTotalInventory());
    }

}
