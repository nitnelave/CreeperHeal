package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_5.block.CraftChest;

public class NeighborChest extends CraftChest {
	
	private boolean right;

	public NeighborChest(Block block, boolean right) {
		super(block);
		this.right = right;
	}

	public boolean isRight() {
		return right;
	}
}
