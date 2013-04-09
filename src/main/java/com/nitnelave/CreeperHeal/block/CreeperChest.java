package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;

/**
 * InventoryHolder implementation of CreeperBlock.
 * 
 * @author nitnelave
 * 
 */
class CreeperChest extends CreeperBlock {

    private final Block chest;

    private NeighborChest neighbor = null;

    private ItemStack[] storedInventory, neighborInventory;

    /*
     * Constructor.
     */
    protected CreeperChest (BlockState blockState) {
        super (blockState);
        chest = getBlock ();
        Inventory inv = ((InventoryHolder) blockState).getInventory ();
        storedInventory = inv.getContents ();
        if (inv.getType () == InventoryType.CHEST)
        {
            neighbor = scanForNeighborChest (chest.getState ());
            if (neighbor != null)
            {
                Inventory otherInv = neighbor.isRight () ? ((DoubleChestInventory) inv).getLeftSide () : ((DoubleChestInventory) inv).getRightSide ();
                Inventory mainInv = neighbor.isRight () ? ((DoubleChestInventory) inv).getRightSide () : ((DoubleChestInventory) inv).getLeftSide ();

                storedInventory = mainInv.getContents ();
                neighborInventory = otherInv.getContents ();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#remove()
     */
    @Override
    public void remove () {
        ((InventoryHolder) blockState).getInventory ().clear ();
        getBlock ().setType (Material.AIR);
        if (neighbor != null)
            neighbor.getBlock ().setType (Material.AIR);
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#dropBlock()
     */
    @Override
    public boolean drop (boolean forced) {
        ItemStack[] stacks = getTotalInventory ();
        if (stacks != null)
            for (ItemStack stack : stacks)
                if (stack != null)
                    getWorld ().dropItemNaturally (getLocation (), stack);
        return super.drop (forced);
    }

    /*
     * Get the total inventory : it is either the normal one, or in case of a
     * double chest, the combined inventory of both chests.
     */
    private ItemStack[] getTotalInventory () {
        if (!hasNeighbor ())
            return storedInventory;
        else
        {
            ItemStack[] otherInv = neighborInventory;
            ItemStack[] newInv = storedInventory;
            if (neighbor.isRight ())
                return CreeperUtils.concat (otherInv, newInv);
            else
                return CreeperUtils.concat (newInv, otherInv);
        }
    }

    /*
     * Get whether the chest has a neighbor (double chest).
     */
    private boolean hasNeighbor () {
        return neighbor != null;
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update(boolean)
     */
    @Override
    public void update () {
        blockState.update (true);
        try
        {
            if (hasNeighbor ())
            {
                neighbor.getChest ().update (true);
                Inventory i = ((InventoryHolder) chest.getState ()).getInventory ();
                ItemStack[] both;
                ItemStack[] otherInv = neighborInventory;
                ItemStack[] newInv = storedInventory;
                if (neighbor.isRight ())
                    both = CreeperUtils.concat (otherInv, newInv);
                else
                    both = CreeperUtils.concat (newInv, otherInv);
                i.setContents (both);

            }
            else
                ((InventoryHolder) chest.getState ()).getInventory ().setContents (storedInventory);
        } catch (java.lang.ClassCastException e)
        {
            CreeperLog.warning ("Error detected, please report the whole message");
            CreeperLog.warning ("ClassCastException when replacing a chest : ");
            CreeperLog.warning (chest.getClass ().getCanonicalName ());
            CreeperLog.displayBlockLocation (chest, true);
            e.printStackTrace ();
        }

    }

    /*
     * Get the other chest of the double chest. null if it is a simple chest.
     */
    private static NeighborChest scanForNeighborChest (BlockState block) {
        return scanForNeighborChest (block.getWorld (), block.getX (), block.getY (), block.getZ (), block.getRawData (), block.getType ());
    }

    /*
     * Get the other chest of the double chest. null if it is a simple chest.
     */
    private static NeighborChest scanForNeighborChest (World world, int x, int y, int z, short d, Material material) {
        Block neighbor;
        if (d <= 3)
        {
            neighbor = world.getBlockAt (x - 1, y, z);
            if (neighbor.getType ().equals (material))
                return new NeighborChest (neighbor, d == 3);
            neighbor = world.getBlockAt (x + 1, y, z);
            if (neighbor.getType ().equals (material))
                return new NeighborChest (neighbor, d == 2);
        }
        else
        {
            neighbor = world.getBlockAt (x, y, z - 1);
            if (neighbor.getType ().equals (material))
                return new NeighborChest (neighbor, d == 4);
            neighbor = world.getBlockAt (x, y, z + 1);
            if (neighbor.getType ().equals (material))
                return new NeighborChest (neighbor, d == 5);
        }
        return null;
    }
}
