package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Sign;


public class CreeperSign extends CreeperBlock {

	public CreeperSign(Sign sign) {
		super(sign);
	}
	
	@Override
	public void update(boolean force) {
		super.update(force);
		Sign state = (Sign)getBlock().getState();
		Sign sign = (Sign)getState();
		for(int k = 0; k < 4; k++) 
			state.setLine(k, sign.getLine(k));
		
		state.getData().setData(sign.getRawData());
		state.update(true);
	}


}
