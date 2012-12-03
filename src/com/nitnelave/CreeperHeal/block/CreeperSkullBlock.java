package com.nitnelave.CreeperHeal.block;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.shininet.bukkit.playerheads.Skull;

/**
* @author meiskam
*/

public class CreeperSkullBlock extends CreeperBlock {
	
	private Skull skull;
	private Location location;

	protected CreeperSkullBlock(BlockState blockState) {
		super(blockState);
		location = blockState.getLocation();
		try {
			skull = new Skull(location);
		} catch (NoSuchMethodError e) {
			//Server is not running a high enough version of PlayerHeads
		}
	}

	@Override
	public void update(boolean force) {
		super.update(force);
		try {
			skull.place(location);
		} catch (NoSuchMethodError e) {
			//Server is not running a high enough version of PlayerHeads
		}
	}
}
