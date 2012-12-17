package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import com.nitnelave.CreeperHeal.utils.NeighborFire;

public class BurntBlockManager {

	private static CreeperHeal plugin;
	private static List<CreeperBurntBlock> burntList = Collections.synchronizedList(new LinkedList<CreeperBurntBlock>());
	private static Map<Location, Date> recentlyBurnt;
	private static NeighborFire fireIndex;
	
	static {
		if(!CreeperConfig.lightweightMode)
		{
			fireIndex = new NeighborFire();
			recentlyBurnt = Collections.synchronizedMap(new HashMap<Location, Date>());
		}
	}

	public static void setBurntBlockManagerPlugin(CreeperHeal plugin) {
		BurntBlockManager.plugin = plugin;
	}


	public static void forceReplaceBurnt(long since, WorldConfig world_config) {     //replace all of the burnt blocks since "since"
		boolean force = false;
		if(since == 0)
			force = true;
		World world = plugin.getServer().getWorld(world_config.getName());

		synchronized (burntList){
			Date now = new Date();
			Iterator<CreeperBurntBlock> iter = burntList.iterator();
			while (iter.hasNext()) {
				CreeperBurntBlock cBlock = iter.next();
				Date time = cBlock.getTime();
				if(cBlock.getWorld() == world && (new Date(time.getTime() + since * 1000).after(now) || force)) {        //if enough time went by
					BlockManager.replace_blocks(cBlock);        //replace the non-dependent block
					if(!CreeperConfig.lightweightMode)
						recentlyBurnt.put(cBlock.getLocation(), new Date(System.currentTimeMillis() + 1000 * CreeperConfig.waitBeforeBurnAgain));
					iter.remove();
				}
			}
		}
	}

	

	public static void replaceBurnt() {        //checks for burnt blocks to replace, with an override for onDisable()

		Date now = new Date();
		synchronized (burntList) {
			Iterator<CreeperBurntBlock> iter = burntList.iterator();
			while (iter.hasNext()) {
				CreeperBurntBlock cBlock = iter.next();
				Date time = cBlock.getTime();
				Block block = cBlock.getBlock();
				if((new Date(time.getTime() + CreeperConfig.waitBeforeHealBurnt * 1000).before(now))) {        //if enough time went by
					if(CreeperBlock.isDependent(block.getTypeId()))
					{
						Block support = block.getRelative(cBlock.getAttachingFace().getOppositeFace());
						if(!CreeperBlock.isSolid(support.getTypeId()))
							cBlock.addTime(CreeperConfig.waitBeforeHealBurnt * 1000);
						else
						{
							BlockManager.replace_blocks(cBlock);
							if(!CreeperConfig.lightweightMode)
								recentlyBurnt.put(cBlock.getLocation(), new Date(System.currentTimeMillis() + 1000 * CreeperConfig.waitBeforeBurnAgain));
							iter.remove();
						}
					}
					else
					{
						BlockManager.replace_blocks(cBlock);
						if(!CreeperConfig.lightweightMode)
							recentlyBurnt.put(cBlock.getLocation(), new Date(System.currentTimeMillis() + 1000 * CreeperConfig.waitBeforeBurnAgain));
						iter.remove();
					}
				}
				else if(!CreeperBlock.isDependent(block.getTypeId()))
					break;
			}
		}
	}


	private static void recordAttachedBurntBlocks(Block block, Date now, BlockFace face){
		BlockState block_up = block.getRelative(face).getState();
		if(CreeperBlock.isDependent(block_up.getTypeId())) {        //the block above is a dependent block, store it, but one interval after
			if(CreeperBlock.getAttachingFace(block_up) == CreeperUtils.rotateCClockWise(face))
			{
				CreeperBurntBlock cBB = new CreeperBurntBlock(new Date(now.getTime() + 100), block_up);
				burntList.add(cBB);
				if(!CreeperConfig.lightweightMode)
					fireIndex.addElement(cBB, cBB.getLocation().getX(), cBB.getLocation().getZ());
				block_up.getBlock().setTypeIdAndData(0, (byte)0, false);

			}
		}
	}


	public static void recordBurn(Block block) {            //record a burnt block
		if(block.getType() != Material.TNT) {        //unless it's TNT triggered by fire
			Date now = new Date();
			CreeperBurntBlock cBB = new CreeperBurntBlock(now, block.getState());
			burntList.add(cBB);
			if(!(CreeperConfig.lightweightMode))
			{
				Location l = cBB.getLocation();
				fireIndex.addElement(cBB, l.getX(), l.getY());
				
				block.setTypeIdAndData(0, (byte)0, false);
			}
			BlockFace[] faces = {BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
			for(BlockFace face : faces)
			{
				recordAttachedBurntBlocks(block, now, face);
			}

		}
	}



	public static void cleanIndex() {
			fireIndex.clean();
	}


	public static boolean isNextToFire(Location location) {
		return fireIndex.hasNeighbor(location);
	}


	public static boolean isIndexEmpty() {
		return fireIndex.isEmpty();
	}

	public static boolean wasRecentlyBurnt(Location loc) {
		Date d = recentlyBurnt.get(loc);
		if(d == null)
			return false;
		return d.after(new Date());
	}


	public static void cleanUp() {
		fireIndex.clean();
		synchronized(recentlyBurnt) {
			Iterator<Location> iter = recentlyBurnt.keySet().iterator();
			Date now = new Date();
			while(iter.hasNext())
			{
				Location l = iter.next();
				Date d = recentlyBurnt.get(l);
				if(d.before(now))
					iter.remove();
			}
		}
	}

}
