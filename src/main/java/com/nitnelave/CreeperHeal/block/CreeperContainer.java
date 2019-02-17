package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WCfgVal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * InventoryHolder implementation of CreeperMultiblock.
 *
 * @author Jikoo
 */
class CreeperContainer extends CreeperMultiblock
{

    /*
     * Constructor.
     */
    CreeperContainer(Container blockState)
    {
        super(blockState);

        Inventory inv = blockState.getInventory();
        if (!(inv instanceof DoubleChestInventory))
            return;

        DoubleChestInventory doubleChest = ((DoubleChestInventory) inv);

        BlockState right = doubleChest.getRightSide().getLocation().getBlock().getState();

        // Left side is primary chest inventory
        this.blockState = doubleChest.getLeftSide().getLocation().getBlock().getState();
        this.dependents.add(right);

    }

    /*
     * @see com.nitnelave.CreeperHeal.block.Replaceable#remove()
     */
    @Override
    public void remove()
    {
        World world = blockState.getWorld();
        if (CreeperConfig.getWorld(world).getBool(WCfgVal.DROP_CHEST_CONTENTS))
        {
            Location location = blockState.getLocation().add(0.5, 0.5, 0.5);
            for (ItemStack itemStack : ((Container) blockState).getSnapshotInventory().getContents())
                if (itemStack != null)
                    world.dropItemNaturally(location, itemStack);
            ((Container) blockState).getSnapshotInventory().clear();
            blockState.update(true, false);
            for (BlockState dependent : dependents)
            {
                if (!(dependent instanceof Container))
                    continue;
                location = dependent.getLocation().add(0.5, 0.5, 0.5);
                for (ItemStack itemStack : ((Container) dependent).getSnapshotInventory().getContents())
                    if (itemStack != null)
                        world.dropItemNaturally(location, itemStack);
                ((Container) dependent).getSnapshotInventory().clear();
                dependent.update(true, false);
            }
        }

        ((Container) blockState).getInventory().clear();
        for (BlockState dependent : dependents)
            if (dependent instanceof Container)
                ((Container) dependent).getInventory().clear();

        super.remove();
    }

    /*
     * @see com.nitnelave.CreeperHeal.block.Replaceable#drop(boolean)
     */
    @Override
    public boolean drop(boolean forced)
    {
        if (forced || CreeperConfig.shouldDrop())
        {
            BlockState current = blockState.getBlock().getState();
            blockState.update(true, false);
            Collection<ItemStack> drop = blockState.getBlock().getDrops();
            current.update(true, false);
            Location location = blockState.getLocation().add(0.5, 0.5, 0.5);
            World world = blockState.getWorld();

            for (ItemStack s : drop)
                world.dropItemNaturally(location, s);

            for (BlockState dependent : dependents)
            {
                current = dependent.getBlock().getState();
                dependent.update(true, false);
                drop = dependent.getBlock().getDrops();
                current.update(true, false);
                location = dependent.getLocation().add(0.5, 0.5, 0.5);
                world = dependent.getWorld();
                for (ItemStack s : drop)
                    world.dropItemNaturally(location, s);
            }
            return true;
        }
        return false;
    }

    /*
     * @see com.nitnelave.CreeperHeal.block.Replaceable#update()
     */
    @Override
    public void update()
    {
        blockState.getChunk().load();
        if (CreeperConfig.getWorld(getWorld()).getBool(WCfgVal.DROP_CHEST_CONTENTS))
            ((Container) blockState).getSnapshotInventory().clear();
        blockState.update(true, false);

        dependents.forEach(state ->
        {
            if (CreeperConfig.getWorld(getWorld()).getBool(WCfgVal.DROP_CHEST_CONTENTS) && state instanceof Container)
                ((Container) state).getSnapshotInventory().clear();
            state.update(true, false);
        });

        getWorld().playSound(getLocation(), CreeperConfig.getSound(), CreeperConfig.getInt(CfgVal.SOUND_VOLUME) / 10F,
                ThreadLocalRandom.current().nextFloat() * 2);
    }

}
