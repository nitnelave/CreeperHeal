package com.nitnelave.CreeperHeal.block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.InventoryHolder;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.CreeperTrapHandler;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.AddTrapRunnable;
import com.nitnelave.CreeperHeal.utils.CreeperComparator;

public class ExplodedBlockManager {

	private static Map<Location, BlockState> toReplace;
	private static List<CreeperExplosion> explosionList = Collections.synchronizedList(new LinkedList<CreeperExplosion>());
	private static CreeperHeal plugin;


	public ExplodedBlockManager(Map<Location, BlockState> toReplace, CreeperHeal plugin) {
		ExplodedBlockManager.plugin = plugin;
		ExplodedBlockManager.toReplace = toReplace;
	}


	public static void replaceNear(Player target)
	{
		int k = CreeperConfig.distanceNear;
		Location playerLoc = target.getLocation();

		World w = playerLoc.getWorld();
		Iterator<CreeperExplosion> iter = explosionList.iterator();
		while (iter.hasNext())
		{
			CreeperExplosion cEx = iter.next();
			Location loc = cEx.getLocation();
			if(loc.getWorld() == w)
			{
				if(loc.distance(playerLoc) < k)
				{
					BlockManager.replace_blocks(cEx.getBlockList());
					iter.remove();
				}
			}
		}

	}


	public static void forceReplace(long since, WorldConfig world)         //force replacement of all the explosions since x seconds
	{
		Date now = new Date();

		Iterator<CreeperExplosion> iterator = explosionList.iterator();
		while(iterator.hasNext()) 
		{
			CreeperExplosion cEx = iterator.next();
			Date time = cEx.getTime();
			if(new Date(time.getTime() + since).after(now) || since == 0)         //if the explosion happened since x seconds
			{
				List<BlockState> list = cEx.getBlockList();
				if(!list.isEmpty() && list.get(0).getWorld().getName().equals( world.getName())) 
				{
					BlockManager.replace_blocks(cEx.getBlockList());
					iterator.remove();
				}
			}
		}
		PaintingsManager.replace_paintings();
		if(since == 0) 
			BurntBlockManager.forceReplaceBurnt(0L, world);
	}



	public static void recordBlocks(EntityExplodeEvent event, WorldConfig world) 
	{
		event.setYield(0);
		recordBlocks(event.blockList(), event.getLocation(), event.getEntity(), world.isRepairTimed());
	}

	private static boolean toReplaceContains(Location location) {      //check if a block is already included in the list of blocks to be immediately replaced
		return toReplace.containsKey(location);
	}



	public static void recordBlocks(List<Block> list, Location location)
	{
		recordBlocks(list, location, null, CreeperConfig.loadWorld(location.getWorld()).isRepairTimed());
	}

	protected static void recordBlocks(List<Block> list, Location location, Entity entity, boolean timed)
	{
		if(plugin.isInArena(location)) 
			return;
		//record the list of blocks of an explosion, from bottom to top
		Date now = new Date();
		List<BlockState> listState = new LinkedList<BlockState>();        //the list of blockstate we'll be keeping afterward
		WorldConfig world = CreeperConfig.loadWorld(location.getWorld());
		List<Block> to_add = new LinkedList<Block>();

		for(Block block : list)     //cycle through the blocks declared destroyed
			record(block, listState, world, to_add);

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){public void run() {BlockManager.replaceProtected();}});       //immediately replace the blocks marked for immediate replacement


		Iterator<BlockState> iter = listState.iterator();
		while(iter.hasNext())
		{
			BlockState state = iter.next();
			if(toReplaceContains(state.getBlock().getLocation()))       //remove the dupes already stored in the immediate
				iter.remove();
		}

		BlockState[] tmp_array = listState.toArray(new BlockState[listState.size()]);        //sort through an array (bottom to top, dependent blocks in last), then store back in the list
		Arrays.sort(tmp_array, new CreeperComparator());
		listState.clear();

		for(BlockState block : tmp_array) 
			listState.add(block);

		CreeperExplosion cEx;
		if(timed)
			now = new Date(now.getTime() + 1200000);

		cEx = new CreeperExplosion(now, listState, location);        //store in the global hashmap, with the time it happened as a key

		explosionList.add(cEx);

		if(entity instanceof TNTPrimed) 
		{            //to replace the tnt that just exploded
			Block block = location.getBlock();
			if(/*world.replaceTNT || */CreeperTrapHandler.isTrap(block)) 
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new AddTrapRunnable(cEx, block,Material.TNT));
		}
		for(Block block : to_add)
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new AddTrapRunnable(cEx, block,Material.TNT));

	}
	
	


	private static void record(Block block, List<BlockState> listState, WorldConfig world, List<Block> toAdd) {
		int type_id = block.getTypeId();
		if (type_id == 0)
			return;
		byte data = block.getData();

		if(CreeperConfig.preventChainReaction && block.getType().equals(Material.TNT))
		{
			toReplace.put(block.getLocation(), block.getState());
			block.setTypeIdAndData(0, (byte)0, false);
			return;
		}

		if(world.whiteBlockList ^ !world.blockList.contains(new BlockId(type_id, data)))
			//if the block is to be replaced
		{

			if(CreeperConfig.replaceProtectedChests && CreeperHeal.isProtected(block))
				toReplace.put(block.getLocation(), block.getState());    //replace immediately

			if(block.getState() instanceof InventoryHolder)         //save the inventory
				ChestManager.storeChest(block, listState);
			else if(block.getState() instanceof Sign)                //save the text
				BlockManager.putSignText(block.getLocation(), ((Sign)block.getState()).getLines());
			else if(block.getState() instanceof NoteBlock) 
				BlockManager.putNoteBlock(block.getLocation(), ((NoteBlock)(block.getState())).getRawNote());
			else if(block.getState() instanceof CreatureSpawner) 
				BlockManager.putMobSpawner(block.getLocation(), ((CreatureSpawner)(block.getState())).getCreatureTypeName());

			switch (block.getType()) 
			{       
			case IRON_DOOR_BLOCK :                //in case of a door or bed, only store one block to avoid dupes
			case WOODEN_DOOR :
				if(block.getData() < 8) 
				{
					listState.add(block.getState());
					block.setTypeIdAndData(0, (byte)0, false);
					block.getRelative(BlockFace.UP).setTypeIdAndData(0, (byte)0, false);
				}
				break;
			case BED_BLOCK :
				if(data < 8) 
				{
					listState.add(block.getState());
					BlockFace face;
					if(data == 0)            //facing the right way
						face = BlockFace.WEST;
					else if(data == 1)
						face = BlockFace.NORTH;
					else if(data == 2)
						face = BlockFace.EAST;
					else
						face = BlockFace.SOUTH;
					block.setTypeIdAndData(0, (byte)0, false);
					block.getRelative(face).setTypeIdAndData(0, (byte)0, false);
				}
				break;
			case AIR :                        //don't store air
				break;
			case FIRE :                        //or fire
			case PISTON_EXTENSION :				//pistons are special, don't store this part
				block.setTypeIdAndData(0, (byte)0, false);
				break;
			case TNT :      //add the traps triggered to the list of blocks to be replaced
				if(CreeperTrapHandler.isTrap(block)/* || loadWorld(block.getWorld()).replaceTNT*/)
					toAdd.add(block);
				break;
			case STONE_PLATE :
			case WOOD_PLATE :
				BlockState state = block.getState();
				state.setRawData((byte) 0);
				listState.add(state);
				block.setTypeIdAndData(0, (byte) 0, false);
				break;
			case SMOOTH_BRICK :
			case BRICK_STAIRS :
				if(CreeperConfig.crackDestroyedBricks  && block.getData() == (byte)0)
					block.setData((byte) 2);        //crack the bricks if the setting is right
			default :                        //store the rest
				listState.add(block.getState());
				block.setTypeIdAndData(0, (byte)0, false);
				break;
			}

		}
		else if(CreeperConfig.dropDestroyedBlocks)      //the block should not be replaced, check if it drops
		{
			Random generator = new Random();
			if(generator.nextInt(100) < CreeperConfig.dropChance)        //percentage
				BlockManager.dropBlock(block.getState());
			block.setTypeIdAndData(0, (byte)0, false);

		}
		
	}
	

	public static void checkReplace(boolean blockPerBlock) {        //check to see if any block has to be replaced
		Date now = new Date();


		Iterator<CreeperExplosion> iter = explosionList.iterator();
		while(iter.hasNext()) {
			CreeperExplosion cEx = iter.next();
			Date time = cEx.getTime();
			List<BlockState> blockList = cEx.getBlockList();
			Date after = new Date(time.getTime() + CreeperConfig.waitBeforeHeal * 1000);
			if(after.before(now)) {        //if enough time went by
				if(!blockPerBlock){        //all blocks at once
					BlockManager.replace_blocks(blockList);        //replace the blocks
					iter.remove();                    //remove the explosion from the record
				}
				else {            //block per block
					if(!blockList.isEmpty())        //still some blocks left to be replaced
						BlockManager.replace_one_block(blockList);        //replace one
					if(blockList.isEmpty())         //if empty, remove from list
						iter.remove();
				}

			}
			else
				break;
		}   
		PaintingsManager.replacePaintings(now);


	}


	public static List<CreeperExplosion> getExplosionList() {
		return explosionList;
	}


}
