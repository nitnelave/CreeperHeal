package com.nitnelave.CreeperHeal.block;


import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

public class CreeperHead extends CreeperBlock {

	protected CreeperHead(BlockState blockState) {
		super(blockState);
	}
	
	@Override
	public void update(boolean force) {
		super.update(force);
		Skull skull = (Skull) blockState;
		Skull newSkull = ((Skull) blockState.getBlock().getState());
		newSkull.setRotation(skull.getRotation());
		newSkull.setSkullType(skull.getSkullType());
		if(skull.hasOwner())
			newSkull.setOwner(skull.getOwner());
		newSkull.update(force);
	}

}
