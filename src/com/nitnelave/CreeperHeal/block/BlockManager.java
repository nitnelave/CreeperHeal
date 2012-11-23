package com.nitnelave.CreeperHeal.block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;
import com.nitnelave.CreeperHeal.utils.DelayReplacement;

public class BlockManager {
	/**
	 * Constants
	 */


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

	protected static Map<Location, String[]> signText = Collections.synchronizedMap(new HashMap<Location, String[]>());                    //stores the signs text
	private static Map<Location, Byte> noteBlock = Collections.synchronizedMap(new HashMap<Location, Byte>());								//stores the note blocks' notes
	private static Map<Location, String> mobSpawner = Collections.synchronizedMap(new HashMap<Location, String>());						//stores the mob spawners' type
	private static Map<Location, BlockState> toReplace = Collections.synchronizedMap(new HashMap<Location,BlockState>());		//blocks to be replaced immediately after an explosion

	private static CreeperHeal plugin;

	public BlockManager(CreeperHeal plugin) {
		setBlockManagerPlugin(plugin);
		new CreeperDrop(plugin);
		new PaintingsManager();
		ChestManager.setToReplaceMap(toReplace);
		BurntBlockManager.setBurntBlockManagerPlugin(plugin);
		new ExplodedBlockManager(toReplace, plugin);
	}



	private void setBlockManagerPlugin(CreeperHeal plugin) {
		BlockManager.plugin = plugin;
	}



	protected static void dropBlock(BlockState blockState)
	{

		Location loc = blockState.getBlock().getLocation();
		World w = loc.getWorld();

		ItemStack drop = CreeperDrop.getDrop(blockState);
		if(drop != null)
			w.dropItemNaturally(loc, drop);

		if(blockState instanceof InventoryHolder)        //in case of a chest, drop the contents on the ground as well
		{
			ItemStack[] stacks = ChestManager.getContents(loc);
			if(stacks!=null)
			{
				for(ItemStack stack : stacks)
				{
					if(stack !=null)
						w.dropItemNaturally(loc, stack);
				}
				ChestManager.removeAt(loc);
			}

		}
		else if(blockState instanceof Sign)         //for the rest, just delete the reference
			signText.remove(loc);

		else if(blockState instanceof NoteBlock) 
			noteBlock.remove(loc);

		else if(blockState instanceof CreatureSpawner) 
			mobSpawner.remove(loc);
	}




	protected static void replaceProtected() {         //replace the blocks that should be immediately replaced after an explosion
		Iterator<BlockState> iter = toReplace.values().iterator();
		while(iter.hasNext())
			block_state_replace(iter.next());


		toReplace.clear();

	}




	protected static void replace_blocks(BlockState block) {        //if there's just one block, no need to go over all this
		block_state_replace(block);
	}


	public static void block_state_replace(BlockState blockState)
	{
		Block block = blockState.getBlock();
		int block_id = block.getTypeId();
		//int tmp_id = 0;

		if(!CreeperConfig.overwriteBlocks && !empty_blocks.contains(block_id)) {        //drop an item on the spot
			if(CreeperConfig.dropDestroyedBlocks)
				dropBlock(blockState);
			return;
		}
		else if(CreeperConfig.overwriteBlocks && !empty_blocks.contains(block_id) && CreeperConfig.dropDestroyedBlocks)
		{
			dropBlock(block.getState());
			block.setTypeIdAndData(0, (byte)0, false);
		}

		if(blocks_dependent.contains(blockState.getTypeId()) && empty_blocks.contains(blockState.getBlock().getRelative(CreeperUtils.getAttachingFace(blockState).getOppositeFace()).getTypeId()))
			delay_replacement(blockState);
		else
		{
			if (blockState.getType() == Material.WOODEN_DOOR || blockState.getType() == Material.IRON_DOOR_BLOCK)         //if it's a door, put the bottom then the top (which is unrecorded)
			{
				blockState.update(true);
				block.getRelative(BlockFace.UP).setTypeIdAndData(blockState.getTypeId(), (byte)(blockState.getRawData() + 8), false);
			}
			else if(blockState.getType() == Material.BED_BLOCK) 
			{        //put the head, then the feet
				byte data = blockState.getRawData();
				BlockFace face;
				if(data == 0)            //facing the right way
					face = BlockFace.WEST;
				else if(data == 1)
					face = BlockFace.NORTH;
				else if(data == 2)
					face = BlockFace.EAST;
				else
					face = BlockFace.SOUTH;
				blockState.update(true);
				block.getRelative(face).setTypeIdAndData(blockState.getTypeId(), (byte)(data + 8), false);    //feet
			}
			else if(blockState.getType() == Material.PISTON_MOVING_PIECE) {}
			else if(blockState.getType() == Material.RAILS || blockState.getType() == Material.POWERED_RAIL || blockState.getType() == Material.DETECTOR_RAIL)
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ReorientRails(blockState));//enforce the rails' direction, as it sometimes get messed up by the other rails around
			else if(blocks_physics.contains(blockState.getTypeId()))
			{
				if(CreeperConfig.preventBlockFall)
					plugin.getPreventBlockFall().put(blockState.getBlock().getLocation(), new Date());
				blockState.getBlock().setTypeIdAndData(blockState.getTypeId(), blockState.getRawData(), false);

			}
			else         //rest of it, just normal
				blockState.update(true);
		}

		CreeperUtils.checkForAscendingRails(blockState, plugin.getPreventUpdate());

		if(blockState instanceof InventoryHolder) //if it's a chest, put the inventory back
			ChestManager.restoreChest(block);
		else if(blockState instanceof Sign)                     //if it's a sign... no I'll let you guess
			restoreSign(block);
		else if(blockState instanceof NoteBlock) {
			if(block.getState() instanceof NoteBlock)
				((NoteBlock)block.getState()).setRawNote(noteBlock.get(block.getLocation()));
			noteBlock.remove(block.getLocation());
		}
		else if(blockState instanceof CreatureSpawner) {
			if(block.getState() instanceof CreatureSpawner)
				((CreatureSpawner)block.getState()).setCreatureTypeByName(mobSpawner.get(block.getLocation()));
			mobSpawner.remove(block.getLocation());
		}

	}


	private static void restoreSign(Block block) {
		if(block.getState() instanceof Sign)
		{
			Sign state = (Sign) block.getState();
			int k = 0;

			for(String line : signText.get(block.getLocation())) {
				state.setLine(k++, line);
			}
			state.update(true);
		}
		signText.remove(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()));
	}


	private static void delay_replacement(BlockState blockState)	//the block is dependent on a block that is just air. Schedule it for a later replacement
	{
		delay_replacement(blockState, 0);
	}

	public static void delay_replacement(BlockState blockState, int count)
	{
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new DelayReplacement(blockState, count), CreeperConfig.waitBeforeHeal);
	}


	protected static void replace_one_block(List<BlockState> list) {        //replace one block (block per block)
		replace_blocks(list.get(0));        //blocks are sorted, so get the first
		check_player_one_block(list.get(0).getBlock().getLocation());
		list.remove(0);
	}



	protected static void check_player_one_block(Location loc) {      //get the living entities around to save thoses who are suffocating
		if(CreeperConfig.teleportOnSuffocate) {
			Entity[] play_list = loc.getBlock().getChunk().getEntities();
			if(play_list.length!=0) {
				for(Entity en : play_list) {
					if(en instanceof LivingEntity) {
						if(loc.distance(en.getLocation()) < 2)
							CreeperUtils.check_player_suffocate((LivingEntity)en);
					}
				}
			}
		}
	}



	protected static void replace_blocks(List<BlockState> list) {    //replace all the blocks in the given list
		if(list == null)
			return;
		while(!list.isEmpty()){            //replace all non-physics non-dependent blocks
			Iterator<BlockState> iter = list.iterator();
			while (iter.hasNext()){
				BlockState block = iter.next();
				if(!blocks_physics.contains(block.getTypeId())){
					block_state_replace(block);
					iter.remove();
				}
			}
			iter = list.iterator();
			while (iter.hasNext()){        //then all physics
				BlockState block = iter.next();
				if(blocks_physics.contains(block.getTypeId())){
					block_state_replace(block);
					iter.remove();
				}
			}

		}
		if(CreeperConfig.teleportOnSuffocate) {            //checks for players suffocating anywhere
			Player[] player_list = plugin.getServer().getOnlinePlayers();
			for(Player player : player_list) {
				CreeperUtils.check_player_suffocate(player);
			}
		}

	}






	public void checkReplaceTime()
	{
		for(WorldConfig w : CreeperConfig.world_config.values()) {
			long time = plugin.getServer().getWorld(w.name).getTime();
			if(w.repairTime != -1 && ((Math.abs(w.repairTime - time) < 600) || (Math.abs(Math.abs(w.repairTime - time) - 24000)) < 600)){
				ExplodedBlockManager.forceReplace(0, w);        
				BurntBlockManager.forceReplaceBurnt(0, w);
				PaintingsManager.replace_paintings();
			}
		}
	}



	public static void putSignText(Location location, String[] lines) {
		signText.put(location, lines);
	}




	public static void putNoteBlock(Location location, byte rawNote) {
		noteBlock.put(location, rawNote);
	}



	public static void putMobSpawner(Location location, String creatureTypeName) {
		mobSpawner.put(location, creatureTypeName);
	}


}
