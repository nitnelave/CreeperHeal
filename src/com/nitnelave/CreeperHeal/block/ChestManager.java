package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
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

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;

public class ChestManager {
	private static Map<Location, ItemStack[]> chestContents = Collections.synchronizedMap(new HashMap<Location, ItemStack[]>());         //stores the chests contents
	private static Map<Location, BlockState> toReplace;
	
	public static void setToReplaceMap(Map<Location, BlockState> toReplace) {
		ChestManager.toReplace = toReplace;
	}

	protected static void storeChest(Block block, List<BlockState> listState) {
		Inventory inv = ((InventoryHolder) block.getState()).getInventory();
		if(inv.getType() == InventoryType.CHEST)
		{
			CreeperChest d = scanForNeighborChest(block.getState());
			if(d != null)
			{

				Inventory otherInv = d.right?((DoubleChestInventory)inv).getLeftSide():((DoubleChestInventory)inv).getRightSide();
				Inventory mainInv = d.right?((DoubleChestInventory)inv).getRightSide():((DoubleChestInventory)inv).getLeftSide();
				chestContents.put(d.chest.getLocation(), otherInv.getContents());
				chestContents.put(block.getLocation(), mainInv.getContents()); 

				if(CreeperConfig.replaceProtectedChests && CreeperHeal.isProtected(block))
					toReplace.put(d.chest.getLocation(), d.chest.getState());
				if(CreeperConfig.replaceAllChests)
				{
					toReplace.put(d.chest.getLocation(), d.chest.getState());    //replace immediately
					toReplace.put(block.getLocation(),block.getState());    //replace immediately
				}
				listState.add(d.chest.getState());
				inv.clear();
				d.chest.setTypeIdAndData(0, (byte)0, false);

			}
			else
			{
				chestContents.put(block.getLocation(), inv.getContents()); 
				inv.clear();
				if(CreeperConfig.replaceAllChests)
					toReplace.put(block.getLocation(),block.getState());    //replace immediately
			}
		}
		else
		{
			chestContents.put(block.getLocation(), inv.getContents()); 
			inv.clear();
			if(CreeperConfig.replaceAllChests)
				toReplace.put(block.getLocation(),block.getState());    //replace immediately
		}

			
	}



	protected static ItemStack[] getContents(Location loc) {
		return chestContents.get(loc);
	}



	protected static void removeAt(Location loc) {
		chestContents.remove(loc);
	}



	public static void restoreChest(Block block) {
		if(block.getState() instanceof Chest)
		{
			CreeperChest d = scanForNeighborChest(block.getState());
			Chest chest = (Chest) block.getState();
			if(d != null)
			{
				Inventory i = chest.getInventory();
				ItemStack[] both;
				ItemStack[] otherInv = getOtherChestInventory(chest, d.right);
				if(otherInv == null)
				{
					CreeperLog.warning("empty inventory");
				}
				else
				{
					ItemStack[] newInv = chestContents.get(block.getLocation());
					if(d.right)
						both = CreeperUtils.concat(otherInv , newInv);
					else
						both = CreeperUtils.concat(newInv, otherInv);
					i.setContents(both);
				}
			}
			else
				((InventoryHolder) block.getState()).getInventory().setContents(chestContents.get(block.getLocation()));
		}
		else
			((InventoryHolder) block.getState()).getInventory().setContents(chestContents.get(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ())));
		chestContents.remove(block.getLocation());

	}



	protected static CreeperChest scanForNeighborChest(BlockState block)
	{
		return scanForNeighborChest(block.getWorld(), block.getX(), block.getY(), block.getZ(), block.getRawData());
	}
	


	protected static CreeperChest scanForNeighborChest(World world, int x, int y, int z, short d) //given a chest, scan for double, return the Chest
	{
		Block neighbor;
		if(d <= 3)
		{
			neighbor = world.getBlockAt(x - 1, y, z);
			if (neighbor.getType().equals(Material.CHEST)) {
				return new CreeperChest(neighbor, d == 3);
			}
			neighbor = world.getBlockAt(x + 1, y, z);
			if (neighbor.getType().equals(Material.CHEST)) {
				return new CreeperChest(neighbor, d == 2);
			}
		}
		else
		{
			neighbor = world.getBlockAt(x, y, z - 1);
			if (neighbor.getType().equals(Material.CHEST)) {
				return new CreeperChest(neighbor, d == 4 );
			}
			neighbor = world.getBlockAt(x, y, z + 1);
			if (neighbor.getType().equals(Material.CHEST)) {
				return new CreeperChest(neighbor, d == 5);
			}
		}
		return null;
	}
	



	private static ItemStack[] getOtherChestInventory(BlockState state, boolean right)
	{
		int i = 0, j = 0;
		switch (state.getRawData())
		{
		case 2:
			i = right?1:-1;
			break;
		case 3:
			i = right?-1:1;
			break;
		case 4:
			j = right?-1:1;
			break;
		default:
			j = right?1:-1;
		}

		BlockState chest = state.getBlock().getRelative(i, 0, j).getState();
		if(chest instanceof Chest)
			return (state.getRawData() == 2 || state.getRawData() == 5?right:!right)?((DoubleChestInventory) ((Chest) chest).getInventory()).getRightSide().getContents():((DoubleChestInventory) ((InventoryHolder) chest).getInventory()).getLeftSide().getContents();
			CreeperLog.warning("[CreeperHeal] Debug : chest inventory error? " + state.getRawData() + " ; " + (state.getX() + i) + " ; " + (state.getZ() + j) + "; orientation : " + state.getRawData() + "right : " + right);
			return null;
	}

}
