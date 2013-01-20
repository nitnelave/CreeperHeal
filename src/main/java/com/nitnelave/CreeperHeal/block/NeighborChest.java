package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class NeighborChest {
	
	private BlockState chest;
	private boolean right;
	
	public NeighborChest(Block b, boolean right) {
		this (b.getState(), right);
	}

	public NeighborChest(BlockState chest, boolean right) {
		this.chest = chest;
		this.right = right;
	}

	public boolean isRight() {
		return right;
	}
	
	public BlockState getChest() {
		return chest;
	}
}
