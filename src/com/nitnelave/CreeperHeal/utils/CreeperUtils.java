package com.nitnelave.CreeperHeal.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.material.Attachable;
import org.bukkit.material.Rails;

import com.nitnelave.CreeperHeal.block.CreeperBlock;
import com.nitnelave.CreeperHeal.config.WorldConfig;

public class CreeperUtils
{
	public static void checkForAscendingRails(CreeperBlock blockState, Map<CreeperBlock, Date> preventUpdate)
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

	public static boolean check_free_horizontal(World w, int x, int y, int z, LivingEntity en) {        //checks one up and down, to broaden the scope
		for(int k = -1; k<2; k++){
			if(check_free(w, x, y+k, z, en))
				return true;  //found a spot
		}
		return false;
	}

	public static boolean check_free(World w, int x, int y, int z, LivingEntity en) {
		Block block = w.getBlockAt(x, y, z);
		if(CreeperBlock.blocks_non_solid.contains(block.getTypeId()) && CreeperBlock.blocks_non_solid.contains(block.getRelative(0, 1, 0).getTypeId()) && !CreeperBlock.blocks_non_solid.contains(block.getRelative(0, -1, 0).getTypeId())) {
			Location loc = new Location(w, x, y+0.5, z+0.5);
			loc.setYaw(en.getLocation().getYaw());
			loc.setPitch(en.getLocation().getPitch());
			en.teleport(loc);            //if there's ground under and space to breathe, put the player there
			return true;
		}
		return false;
	}

	public static void check_player_suffocate(LivingEntity en) {
		Location loc = en.getLocation();
		int x =loc.getBlockX();        //get the player's coordinates in ints, to have the block he's standing on
		int y =loc.getBlockY();
		int z =loc.getBlockZ();
		World w = en.getWorld();
		if(!CreeperBlock.blocks_non_solid.contains(loc.getBlock().getTypeId()) || !CreeperBlock.blocks_non_solid.contains(loc.getBlock().getRelative(0, 1, 0).getTypeId())) {
			for(int k =1; k + y < 127; k++) {        //all the way to the sky, checks if there's some room up or around

				if(check_free(w, x, y+k, z, en))
					break;

				if(check_free_horizontal(w, x+k, y, z, en))
					break;

				if(check_free_horizontal(w, x-k, y, z, en))
					break;

				if(check_free_horizontal(w, x, y, z+k, en))
					break;

				if(check_free_horizontal(w, x, y, z-k, en))
					break;

			}

		}

	}

	public static Location getAttachingBlock(Location loc, Hanging hanging, BlockFace face)
	{
		if(hanging instanceof Painting)
		{
			Art art = ((Painting) hanging).getArt();

			if(art.getBlockHeight() + art.getBlockWidth() < 5)
			{
				int i = 0, j = 0, k = art.getBlockWidth() - 1;
				switch(face){
				case EAST:
					break;
				case WEST:
					i = -k;
					break;
				case NORTH:
					j = -k;
					break;
				case SOUTH:
					break;
				default:
					break;
				}
				loc.add(i, 1-art.getBlockHeight(), j);
			}
			else 
			{ 
				if(art.getBlockHeight() == 4)
					loc.add(0, -1, 0);
				if(art.getBlockWidth() == 4)
				{
					switch(face){
					case EAST:
						break;
					case WEST:
						loc.add(-1, 0, 0);
						break;
					case NORTH:
						loc.add(0, 0, -1);
						break;
					case SOUTH:
						break;
					default:
						break;
					}
				}
			}
		}
		else
		{
			switch(face) {
			case EAST:
				loc.add(1, 0, 0);
				break;
			case NORTH:
				loc.add(0, 0, -1);
				break;
			case SOUTH:
				loc.add(0, 0, 1);
				break;
			case WEST:
				loc.add(-1, 0, 0);
				break;
			default:
				break;

			}
		}
		CreeperLog.logInfo("painting attached to : x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ(), 2);
		return loc;
	}




	public static boolean isAbove(Entity entity, int replaceLimit) {       //the entity that exploded was above the limit
		return entity.getLocation().getBlockY()>= replaceLimit;
	}

	public static String locToString(Location loc) {       //location to file-friendly string
		return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
	}

	public static String locToString(Block block) {        
		return block.getWorld().getName() + ";" + block.getX() + ";" + block.getY() + ";" + block.getZ();

	}


	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static boolean shouldReplace(Entity entity, WorldConfig world)
	{

		if(entity != null) {

			if( entity instanceof Creeper)         //if it's a creeper, and creeper explosions are recorded
			{
				if(world.replaceAbove)
				{
					if(isAbove(entity, world.replaceLimit))
						return world.creepers;
					return false;
				}
				return world.creepers;
			}
			else if(entity instanceof TNTPrimed)                 //tnt -- it checks if it's a trap.
			{
				if(world.replaceAbove){
					if(isAbove(entity, world.replaceLimit))
						return world.tnt;
					return false;
				}
				else
					return world.tnt;
			}
			else if(entity instanceof Fireball)         //fireballs (shot by ghasts)
			{
				if(world.replaceAbove){
					if(isAbove(entity, world.replaceLimit))
						return world.ghast;
					return false;
				}
				else
					return world.ghast;
			}
			else if(entity instanceof EnderDragon)
				return world.dragons;
			else if(entity instanceof Wither)
				return world.wither;
			else        //none of it, another custom entity
				return world.magical;

		}
		else
			return world.magical;
	}

	public static BlockFace rotateCClockWise(BlockFace face)
	{
		if(face == BlockFace.EAST)
			return BlockFace.NORTH;
		else if(face == BlockFace.NORTH)
			return BlockFace.WEST;
		else if(face == BlockFace.WEST)
			return BlockFace.SOUTH;
		else if(face == BlockFace.SOUTH)
			return BlockFace.EAST;
		else
			return face;
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

		/*if(BlockManager.blocks_dependent_down.contains(block.getTypeId()))
			return BlockFace.DOWN;
		else
			switch(block.getType()){
			case TORCH:
			case REDSTONE_TORCH_ON:
			case REDSTONE_TORCH_OFF:
				switch(block.getRawData()){
				case 1: return BlockFace.NORTH;
				case 2: return BlockFace.SOUTH;
				case 3: return BlockFace.EAST;
				case 4: return BlockFace.WEST;
				default: return BlockFace.DOWN;
				}
			case LADDER:
				switch(block.getRawData()) {
				case 2: return BlockFace.WEST;
				case 3: return BlockFace.EAST;
				case 4: return BlockFace.SOUTH;
				case 5: return BlockFace.NORTH;
				default: return null;
				}
			case WALL_SIGN:
				return ((Sign)(block.getData())).getAttachedFace();
			case RAILS:
			case DETECTOR_RAIL:
			case POWERED_RAIL:
				switch(block.getRawData()){
				case 5: return BlockFace.EAST;
				case 4: return BlockFace.WEST;
				case 3: return BlockFace.SOUTH;
				case 2: return BlockFace.NORTH;
				default: return BlockFace.DOWN;
				}
			case LEVER:
			case STONE_BUTTON:
				switch(block.getRawData()> 8?block.getRawData() - 8:block.getRawData()){
				case 1: return BlockFace.EAST;
				case 2: return BlockFace.WEST;
				case 3: return BlockFace.SOUTH;
				case 4: return BlockFace.NORTH;
				default: return BlockFace.DOWN;
				}
			case TRAP_DOOR:
				switch(block.getRawData()> 4?block.getRawData() - 4:block.getRawData()){
				case 4: return BlockFace.EAST;
				case 3: return BlockFace.WEST;
				case 2: return BlockFace.SOUTH;
				case 1: return BlockFace.NORTH;
				default: return BlockFace.DOWN;
				}
			default:
				return null;
			}*/
	}


	public static String getEntityNameFromId(byte data)
	{
		switch (data)
		{
		case 50:
			return "Creeper";
		case 51:
			return "Skeleton";
		case 52:
			return "Spider";
		case 53:
			return "Giant";
		case 54:
			return "Zombie";
		case 55:
			return "Slime";
		case 56:
			return "Ghast";
		case 57:
			return "Zombie Pigman";
		case 58:
			return "Enderman";
		case 59:
			return "Cave Spider";
		case 60:
			return "Silverfish";
		case 61:
			return "Blaze";
		case 62:
			return "Magma Cube";
		case 63:
			return "EnderDragon";
		case 64:
			return "Wither";
		case 65:
			return "Bat";
		case 66:
			return "Witch";
		case 90:
			return "Pig";
		case 91:
			return "Sheep";
		case 92:
			return "Cow";
		case 93:
			return "Chicken";
		case 94:
			return "Squid";
		case 95:
			return "Wolf";
		case 96:
			return "Mooshroom";
		case 97:
			return "Snow Golem";
		case 98:
			return "Ocelot";
		case 99:
			return "Iron Golem";
		case 120:
			return "Villager";
		default:
			return "Non-living Entity";
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Set<T> createFinalHashSet(T... elements) {
		Set<T> set = new HashSet<T>(elements.length);

		for(T element : elements)
			set.add(element);

		return Collections.unmodifiableSet(set);
	}

}
