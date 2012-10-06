package com.nitnelave.CreeperHeal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.EntityPainting;
import net.minecraft.server.EnumArt;
import net.minecraft.server.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yi.acru.bukkit.Lockette.Lockette;

import com.garbagemule.MobArena.MobArenaHandler;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.nitnelave.CreeperHeal.CreeperPlayer.WarningCause;
import com.nitnelave.CreeperTrap.CreeperTrap;



public class CreeperHeal extends JavaPlugin {
	/**
	 * Constants
	 */


	protected final static ArrayList<Integer> blocks_physics = new ArrayList<Integer>(Arrays.asList(12,13,88));                        //sand gravel, soulsand fall
	protected final static ArrayList<Integer> blocks_dependent_down = new ArrayList<Integer>(Arrays.asList(6,26,27,28,31,32,37,38,39,40,55,59,63,64,66,70,71,72,78,93,94,104,105,115));
	protected final static ArrayList<Integer> blocks_dependent = new ArrayList<Integer>(Arrays.asList(6,26,27,28,31,32,37,38,39,40,50,55,59,63,64,65,66,68,69,70,71,72,75,76,77,78,93,94,96,104,105,106,115));
	protected final static ArrayList<Integer> blocks_non_solid = new ArrayList<Integer>(Arrays.asList(0,6,8,9,26,27,28,30,31,37,38,39,40, 50,55,59,63,64,65,66,68,69,70,71,72,75,76,77,78,83,90,93,94,96));   //the player can breathe
	private final static ArrayList<Integer> empty_blocks = new ArrayList<Integer>(Arrays.asList(0,8,9,10,11, 51, 78));
	public static HashSet<Byte> transparent_blocks = null;			//blocks that you can aim through while creating a trap.

	/**
	 * Static constructor.
	 */
	static {
		Byte[] elements = {0, 6, 8, 9, 10, 11, 18, 20, 26, 27, 28, 30, 31, 32, 37, 38, 39, 40, 44, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 78, 83, 93, 94, 96, 101, 102, 104, 105, 106, 111, 115, 117};
		transparent_blocks = new HashSet<Byte>(Arrays.asList(elements));
	}


	/**
	 * Listeners
	 */

	protected CreeperListener listener = new CreeperListener(this);                        //listener for explosions
	private FancyListener fancyListener = new FancyListener(this);

	/**
	 * HashMaps
	 */

	private Map<Location, ItemStack[]> chestContents = Collections.synchronizedMap(new HashMap<Location, ItemStack[]>());         //stores the chests contents
	private Map<Location, String[]> signText = Collections.synchronizedMap(new HashMap<Location, String[]>());                    //stores the signs text
	private Map<Location, Byte> noteBlock = Collections.synchronizedMap(new HashMap<Location, Byte>());								//stores the note blocks' notes
	private Map<Location, String> mobSpawner = Collections.synchronizedMap(new HashMap<Location, String>());						//stores the mob spawners' type
	private List<CreeperPainting> paintings = Collections.synchronizedList(new ArrayList<CreeperPainting>());					//paintings to be replaced
	private Map<Location, BlockState> toReplace = Collections.synchronizedMap(new HashMap<Location,BlockState>());		//blocks to be replaced immediately after an explosion
	protected Map<BlockState, Date> preventUpdate = Collections.synchronizedMap(new HashMap<BlockState, Date>());
	protected Map<Location, Date> fireList = Collections.synchronizedMap(new HashMap<Location, Date>());
	protected Map<Location, Date> preventBlockFall = Collections.synchronizedMap(new HashMap<Location, Date>());
	protected LinkedList<CreeperBurntBlock> burntList = new LinkedList<CreeperBurntBlock>();
	protected LinkedList<CreeperExplosion> explosionList = new LinkedList<CreeperExplosion>();
	protected List<CreeperPlayer> warnList = new ArrayList<CreeperPlayer>(); 

	/**
	 * Handlers for misc. plugins
	 */

	private MobArenaHandler maHandler = null;		//handler to detect mob arenas
	private LWC lwc = null;			//handler for LWC protection




	protected final static Logger log = Logger.getLogger("Minecraft");            //to output messages to the console/log
	protected CreeperConfig config;
	protected CreeperCommandManager commandExecutor;
	private CreeperDrop creeperDrop;
	private CreeperHandler handler;
	private CreeperPermissionManager perms;
	protected CreeperTrap creeperTrap;
	private CreeperEconomy cEconomy;
	private CreeperLog warningLog;
	protected CreeperMessenger messenger;




	public void onEnable() {

		config = new CreeperConfig(this);

		messenger = new CreeperMessenger(getDataFolder(), this);

		commandExecutor = new CreeperCommandManager(this);
		perms = new CreeperPermissionManager(this);
		CommandMap commandMap = null;
		try{
			Field field = SimplePluginManager.class.getDeclaredField("commandMap");
			field.setAccessible(true);
			commandMap = (CommandMap)(field.get(getServer().getPluginManager()));
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}
		catch(IllegalAccessException e){
			e.printStackTrace();
		}

		String[] aliases = {"CreeperHeal",config.alias};
		CreeperCommand com = new CreeperCommand(aliases, "", "", commandExecutor);

		commandMap.register("_", com);

		creeperDrop = new CreeperDrop(this);

		handler = new CreeperHandler(this);

		cEconomy = new CreeperEconomy(this);

		File warningLogFile = new File(getDataFolder()+"/log.txt");
		if(!warningLogFile.exists())
			try
		{
				warningLogFile.createNewFile();
		}
		catch (IOException e)
		{
			log.log(Level.WARNING, e.getMessage());
		}

		warningLog = new CreeperLog(warningLogFile);



		/*
		 * Recurrent tasks
		 */

		 int tmp_period = 20;        //register the task to go every "period" second if all at once
		 if(config.blockPerBlock)                    //or every "block_interval" ticks if block_per_block
			 tmp_period = config.blockPerBlockInterval;
		 if( getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			 public void run() {
				 check_replace(config.blockPerBlock);        //check to replace explosions/blocks
			 }}, 200, tmp_period) == -1)
			 log.warning("[CreeperHeal] Impossible to schedule the re-filling task. Auto-refill will not work");

		 if( getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			 public void run() {
				 replace_burnt();
			 }}, 200, 20) == -1)
			 log.warning("[CreeperHeal] Impossible to schedule the replace-burnt task. Burnt blocks replacement will not work");

		 if( getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			 public void run() {
				 cleanMaps();
			 }}, 200, 200) == -1)
			 log.warning("[CreeperHeal] Impossible to schedule the map-cleaning task. Map cleaning will not work");

		 /*
		  * Connection with the other plugins
		  */

		 PluginManager pm = getServer().getPluginManager(); 



		 Plugin lwcPlugin = pm.getPlugin("LWC");
		 if(lwcPlugin != null) {
			 lwc = ((LWCPlugin) lwcPlugin).getLWC();
			 log_info("Successfully hooked in LWC",0);
		 }

		 Plugin lockettePlugin = pm.getPlugin("Lockette");
		 if(lockettePlugin!=null){
			 config.lockette  = true;
			 log_info("Successfully detected Lockette",0);
		 }


		 Plugin mobArena = pm.getPlugin("MobArena");
		 if(mobArena != null) {
			 maHandler = new MobArenaHandler();
			 log_info("Successfully hooked in MobArena",0);
		 }

		 Plugin cTrap = pm.getPlugin("CreeperTrap");
		 if(cTrap != null)
		 {
			 creeperTrap = (CreeperTrap) cTrap;
			 log_info("Successfully hooked in CreeperTrap", 0);
		 }


		 pm.registerEvents(listener, this);

		 if(!(config.lightweightMode))
			 pm.registerEvents(fancyListener, this);

		 populateWarnList();

	}

	private void populateWarnList()
	{
		warnList.clear();
		for(Player p : getServer().getOnlinePlayers())
		{
			if(checkPermissions(p, false, "grief.warn.*", "grief.warn.lava", "grief.warn.fire", "grief.warn.tnt", "grief.warn.blacklist"))
			{
				warnList.add(new CreeperPlayer(p, this));
			}
		}
	}

	protected void scheduleTimeRepairs()
	{
		if(getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				checkReplaceTime();
			}}, 200, 1200) == -1)

			log.warning("[CreeperHeal] Impossible to schedule the time-repair task. Time repairs will not work");

	}



	protected void cleanMaps()
	{
		Date now = new Date();
		Date delay = new Date(now.getTime() - 7500*config.waitBeforeHeal);
		Iterator<Date> iter;
		synchronized (preventUpdate)
		{
			iter = preventUpdate.values().iterator();
			while(iter.hasNext())
			{
				Date date = iter.next();
				if(date.before(delay))
					iter.remove();
				else
					break;
			}
		}
		synchronized (preventBlockFall)
		{
			iter = preventBlockFall.values().iterator();
			while(iter.hasNext())
			{
				Date date = iter.next();
				if(date.before(delay))
					iter.remove();
				else
					break;
			}
		}
		if(!(config.lightweightMode))
		{
			synchronized (fireList)
			{
				iter = fireList.values().iterator();
				delay = new Date(now.getTime() - 1000 * config.waitBeforeHealBurnt);
				while(iter.hasNext())
				{

					Date date = iter.next();
					if(date.before(delay))
						iter.remove();
					else
						break;

				}

			}
		}
	}


	public void onDisable() {
		for(WorldConfig w : config.world_config.values()) {
			force_replace(0, w);        //replace blocks still in memory, so they are not lost
			force_replace_burnt(0, w);    //same for burnt_blocks
		}
		log.info("[CreeperHeal] Disabled");
	}


	protected void recordBlocks(EntityExplodeEvent event, WorldConfig world) 
	{
		event.setYield(0);
		recordBlocks(event.blockList(), event.getLocation(), event.getEntity(), world.isRepairTimed());
	}


	public void recordBlocks(List<Block> list, Location location)
	{
		recordBlocks(list, location, null, loadWorld(location.getWorld()).isRepairTimed());
	}

	protected void recordBlocks(List<Block> list, Location location, Entity entity, boolean timed)
	{
		if(maHandler != null) 
		{
			if (maHandler.inRegion(location)) 
				return;		//Explosion inside a mob arena
		}

		//record the list of blocks of an explosion, from bottom to top
		Date now = new Date();

		List<BlockState> listState = new ArrayList<BlockState>();        //the list of blockstate we'll be keeping afterward
		WorldConfig world = loadWorld(location.getWorld());
		List<Block> to_add = new ArrayList<Block>();


		for(Block block : list)     //cycle through the blocks declared destroyed
		{
			int type_id = block.getTypeId();
			if (type_id == 0)
				continue;
			byte data = block.getData();
			if(/*(world.restrictBlocks.equalsIgnoreCase("whitelist") && world.blockList.contains(new BlockId(type_id, data))
					|| (world.restrictBlocks.equalsIgnoreCase("blacklist") && !world.blockList.contains(new BlockId(type_id, data))
							|| world.restrictBlocks.equalsIgnoreCase("false")))*/!world.blockList.contains(new BlockId(type_id, data)))       
				//if the block is to be replaced
			{

				if(config.replaceProtectedChests && isProtected(block))
					toReplace.put(block.getLocation(), block.getState());    //replace immediately


				if(block.getState() instanceof InventoryHolder)         //save the inventory
				{
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

							if(config.replaceProtectedChests && isProtected(block))
								toReplace.put(d.chest.getLocation(), d.chest.getState());
							if(config.replaceAllChests)
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
							if(config.replaceAllChests)
								toReplace.put(block.getLocation(),block.getState());    //replace immediately
						}
					}
					else
					{
						chestContents.put(block.getLocation(), inv.getContents()); 
						inv.clear();
						if(config.replaceAllChests)
							toReplace.put(block.getLocation(),block.getState());    //replace immediately
					}

				}
				else if(block.getState() instanceof Sign)                //save the text
					signText.put(block.getLocation(), ((Sign)block.getState()).getLines());

				else if(block.getState() instanceof NoteBlock) 
					noteBlock.put(block.getLocation(), ((NoteBlock)(block.getState())).getRawNote());

				else if(block.getState() instanceof CreatureSpawner) 
					mobSpawner.put(block.getLocation(), ((CreatureSpawner)(block.getState())).getCreatureTypeName());

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
						if(isTrap(block)/* || loadWorld(block.getWorld()).replaceTNT*/)
							to_add.add(block);
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
						if(config.crackDestroyedBricks  && block.getData() == (byte)0)
							block.setData((byte) 2);        //crack the bricks if the setting is right
					default :                        //store the rest
						listState.add(block.getState());
						block.setTypeIdAndData(0, (byte)0, false);
						break;
				}

			}
			else if(config.dropDestroyedBlocks)      //the block should not be replaced, check if it drops
			{
				Random generator = new Random();
				if(generator.nextInt(100) < config.dropChance)        //percentage
					dropBlock(block.getState());
				block.setTypeIdAndData(0, (byte)0, false);

			}
		}



		getServer().getScheduler().scheduleSyncDelayedTask(this,new Runnable(){public void run() {replaceProtected();}});       //immediately replace the blocks marked for immediate replacement




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
		{
			listState.add(block);
		}



		CreeperExplosion cEx;
		if(timed)
			now = new Date(now.getTime() + 1200000);

		cEx = new CreeperExplosion(now, listState, location);        //store in the global hashmap, with the time it happened as a key


		explosionList.add(cEx);


		if(entity instanceof TNTPrimed) 
		{            //to replace the tnt that just exploded
			Block block = location.getBlock();

			if(/*world.replaceTNT || */isTrap(block)) 
				getServer().getScheduler().scheduleSyncDelayedTask(this, new AddTrapRunnable(cEx, block,Material.TNT));
		}
		for(Block block : to_add)
		{
			getServer().getScheduler().scheduleSyncDelayedTask(this, new AddTrapRunnable(cEx, block,Material.TNT));
		}




	}


	private void check_replace(boolean block_per_block) {        //check to see if any block has to be replaced
		Date now = new Date();


		Iterator<CreeperExplosion> iter = explosionList.iterator();
		while(iter.hasNext()) {
			CreeperExplosion cEx = iter.next();
			Date time = cEx.getTime();
			List<BlockState> blockList = cEx.getBlockList();
			Date after = new Date(time.getTime() + config.waitBeforeHeal * 1000);
			if(after.before(now)) {        //if enough time went by
				if(!block_per_block){        //all blocks at once
					replace_blocks(blockList);        //replace the blocks
					iter.remove();                    //remove the explosion from the record
				}
				else {            //block per block
					if(!blockList.isEmpty())        //still some blocks left to be replaced
						replace_one_block(blockList);        //replace one
					if(blockList.isEmpty())         //if empty, remove from list
						iter.remove();
				}

			}
			else
				break;
		}   
		replacePaintings(now);


	}

	private void replace_one_block(List<BlockState> list) {        //replace one block (block per block)
		replace_blocks(list.get(0));        //blocks are sorted, so get the first
		if(!list.isEmpty())
		{
			check_player_one_block(list.get(0).getBlock().getLocation());
			list.remove(0);
		}


	}

	protected void check_player_one_block(Location loc) {      //get the living entities around to save thoses who are suffocating
		if(config.teleportOnSuffocate) {
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


	protected void force_replace(long since, WorldConfig world)         //force replacement of all the explosions since x seconds
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
					replace_blocks(cEx.getBlockList());
					iterator.remove();
				}
			}
		}
		replace_paintings();
		if(since == 0)
			force_replace_burnt(0L, world);
	}


	private void replace_blocks(List<BlockState> list) {    //replace all the blocks in the given list
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
		if(config.teleportOnSuffocate) {            //checks for players suffocating anywhere
			Player[] player_list = getServer().getOnlinePlayers();
			for(Player player : player_list) {
				CreeperUtils.check_player_suffocate(player);
			}
		}

	}

	private void replace_blocks(BlockState block) {        //if there's just one block, no need to go over all this
		block_state_replace(block);
	}


	protected void block_state_replace(BlockState blockState)
	{
		Block block = blockState.getBlock();
		int block_id = block.getTypeId();
		//int tmp_id = 0;

		if(!config.overwriteBlocks && !empty_blocks.contains(block_id)) {        //drop an item on the spot
			if(config.dropDestroyedBlocks)
				dropBlock(blockState);
			return;
		}
		else if(config.overwriteBlocks && !empty_blocks.contains(block_id) && config.dropDestroyedBlocks)
		{
			dropBlock(block.getState());
		}




		if(blocks_dependent.contains(blockState) && blockState.getBlock().getRelative(CreeperUtils.getAttachingFace(blockState).getOppositeFace()).getType() == Material.AIR)
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
				getServer().getScheduler().scheduleSyncDelayedTask(this, new ReorientRails(blockState));//enforce the rails' direction, as it sometimes get messed up by the other rails around
			else if(blocks_physics.contains(blockState.getTypeId()))
			{
				preventBlockFall.put(blockState.getBlock().getLocation(), new Date());
				Block tmp_block = block.getRelative(BlockFace.DOWN);
				if(empty_blocks.contains(tmp_block.getTypeId()))
				{
					BlockState tmpState = tmp_block.getState();
					tmp_block.setTypeId(4, false);
					blockState.update(true);
					getServer().getScheduler().scheduleSyncDelayedTask(this, new ReplaceBlockRunnable(tmpState), 2);
				}
				else
					blockState.update(true);

			}
			else         //rest of it, just normal
				blockState.update(true);
		}

		CreeperUtils.checkForAscendingRails(blockState, preventUpdate);

		if(blockState instanceof InventoryHolder) {            //if it's a chest, put the inventory back
			if(block.getState() instanceof Chest)
			{
				CreeperChest d = scanForNeighborChest(block.getState());

				if(d != null)
				{
					Inventory i = ((InventoryHolder) block.getState()).getInventory();
					ItemStack[] both;
					ItemStack[] otherInv = getOtherChestInventory(block.getState(), d.right);
					if(otherInv == null)
					{
						log.warning("empty inventory");
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
					chestContents.remove(block.getLocation());
				}
				else
				{
					((InventoryHolder) block.getState()).getInventory().setContents( chestContents.get(block.getLocation()));
					chestContents.remove(block.getLocation());
				}
			}
			else
			{
				((InventoryHolder) block.getState()).getInventory().setContents( chestContents.get(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ())));
				chestContents.remove(block.getLocation());
			}
		}
		else if(blockState instanceof Sign) {                    //if it's a sign... no I'll let you guess
			Sign state = (Sign) block.getState();
			int k = 0;

			for(String line : signText.get(block.getLocation())) {
				state.setLine(k++, line);
			}
			state.update(true);
			signText.remove(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()));

		}
		else if(blockState instanceof NoteBlock) {
			((NoteBlock)block.getState()).setRawNote( noteBlock.get(block.getLocation()));
			noteBlock.remove(block.getLocation());
		}
		else if(blockState instanceof CreatureSpawner) {
			((CreatureSpawner)block.getState()).setCreatureTypeByName( mobSpawner.get(block.getLocation()));
			mobSpawner.remove(block.getLocation());
		}

	}



	private void delay_replacement(BlockState blockState)	//the block is dependent on a block that is just air. Schedule it for a later replacement
	{
		delay_replacement(blockState, 0);
	}

	protected void delay_replacement(BlockState blockState, int count)
	{
		getServer().getScheduler().scheduleSyncDelayedTask(this, new DelayReplacement(this, blockState, count), config.waitBeforeHeal);
	}




	protected void record_burn(Block block) {            //record a burnt block
		if(block.getType() != Material.TNT) {        //unless it's TNT triggered by fire
			Date now = new Date();
			burntList.add(new CreeperBurntBlock(now, block.getState()));
			if(!(config.lightweightMode))
			{
				World w = block.getWorld();
				Location blockLoc = block.getLocation();
				synchronized(fireList)
				{
					boolean far = true;
					for(Location loc : fireList.keySet())
					{
						if(loc.getWorld() == w)
						{
							if(loc.distance(blockLoc) < 5)
							{
								far = false;
							}
						}
					}
					if(far)
						fireList.put(block.getLocation(), now);
				}
			}
			BlockFace[] faces = {BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};
			for(BlockFace face : faces)
			{
				recordAttachedBurntBlocks(block, now, face);
			}

		}
	}

	private void recordAttachedBurntBlocks(Block block, Date now, BlockFace face){
		BlockState block_up = block.getRelative(face).getState();
		if(blocks_dependent.contains(block_up.getTypeId())) {        //the block above is a dependent block, store it, but one interval after
			if(CreeperUtils.getAttachingFace(block_up) == CreeperUtils.rotateCClockWise(face))
			{
				burntList.add(new CreeperBurntBlock(new Date(now.getTime() + 100), block_up));
				if(block_up instanceof Sign) {                //as a side note, chests don't burn, but signs are dependent
					signText.put(new Location(block_up.getWorld(), block_up.getX(), block_up.getY(), block_up.getZ()), ((Sign)block_up).getLines());
				}
				block_up.getBlock().setTypeIdAndData(0, (byte)0, false);

			}
		}
	}




	private void replace_burnt() {        //checks for burnt blocks to replace, with an override for onDisable()

		Date now = new Date();
		synchronized (burntList) {
			Iterator<CreeperBurntBlock> iter = burntList.iterator();
			while (iter.hasNext()) {
				CreeperBurntBlock cBlock = iter.next();
				Date time = cBlock.getTime();
				BlockState block = cBlock.getBlockState();
				if((new Date(time.getTime() + config.waitBeforeHealBurnt * 1000).before(now))) {        //if enough time went by
					if(blocks_dependent.contains(block.getTypeId()))
					{
						Block support = block.getBlock().getRelative(CreeperUtils.getAttachingFace(block).getOppositeFace());
						if(support.getTypeId() == 0 || support.getTypeId() == 51)
							cBlock.addTime(config.waitBeforeHealBurnt * 1000);
						else
						{
							replace_blocks(block);
							iter.remove();
						}

					}
					else
					{
						replace_blocks(block);
						iter.remove();
					}
				}
				else if(!blocks_dependent.contains(block.getTypeId()))
					break;
			}
		}
	}

	protected void force_replace_burnt(long since, WorldConfig world_config) {     //replace all of the burnt blocks since "since"
		boolean force = false;
		if(since == 0)
			force = true;
		World world = getServer().getWorld(world_config.getName());

		synchronized (burntList){
			Date now = new Date();
			Iterator<CreeperBurntBlock> iter = burntList.iterator();
			while (iter.hasNext()) {
				CreeperBurntBlock cBlock = iter.next();
				Date time = cBlock.getTime();
				BlockState block = cBlock.getBlockState();
				if(block.getWorld() == world && (new Date(time.getTime() + since * 1000).after(now) || force)) {        //if enough time went by
					replace_blocks(block);        //replace the non-dependent block
					iter.remove();
				}
			}
		}
	}



	protected void log_info(String msg, int level) {        //logs a message, according to the log_level
		config.log_info(msg, level);
	}


	private void dropBlock(BlockState blockState)
	{

		Location loc = blockState.getBlock().getLocation();
		World w = loc.getWorld();

		ItemStack drop = creeperDrop.getDrop(blockState);
		if(drop != null)
			w.dropItemNaturally(loc, drop);

		if(blockState instanceof InventoryHolder)        //in case of a chest, drop the contents on the ground as well
		{
			ItemStack[] stacks = chestContents.get(loc);
			if(stacks!=null)
			{
				for(ItemStack stack : stacks)
				{
					if(stack !=null)
						w.dropItemNaturally(loc, stack);
				}
				chestContents.remove(loc);
			}

		}
		else if(blockState instanceof Sign)         //for the rest, just delete the reference
			signText.remove(loc);

		else if(blockState instanceof NoteBlock) 
			noteBlock.remove(loc);

		else if(blockState instanceof CreatureSpawner) 
			mobSpawner.remove(loc);
	}



	protected boolean isProtected(Block block){       //is the block protected?
		if(lwc!=null){                      //lwc gets the priority. BECAUSE!
			boolean protect = (lwc.findProtection(block)!=null);
			if(protect)
				log_info("protected block : " + block.getType(), 1);
			return protect;
		}
		else if(config.lockette){                  //and then lockette
			return Lockette.isProtected(block);
		}
		else return false;
	}


	protected void replaceProtected() {         //replace the blocks that should be immediately replaced after an explosion
		Iterator<BlockState> iter = toReplace.values().iterator();
		while(iter.hasNext())
			block_state_replace(iter.next());


		toReplace.clear();

	}


	private boolean toReplaceContains(Location location) {      //check if a block is already included in the list of blocks to be immediately replaced
		return toReplace.containsKey(location);
	}



	private void replacePaintings(Date time)
	{
		Iterator<CreeperPainting> iter = paintings.iterator();
		while(iter.hasNext())
		{

			CreeperPainting cp = iter.next();
			if(cp.isBurnt())
			{
				if(cp.getDate().getTime() - time.getTime() < 0 || fireList.size() == 0)
				{
					if(!replacePainting(cp.getPainting()))
					{
						if(fireList.size() > 0)
						{
							cp.getWorld().dropItemNaturally(cp.getLocation(), new ItemStack(321, 1));
							iter.remove();
						}
						else
							cp.postPone(config.waitBeforeHealBurnt);

					}
					else 
						iter.remove();
				}
			}
			else
			{
				if(Math.abs(cp.getDate().getTime() - time.getTime()) < 500 || explosionList.size() == 0);

				{
					if(!replacePainting(cp.getPainting()))
						cp.getWorld().dropItemNaturally(cp.getLocation(), new ItemStack(321, 1));
					iter.remove();
				}
			}
		}
	}

	
	private boolean replacePainting(Painting painting) {
		BlockFace face = painting.getAttachedFace().getOppositeFace();
		Location loc = painting.getLocation().getBlock().getRelative(face.getOppositeFace()).getLocation();
		CraftWorld w = (CraftWorld) loc.getWorld();

		loc = CreeperUtils.getAttachingBlock(loc, painting.getArt(), face);

		int dir;
		switch(face) {
			case EAST:
			default:
				dir = 0;
				break;
			case NORTH:
				dir = 1;
				break;
			case WEST:
				dir = 2;
				break;
			case SOUTH:
				dir = 3;;
				break;
		}

		EntityPainting paint = new EntityPainting(w.getHandle(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), dir);
		EnumArt[] array = EnumArt.values();
		paint.art = array[painting.getArt().getId()];
		paint.setDirection(paint.direction);
		if (!(paint).survives()) {
			paint = null;
			return false;
		}
		w.getHandle().addEntity(paint);
		return true;

	}



	protected void checkForPaintings(Painting p, boolean postpone, boolean burnt)
	{
		Date time = new Date();
		if(postpone)
			time = new Date(time.getTime() + 1200000);

		if(burnt)
			paintings.add(new CreeperPainting(p, new Date(time.getTime() + 1000 * config.waitBeforeHealBurnt + 10000), true));
		else
			paintings.add(new CreeperPainting(p, time, false));
		WorldServer w = ((CraftWorld)p.getWorld()).getHandle();
		w.getEntity(p.getEntityId()).dead = true;
	}


	protected void replace_paintings()
	{
		for(CreeperPainting p : paintings)
		{
			if(!replacePainting(p.getPainting()))
				p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(321, 1));
		}
		paintings.clear();
		
	}


	protected WorldConfig loadWorld(World w)
	{
		return config.loadWorld(w);
	}


	protected void replaceNear(Player target)
	{
		int k = config.distanceNear;
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
					replace_blocks(cEx.getBlockList());
					iter.remove();
				}
			}
		}

	}

	protected void checkReplaceTime()
	{
		for(WorldConfig w : config.world_config.values()) {
			long time = getServer().getWorld(w.name).getTime();
			if(w.repairTime != -1 && ((Math.abs(w.repairTime - time) < 600) || (Math.abs(Math.abs(w.repairTime - time) - 24000)) < 600)){
				force_replace(0, w);        
				force_replace_burnt(0, w);
				replace_paintings();
			}
		}
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


	private ItemStack[] getOtherChestInventory(BlockState state, boolean right)
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
			log.warning("[CreeperHeal] Debug : chest inventory error? " + state.getRawData() + " ; " + (state.getX() + i) + " ; " + (state.getZ() + j) + "; orientation : " + state.getRawData() + "right : " + right);
			return null;
	}


	protected static CreeperChest scanForNeighborChest(BlockState block)
	{
		return scanForNeighborChest(block.getWorld(), block.getX(), block.getY(), block.getZ(), block.getRawData());
	}



	public CreeperHandler getHandler()
	{
		return handler;
	}

	protected CreeperPermissionManager getPermissionManager()
	{
		return perms;
	}

	public void createTrap(Player p) throws VaultNotDetectedException, TransactionFailedException
	{
		if(checkPermissions(p, true,"trap.create", "trap.*")) {
			Block block = p.getTargetBlock(CreeperHeal.transparent_blocks, 10);
			if(block.getType() == Material.TNT) {
				String owner = creeperTrap.getTrapOwner(block);
				if(owner == null) {
					double cost = creeperTrap.getTrapFee(p);
					if(checkPermissions(p, true, "trap.bypass.fee"))
						cost = 0;
					if(playerHasEnough(p, cost))
					{
						boolean bypassMaxTraps = checkPermissions(p, true, "trap.bypass.maxTraps");
						if(creeperTrap.createTrap(block.getLocation(), p.getName(), bypassMaxTraps))
						{
							finePlayer(p, cost);
							if(cost == 0)
								p.sendMessage(messenger.processMessage("trap-success", p.getWorld().getName(), p.getName(), null, null, null, null));
						}
						else
							p.sendMessage(messenger.processMessage("too-many-traps", p.getWorld().getName(), p.getName(), null, null, null, Integer.toString(getMaxTraps(p))));
					}
					else
						p.sendMessage(messenger.processMessage("not-enough-money", p.getWorld().getName(), p.getName(), null, null, null, Double.toString(cost)));

				}
				else if(owner.equalsIgnoreCase(p.getName()))
					p.sendMessage(messenger.processMessage("trap-already-registered", p.getWorld().getName(), p.getName(), null, null, null, null));
				else    
					p.sendMessage(messenger.processMessage("cant-remove-trap", p.getWorld().getName(), p.getName(), owner, null, null, null));
			}
			else
				p.sendMessage(messenger.processMessage("trap-not-TNT", p.getWorld().getName(), p.getName(), null, null, null, null));
		}
		else
			p.sendMessage(messenger.processMessage("no-permission-trap", p.getWorld().getName(), p.getName(), null, null, null, null));
	}

	private void finePlayer(Player p, double cost) throws VaultNotDetectedException, TransactionFailedException
	{
		cEconomy.finePlayer(p, cost);
	}


	private boolean playerHasEnough(Player p, double cost) throws VaultNotDetectedException
	{
		return cEconomy.playerHasEnough(p, cost);
	}


	public boolean deleteTrap(Player p)
	{
		boolean delete_own, delete_all = checkPermissions(p, true, "trap.remove.all", "trap.*");
		delete_own = delete_all;
		if(!delete_own)
			delete_own = checkPermissions(p, true, "trap.remove.own");
		if(delete_own) {

			Block block = p.getTargetBlock(CreeperHeal.transparent_blocks, 10);

			if(block.getType() == Material.TNT) {

				String owner = creeperTrap.getTrapOwner(block.getLocation());

				if(owner == null) {

					p.sendMessage(messenger.processMessage("target-not-trap", p.getWorld().getName(), p.getName(), null, null, null, null));
					return false;
				}

				else if(owner.equalsIgnoreCase(p.getName())){

					creeperTrap.deleteTrap(block.getLocation());

					p.sendMessage(messenger.processMessage("trap-removed", p.getWorld().getName(), p.getName(), null, null, null, null));
					return true;

				}

				else {

					if(delete_all) {

						creeperTrap.deleteTrap(block.getLocation());

						p.sendMessage(messenger.processMessage("trap-removed", p.getWorld().getName(), p.getName(), null, null, null, null));
						return true;
					}

					else {
						p.sendMessage(messenger.processMessage("cant-remove-trap", p.getWorld().getName(), p.getName(), null, null, null, null));
						return false;
					}

				}

			}

			else {
				p.sendMessage(messenger.processMessage("trap-not-TNT", p.getWorld().getName(), p.getName(), null, null, null, null));
				return false;
			}

		}

		else {
			p.sendMessage(messenger.processMessage("trap-protected", p.getWorld().getName(), p.getName(), null, null, null, null));
			return false;

		}
	}


	public void deleteAllTraps(CommandSender sender, String target)
	{
		boolean delete_own, delete_all;
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			delete_all = checkPermissions(p, true, "trap.remove.all", "trap.*");
			delete_own = delete_all;
			if(!delete_own)
				delete_own = checkPermissions(p, true, "trap.remove.own");
		}
		else
			delete_own = delete_all = true;

		if(delete_own && sender instanceof Player && sender.getName().equals(target)) 
			creeperTrap.deleteAll(target);
		else if (delete_all) {
			if(getServer().getPlayer(target).hasPlayedBefore())
				creeperTrap.deleteAll(target);
			else
				sender.sendMessage("This player doesn't exist");

		}
		else {
			sender.sendMessage(messenger.processMessage("no-permission-command", null, sender.getName(), null, null, null, null));

		}	    
	}


	public int getMaxTraps(Player player)
	{
		return creeperTrap.getMaxTraps(player);
	}

	public boolean isTrap(Block block)
	{
		if(creeperTrap == null)
			return false;
		else
			return creeperTrap.isTrap(block);
	}

	protected boolean checkPermissions(Player player, boolean joker, String... nodes)
	{
		return perms.checkPermissions(player, joker, nodes);
	}


	protected void warn(WarningCause cause, Player offender,boolean blocked, String material)
	{
		String message = messenger.getMessage(cause, offender.getName(), offender.getWorld().getName(), blocked, material, false);
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		if(config.logWarnings)
			warningLog.record("[" + f.format(new Date()) + "] " + ChatColor.stripColor(message));
		message = ChatColor.RED + message;
		offender.sendMessage(messenger.getMessage(cause, offender.getName(), offender.getWorld().getName(), blocked, material, true));
		for(CreeperPlayer cp : warnList)
		{
			cp.warnPlayer(cp.getPlayer(), cause, message);	
		}

	}

	public void refund(Player p, double amount) throws VaultNotDetectedException, TransactionFailedException
	{
		cEconomy.refundPlayer(p, amount); 
	}




}