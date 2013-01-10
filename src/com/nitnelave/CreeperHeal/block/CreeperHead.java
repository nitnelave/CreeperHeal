package com.nitnelave.CreeperHeal.block;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

import com.nitnelave.CreeperHeal.utils.CreeperLog;

@SuppressWarnings("unused")
public class CreeperHead extends CreeperBlock {

	protected CreeperHead(BlockState blockState) {
		super(blockState);
		CreeperLog.debug("skull");
		World world = blockState.getWorld();
		Location loc = getLocation();
		Skull skull = (Skull) blockState;
		CreeperLog.debug(skull.getRotation().toString());
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
