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

    CreeperContainer(InventoryHolder blockState)
    {
        super((BlockState)blockState);

        Inventory inv = blockState.getInventory();
        storedInventory = inv.getContents();

        if ((inv instanceof DoubleChestInventory))
        {

            DoubleChestInventory doubleChest = ((DoubleChestInventory) inv);

            BlockState right = doubleChest.getRightSide().getLocation().getBlock().getState();

            // Left side is primary chest inventory
            this.blockState = doubleChest.getLeftSide().getLocation().getBlock().getState();
            addDependent(right);

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
        }

        ((InventoryHolder) blockState).getInventory().clear();
        for (BlockStateAndData dependent : getDependents())
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

        // Hacky 1.11 workaround - stored BlockState does not properly update primary inventory.
        BlockState newState = blockState.getBlock().getState();
        if (newState instanceof InventoryHolder)
        {
            Inventory newInv = ((InventoryHolder) newState).getInventory();
            if (newInv instanceof DoubleChestInventory)
                ((DoubleChestInventory) newInv).getLeftSide().setContents(storedInventory);
            else
                newInv.setContents(storedInventory);
        }

        Inventory inv = ((InventoryHolder) blockState).getInventory();
        if (!(inv instanceof DoubleChestInventory))
        {
            return;
        }

        // Setting secondary half is not problematic.
        DoubleChestInventory doubleChest = ((DoubleChestInventory) inv);
        doubleChest.getRightSide().setContents(neighborInventory);

    }

}
