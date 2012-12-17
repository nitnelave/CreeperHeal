package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;
import org.shininet.bukkit.playerheads.Skull;

import com.nitnelave.CreeperHeal.utils.CreeperLog;

/**
 * @author meiskam
 */

public class CreeperSkull extends CreeperBlock {

	private Skull skull = null;

	protected CreeperSkull(BlockState blockState) {
		super(blockState);
		try {
			skull = new Skull(getLocation());
		} catch (NoSuchMethodError e) {
			CreeperLog.warning("Update PlayerHeads for compatibility with CreeperHeal.");
		}
	}

	@Override
	public void update(boolean force) {
		super.update(force);
		try {
			skull.place(getLocation());
		} catch (NoSuchMethodError e) {
			CreeperLog.warning("Update PlayerHeads for compatibility with CreeperHeal.");
		}
		catch (NullPointerException e) {
			CreeperLog.warning("Update PlayerHeads for compatibility with CreeperHeal.");
		}
	}
}