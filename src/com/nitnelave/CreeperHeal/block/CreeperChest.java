package com.nitnelave.CreeperHeal.block;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.utils.CreeperUtils;

public class CreeperChest extends CreeperBlock {

	private Block chest;
	private boolean right;
	private NeighborChest neighbor = null;
	private ItemStack[] storedInventory, neighborInventory;


	public CreeperChest (BlockState blockState)
	{
		super(blockState);
		this.chest = blockState.getBlock();
		Inventory inv = ((InventoryHolder) blockState).getInventory();
		storedInventory = inv.getContents();
		if(inv.getType() == InventoryType.CHEST)
		{
			neighbor = scanForNeighborChest(chest.getState());
			if(neighbor != null)
			{
				Inventory otherInv = neighbor.isRight()?((DoubleChestInventory)inv).getLeftSide():((DoubleChestInventory)inv).getRightSide();
				Inventory mainInv = neighbor.isRight()?((DoubleChestInventory)inv).getRightSide():((DoubleChestInventory)inv).getLeftSide();

				storedInventory = mainInv.getContents();
				neighborInventory = otherInv.getContents();

				inv.clear();
				neighbor.getBlock().setTypeIdAndData(0, (byte)0, false);

			}
			else
			{
				inv.clear();
			}
		}
		else
		{
			inv.clear();
		}
	}


	public boolean isRight() {
		return right;
	}


	public Block getChest() {
		return chest;
	}


	public Chest getNeighbor() {
		return neighbor;
	}


	protected static NeighborChest scanForNeighborChest(BlockState block)
	{
		return scanForNeighborChest(block.getWorld(), block.getX(), block.getY(), block.getZ(), block.getRawData());
	}



	protected static NeighborChest scanForNeighborChest(World world, int x, int y, int z, short d) //given a chest, scan for double, return the Chest
	{
		Block neighbor;
		if(d <= 3)
		{
			neighbor = world.getBlockAt(x - 1, y, z);
			if (neighbor.getType().equals(Material.CHEST)) {
				return new NeighborChest(neighbor, d == 3);
			}
			neighbor = world.getBlockAt(x + 1, y, z);
			if (neighbor.getType().equals(Material.CHEST)) {
				return new NeighborChest(neighbor, d == 2);
			}
		}
		else
		{
			neighbor = world.getBlockAt(x, y, z - 1);
			if (neighbor.getType().equals(Material.CHEST)) {
				return new NeighborChest(neighbor, d == 4);
			}
			neighbor = world.getBlockAt(x, y, z + 1);
			if (neighbor.getType().equals(Material.CHEST)) {
				return new NeighborChest(neighbor, d == 5);
			}
		}
		return null;
	}



	public void restore() {
		super.update(true);
		if(hasNeighbor())
		{
			neighbor.update(true);
			Inventory i = ((InventoryHolder)chest.getState()).getInventory();
			ItemStack[] both;
			ItemStack[] otherInv = neighborInventory;
			ItemStack[] newInv = storedInventory;
			if(neighbor.isRight())
				both = CreeperUtils.concat(otherInv , newInv);
			else
				both = CreeperUtils.concat(newInv, otherInv);
			i.setContents(both);

		}
		else
			((InventoryHolder) chest.getState()).getInventory().setContents(storedInventory);


	}





	public boolean hasNeighbor() {
		return neighbor != null;
	}


	public ItemStack[] getTotalInventory() {
		if(!hasNeighbor())
			return storedInventory;
		else
		{
			ItemStack[] otherInv = neighborInventory;
			ItemStack[] newInv = storedInventory;
			if(neighbor.isRight())
				return CreeperUtils.concat(otherInv , newInv);
			else
				return CreeperUtils.concat(newInv, otherInv);
		}
	}


	@Override
	public void update(boolean force) {
		restore();
	}

	@Override
	public void dropBlock() {
		super.dropBlock();
		ItemStack[] stacks = getTotalInventory();
		if(stacks!=null)
		{
			for(ItemStack stack : stacks)
			{
				if(stack !=null)
					getWorld().dropItemNaturally(getLocation(), stack);
			}
		}



	}

}
