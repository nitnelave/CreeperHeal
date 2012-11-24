package com.nitnelave.CreeperHeal.block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
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
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import com.nitnelave.CreeperHeal.utils.NeighborExplosion;

public class ExplodedBlockManager {

	private static List<CreeperExplosion> explosionList = Collections.synchronizedList(new LinkedList<CreeperExplosion>());
	private static NeighborExplosion explosionIndex;
	private static Map<Location, CreeperBlock> toReplace = BlockManager.getToReplace();		//blocks to be replaced immediately after an explosion

	
	static {
		if(!CreeperConfig.lightweightMode)
			explosionIndex = new NeighborExplosion();
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
				List<CreeperBlock> list = cEx.getBlockList();
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
		CreeperLog.logInfo("Explosion getting recorded...", 3);
		if(CreeperHeal.isInArena(location)) 
			return;
		//record the list of blocks of an explosion, from bottom to top
		Date now = new Date();
		List<CreeperBlock> listState = new LinkedList<CreeperBlock>();        //the list of blockstate we'll be keeping afterward
		WorldConfig world = CreeperConfig.loadWorld(location.getWorld());
		List<Block> to_add = new LinkedList<Block>();

		for(Block block : list)     //cycle through the blocks declared destroyed
			record(block, listState, world, to_add);
		
		if(CreeperConfig.explodeObsidian) 
			checkForObsidian(location, listState);


		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new Runnable(){public void run() {BlockManager.replaceProtected();}});       //immediately replace the blocks marked for immediate replacement


		Iterator<CreeperBlock> iter = listState.iterator();
		while(iter.hasNext())
		{
			CreeperBlock state = iter.next();
			if(toReplaceContains(state.getBlock().getLocation()))       //remove the dupes already stored in the immediate
				iter.remove();
		}

		CreeperBlock[] tmp_array = listState.toArray(new CreeperBlock[listState.size()]);        //sort through an array (bottom to top, dependent blocks in last), then store back in the list
		Arrays.sort(tmp_array, new CreeperComparator());
		listState.clear();

		for(CreeperBlock block : tmp_array) 
			listState.add(block);

		CreeperLog.logInfo("List sorted. Number of blocks : " + listState.size(), 3);
		CreeperExplosion cEx;
		if(timed)
			now = new Date(now.getTime() + 1200000);

		cEx = new CreeperExplosion(now, listState, location);        //store in the global hashmap, with the time it happened as a key

		explosionList.add(cEx);
		if(!CreeperConfig.lightweightMode)
		{
			Location l = cEx.getLocation();
			explosionIndex.addElement(cEx, l.getX(), l.getZ());
		}
		CreeperLog.logInfo("Added explosion to the list", 3);

		if(entity instanceof TNTPrimed) 
		{            //to replace the tnt that just exploded
			Block block = location.getBlock();
			if(/*world.replaceTNT || */CreeperTrapHandler.isTrap(block)) 
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new AddTrapRunnable(cEx, block,Material.TNT));
		}
		for(Block block : to_add)
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CreeperHeal.getInstance(), new AddTrapRunnable(cEx, block,Material.TNT));

	}
	
	


	private static void checkForObsidian(Location location, List<CreeperBlock> listState) {
		int radius = CreeperConfig.obsidianRadius;
		double chance = ((float)CreeperConfig.obsidianChance) / 100;
		World w = location.getWorld();
		
		Random r = new Random(System.currentTimeMillis());
		
		for(int i = location.getBlockX() - radius; i < location.getBlockX() + radius; i++) {
			for(int j = Math.max(0, location.getBlockY() - radius); j < Math.min(w.getMaxHeight(), location.getBlockY() + radius); j++)
			{
				for(int k = location.getBlockZ() - radius; k < location.getBlockZ() + radius; k++) {
					Location l = new Location(w, i, j, k);
					if(l.distance(location) > radius)
						continue;
					Block b = w.getBlockAt(l);
					if(b.getType() == Material.OBSIDIAN && r.nextDouble() < chance)
					{
						listState.add(CreeperBlock.newBlock(b.getState()));
						b.setTypeIdAndData(0, (byte)0, false);
					}
				}
			}
		}
	}


	private static void record(Block block, List<CreeperBlock> listState, WorldConfig world, List<Block> toAdd) {
		int type_id = block.getTypeId();
		if (type_id == 0)
			return;
		byte data = block.getData();

		if(CreeperConfig.preventChainReaction && block.getType().equals(Material.TNT))
		{
			toReplace.put(block.getLocation(), CreeperBlock.newBlock(block.getState()));
			block.setTypeIdAndData(0, (byte)0, false);
			return;
		}

		if(world.whiteBlockList ^ !world.blockList.contains(new BlockId(type_id, data)))
			//if the block is to be replaced
		{

			if(CreeperConfig.replaceProtectedChests && CreeperHeal.isProtected(block))
				toReplace.put(block.getLocation(), CreeperBlock.newBlock(block.getState()));    //replace immediately

			if(block.getState() instanceof InventoryHolder)         //save the inventory
			{
				storeChest(block, listState);
				return;
			}

			switch (block.getType()) 
			{       
			case IRON_DOOR_BLOCK :                //in case of a door or bed, only store one block to avoid dupes
			case WOODEN_DOOR :
				if(block.getData() < 8) 
				{
					listState.add(CreeperBlock.newBlock(new Door(block)));
					block.setTypeIdAndData(0, (byte)0, false);
					block.getRelative(BlockFace.UP).setTypeIdAndData(0, (byte)0, false);
				}
				break;
			case BED_BLOCK :
				if(data < 8) 
				{
					listState.add(CreeperBlock.newBlock(block.getState()));
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
				listState.add(CreeperBlock.newBlock(state));
				block.setTypeIdAndData(0, (byte) 0, false);
				break;
			case SMOOTH_BRICK :
			case BRICK_STAIRS :
				if(CreeperConfig.crackDestroyedBricks  && block.getData() == (byte)0)
					block.setData((byte) 2);        //crack the bricks if the setting is right
			default :                        //store the rest
				listState.add(CreeperBlock.newBlock(block.getState()));
				block.setTypeIdAndData(0, (byte)0, false);
				break;
			}

		}
		else if(CreeperConfig.dropDestroyedBlocks)      //the block should not be replaced, check if it drops
		{
			Random generator = new Random();
			if(generator.nextInt(100) < CreeperConfig.dropChance)        //percentage
				CreeperBlock.newBlock(block.getState()).dropBlock();
			block.setTypeIdAndData(0, (byte)0, false);

		}
		
	}
	
	protected static void storeChest(Block block, List<CreeperBlock> listState) {
		
		CreeperChest chest = new CreeperChest(block.getState());
		if(CreeperConfig.replaceProtectedChests && CreeperHeal.isProtected(block) || CreeperConfig.replaceAllChests)
			toReplace.put(chest.getLocation(), chest);
		else
			listState.add(chest);
			
	}

	public static void checkReplace(boolean blockPerBlock) {        //check to see if any block has to be replaced
		Date now = new Date();


		Iterator<CreeperExplosion> iter = explosionList.iterator();
		while(iter.hasNext()) {
			CreeperExplosion cEx = iter.next();
			Date time = cEx.getTime();
			List<CreeperBlock> blockList = cEx.getBlockList();
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


	public static boolean isNextToExplosion(Location location) {
		return explosionIndex.hasNeighbor(location);
	}



}
