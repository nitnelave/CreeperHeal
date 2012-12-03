package com.nitnelave.CreeperHeal.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import com.nitnelave.CreeperHeal.block.CreeperBlock;

public class CreeperUtils
{
	

	public static boolean check_free_horizontal(World w, int x, int y, int z, LivingEntity en) {        //checks one up and down, to broaden the scope
		for(int k = -1; k<2; k++){
			if(check_free(w, x, y+k, z, en))
				return true;  //found a spot
		}
		return false;
	}

	public static boolean check_free(World w, int x, int y, int z, LivingEntity en) {
		Block block = w.getBlockAt(x, y, z);
		if(!CreeperBlock.isSolid(block.getTypeId()) && !CreeperBlock.isSolid(block.getRelative(0, 1, 0).getTypeId()) && CreeperBlock.isSolid(block.getRelative(0, -1, 0).getTypeId())) {
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
		if(CreeperBlock.isSolid(loc.getBlock().getTypeId()) || CreeperBlock.isSolid(loc.getBlock().getRelative(0, 1, 0).getTypeId())) {
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

	public static BlockFace rotateCClockWise(BlockFace face)
	{
		switch(face) {
		case EAST:
			return BlockFace.NORTH;
		case NORTH:
			return BlockFace.WEST;
		case WEST:
			return BlockFace.SOUTH;
		default:
			return BlockFace.EAST;
		}
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

	public static <T> Set<T> createFinalHashSet(T... elements) {
		Set<T> set = new HashSet<T>(elements.length);

		for(T element : elements)
			set.add(element);

		return Collections.unmodifiableSet(set);
	}

}
