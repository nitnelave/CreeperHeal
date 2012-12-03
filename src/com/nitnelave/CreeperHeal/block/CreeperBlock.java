package com.nitnelave.CreeperHeal.block;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
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
import org.bukkit.material.Attachable;
import org.bukkit.material.Rails;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import com.nitnelave.CreeperHeal.utils.DelayReplacement;

public class CreeperBlock {
	
	private final static Set<Integer> PHYSICS_BLOCKS = CreeperUtils.createFinalHashSet(12,13,88, 145);                        //sand gravel, soulsand fall
	private final static Set<Integer> DEPENDENT_DOWN_BLOCKS = CreeperUtils.createFinalHashSet(6,26,27,28,31,32,37,38,39,40,55,59,63,64,66,70,71,72,78,93,94,104,105,115, 117, 140, 141, 142);
	private final static Set<Integer> DEPENDENT_BLOCKS = CreeperUtils.createFinalHashSet(6,26,27,28,31,32,37,38,39,40,50,55,59,63,64,65,66,68,69,70,71,72,75,76,77,78,93,94,96,104,105,106,115, 117, 127, 131, 140, 141, 142, 143);
	private final static Set<Integer> NOT_SOLID_BLOCKS = CreeperUtils.createFinalHashSet(0,6,8,9,26,27,28,30,31,37,38,39,40, 50,55,59,63,64,65,66,68,69,70,71,72,75,76,77,78,83,90,93,94,96);   //the player can breathe
	private final static Set<Integer> EMPTY_BLOCKS = CreeperUtils.createFinalHashSet(0,8,9,10,11, 51, 78);
	private final static Set<Integer> REDSTONE_BLOCKS = CreeperUtils.createFinalHashSet(55, 93, 94, 131);
	private static HashSet<Byte> TRANSPARENT_BLOCKS;			//blocks that you can aim through while creating a trap.

	/**
	 * Static constructor.
	 */
	static {
		Byte[] elements = {0, 6, 8, 9, 10, 11, 18, 20, 26, 27, 28, 30, 31, 32, 37, 38, 39, 40, 44, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 78, 83, 93, 94, 96, 101, 102, 104, 105, 106, 111, 115, 117};
		TRANSPARENT_BLOCKS = new HashSet<Byte>(Arrays.asList(elements));
	}

	/**
	 * HashMaps
	 */

	
	
	private BlockState blockState;
	
	public static CreeperBlock newBlock(BlockState blockState) {
		if(blockState instanceof InventoryHolder)
			return new CreeperChest(blockState);
		if(blockState instanceof Sign) 
			return new CreeperSign((Sign) blockState);
		if(blockState instanceof NoteBlock)
			return new CreeperNoteBlock((NoteBlock) blockState);
		if(blockState instanceof CreatureSpawner)
			return new CreeperMonsterSpawner((CreatureSpawner) blockState);
		if((CreeperConfig.playerHeads) && (blockState.getType() == Material.SKULL))
			return new CreeperSkullBlock(blockState);
		
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


	public void replace(boolean shouldDrop)
	{
		Block block = getBlock();
		int blockId = block.getTypeId();
		//int tmp_id = 0;

		if(!CreeperConfig.overwriteBlocks && !isEmpty(blockId)) {        //drop an item on the spot
			if(CreeperConfig.dropDestroyedBlocks)
				dropBlock();
			return;
		}
		else if(CreeperConfig.overwriteBlocks && !isEmpty(blockId) && CreeperConfig.dropDestroyedBlocks)
		{
			CreeperBlock.newBlock(block.getState()).dropBlock();
			block.setTypeIdAndData(0, (byte)0, false);
		}


		if(!shouldDrop && isDependent(getTypeId()) && isEmpty(getBlock().getRelative(getAttachingFace()).getTypeId()))
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
				if(!CreeperConfig.overwriteBlocks && !EMPTY_BLOCKS.contains(blockUp.getTypeId())) {        //drop an item on the spot
					if(CreeperConfig.dropDestroyedBlocks)
						dropBlock();
					return;
				}
				else if(CreeperConfig.overwriteBlocks && !EMPTY_BLOCKS.contains(blockUp.getTypeId()) && CreeperConfig.dropDestroyedBlocks)
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
				Bukkit.getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new ReorientRails(this));//enforce the rails' direction, as it sometimes get messed up by the other rails around
			else if(hasPhysics(getTypeId()))
			{
				if(CreeperConfig.preventBlockFall)
					CreeperHeal.getPreventBlockFall().put(getBlock().getLocation(), new Date());
				Block blockDown = block.getRelative(BlockFace.DOWN);
				if(!isSolid(blockDown.getTypeId()))
				{
					Bukkit.getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new FakeBlockTask(blockDown.getLocation(), blockDown.getTypeId(), blockDown.getData()), 1);
					Bukkit.getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new ReplaceBlockRunnable(newBlock(blockDown.getState())), 5);
					blockDown.setType(Material.GLASS);
				}
				else
					update(true);
					
				

			}
			else         //rest of it, just normal
				update(true);
		}

		checkForAscendingRails(CreeperHeal.getPreventUpdate());

	}
	

	private boolean isEmpty(int typeId) {
		return EMPTY_BLOCKS.contains(typeId);
	}

	private void delay_replacement()	//the block is dependent on a block that is just air. Schedule it for a later replacement
	{
		delay_replacement(0);
	}

	public void delay_replacement(int count)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new DelayReplacement(this, count), (long) Math.ceil((double)CreeperConfig.blockPerBlockInterval / 20));
	}

	public static boolean hasPhysics(int typeId) {
		return PHYSICS_BLOCKS.contains(typeId);
	}

	public static boolean isDependentDown(int typeId) {
		return DEPENDENT_DOWN_BLOCKS.contains(typeId);
	}

	public static boolean isSolid(int typeId) {
		return !NOT_SOLID_BLOCKS.contains(typeId);
	}

	public static boolean isDependent(int typeId) {
		return DEPENDENT_BLOCKS.contains(typeId);
	}

	public static HashSet<Byte> getTransparentBlocks() {
		return TRANSPARENT_BLOCKS;
	}

	public static boolean isRedstone(int typeId) {
		return REDSTONE_BLOCKS.contains(typeId);
	}

	public void checkForAscendingRails(Map<CreeperBlock, Date> preventUpdate)
	{
		BlockFace[] cardinals = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP};
		Block block = blockState.getBlock();
		for(BlockFace face : cardinals)
		{
			Block tmp_block = block.getRelative(face);
			if(tmp_block.getState() instanceof Rails)
			{
				byte data = tmp_block.getData();
				if(data>1 && data < 6)
				{
					BlockFace facing = null;
					if(data == 2)
						facing = BlockFace.EAST;
					else if(data == 3)
						facing = BlockFace.WEST;
					else if(data == 4)
						facing = BlockFace.NORTH;
					else if(data == 5)
						facing = BlockFace.SOUTH;
					if(tmp_block.getRelative(facing).getType() == Material.AIR)
						preventUpdate.put(CreeperBlock.newBlock(tmp_block.getState()), new Date());
				}
			}
		}
	}
	
	public BlockFace getAttachingFace() {
		return getAttachingFace(blockState);
	}
	
	public static BlockFace getAttachingFace(BlockState block)
	{
		if(block.getData() instanceof Attachable)
			return ((Attachable)block.getData()).getAttachedFace();
		switch(block.getType()) {
		case WOODEN_DOOR:
		case IRON_DOOR:
			return BlockFace.DOWN;
		case RAILS:
		case DETECTOR_RAIL:
		case POWERED_RAIL:
			switch(block.getRawData()){
			case 5: return BlockFace.WEST;
			case 4: return BlockFace.EAST;
			case 3: return BlockFace.NORTH;
			case 2: return BlockFace.SOUTH;
			default: return BlockFace.DOWN;
			}
		default:
			return BlockFace.DOWN;

		}
	}



}
