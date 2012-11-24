package com.nitnelave.CreeperHeal.block;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import com.nitnelave.CreeperHeal.utils.DelayReplacement;

public class CreeperBlock {
	
	public final static Set<Integer> blocks_physics = CreeperUtils.createFinalHashSet(12,13,88);                        //sand gravel, soulsand fall
	public final static Set<Integer> blocks_dependent_down = CreeperUtils.createFinalHashSet(6,26,27,28,31,32,37,38,39,40,55,59,63,64,66,70,71,72,78,93,94,104,105,115);
	public final static Set<Integer> blocks_dependent = CreeperUtils.createFinalHashSet(6,26,27,28,31,32,37,38,39,40,50,55,59,63,64,65,66,68,69,70,71,72,75,76,77,78,93,94,96,104,105,106,115);
	public final static Set<Integer> blocks_non_solid = CreeperUtils.createFinalHashSet(0,6,8,9,26,27,28,30,31,37,38,39,40, 50,55,59,63,64,65,66,68,69,70,71,72,75,76,77,78,83,90,93,94,96);   //the player can breathe
	public final static Set<Integer> empty_blocks = CreeperUtils.createFinalHashSet(0,8,9,10,11, 51, 78);
	public static HashSet<Byte> transparent_blocks;			//blocks that you can aim through while creating a trap.

	/**
	 * Static constructor.
	 */
	static {
		Byte[] elements = {0, 6, 8, 9, 10, 11, 18, 20, 26, 27, 28, 30, 31, 32, 37, 38, 39, 40, 44, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 78, 83, 93, 94, 96, 101, 102, 104, 105, 106, 111, 115, 117};
		transparent_blocks = new HashSet<Byte>(Arrays.asList(elements));
	}

	/**
	 * HashMaps
	 */

	
	
	private BlockState blockState;
	
	public static CreeperBlock newBlock(BlockState blockState) {
		if(blockState instanceof InventoryHolder)
			return new CreeperChest(blockState);
		else if(blockState instanceof Sign) 
			return new CreeperSign((Sign) blockState);
		else if(blockState instanceof NoteBlock)
			return new CreeperNoteBlock((NoteBlock) blockState);
		else if(blockState instanceof CreatureSpawner)
			return new CreeperMonsterSpawner((CreatureSpawner) blockState);
		
		return new CreeperBlock(blockState);
	}
	
	protected CreeperBlock(BlockState blockState) {
		this.blockState = blockState;
	}
	
	public void update(boolean force) {
		blockState.update(force);
	}

	public Location getLocation() {
		return blockState.getLocation();
	}

	public World getWorld() {
		return blockState.getWorld();
	}
	
	public Block getBlock() {
		return blockState.getBlock();
	}
	
	public BlockState getState() {
		return blockState;
	}

	public int getTypeId() {
		return blockState.getTypeId();
	}

	public Material getType() {
		return blockState.getType();
	}

	public byte getRawData() {
		return blockState.getRawData();
	}
	

	public void dropBlock()
	{

		Location loc = blockState.getBlock().getLocation();
		World w = loc.getWorld();

		ItemStack drop = CreeperDrop.getDrop(blockState);
		if(drop != null)
			w.dropItemNaturally(loc, drop);

	}


	public void replace()
	{
		Block block = getBlock();
		int blockId = block.getTypeId();
		//int tmp_id = 0;

		if(!CreeperConfig.overwriteBlocks && !empty_blocks.contains(blockId)) {        //drop an item on the spot
			if(CreeperConfig.dropDestroyedBlocks)
				dropBlock();
			return;
		}
		else if(CreeperConfig.overwriteBlocks && !empty_blocks.contains(blockId) && CreeperConfig.dropDestroyedBlocks)
		{
			CreeperBlock.newBlock(block.getState()).dropBlock();
			block.setTypeIdAndData(0, (byte)0, false);
		}


		if(blocks_dependent.contains(getTypeId()) && empty_blocks.contains(getBlock().getRelative(CreeperUtils.getAttachingFace(blockState)).getTypeId()))
		{
			delay_replacement();
			return;
		}
		else
		{
			Material type = getType();
			if (blockState instanceof Door)
			{
				Block blockUp = block.getRelative(BlockFace.UP);
				if(!CreeperConfig.overwriteBlocks && !empty_blocks.contains(blockUp.getTypeId())) {        //drop an item on the spot
					if(CreeperConfig.dropDestroyedBlocks)
						dropBlock();
					return;
				}
				else if(CreeperConfig.overwriteBlocks && !empty_blocks.contains(blockUp.getTypeId()) && CreeperConfig.dropDestroyedBlocks)
				{
					CreeperBlock.newBlock(blockUp.getState()).dropBlock();
					blockUp.setTypeIdAndData(0, (byte)0, false);
				}
				update(true);
				byte b = (byte)(8 + (((Door)blockState).isHingeRight()?0:1));
				blockUp.setTypeIdAndData(getTypeId(), b, false);
			}
			else if(type == Material.BED_BLOCK) 
			{        //put the head, then the feet
				byte data = getRawData();
				BlockFace face;
				if(data == 0)            //facing the right way
					face = BlockFace.WEST;
				else if(data == 1)
					face = BlockFace.NORTH;
				else if(data == 2)
					face = BlockFace.EAST;
				else
					face = BlockFace.SOUTH;
				update(true);
				block.getRelative(face).setTypeIdAndData(getTypeId(), (byte)(data + 8), false);    //feet
			}
			else if(type == Material.PISTON_MOVING_PIECE) {}
			else if(type == Material.RAILS || type == Material.POWERED_RAIL || type == Material.DETECTOR_RAIL)
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new ReorientRails(this));//enforce the rails' direction, as it sometimes get messed up by the other rails around
			else if(blocks_physics.contains(getTypeId()))
			{
				if(CreeperConfig.preventBlockFall)
					CreeperHeal.getPreventBlockFall().put(getBlock().getLocation(), new Date());
				getBlock().setTypeIdAndData(getTypeId(), getRawData(), false);

			}
			else         //rest of it, just normal
				update(true);
		}

		CreeperUtils.checkForAscendingRails(this, CreeperHeal.getPreventUpdate());

	}
	

	private void delay_replacement()	//the block is dependent on a block that is just air. Schedule it for a later replacement
	{
		delay_replacement(0);
	}

	public void delay_replacement(int count)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new DelayReplacement(this, count), CreeperConfig.waitBeforeHeal);
	}




}
