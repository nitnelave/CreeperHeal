package com.nitnelave.CreeperHeal.block;

import org.bukkit.World;
import org.bukkit.block.Block;

public interface Replaceable {
	
	public boolean replace(boolean shouldDrop);

	public Block getBlock();

	public World getWorld();

	public int getTypeId();


}
