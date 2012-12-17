package com.nitnelave.CreeperHeal.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.World;

import com.nitnelave.CreeperHeal.block.CreeperBurntBlock;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class NeighborFire extends NeighborFinder<CreeperBurntBlock>{

	@Override
	protected boolean hasNeighbor(Location loc, LinkedList<CreeperBurntBlock> list) {
		World w = loc.getWorld();
		if(list == null)
			return false;
		for(CreeperBurntBlock cB : list) {
			if(loc.getWorld() == w && loc.distance(cB.getLocation()) < 10)
				return true;
		}
		return false;
	}

	public void clean() {

		Iterator<LinkedList<CreeperBurntBlock>> iter = map.values().iterator();
		Date delay = new Date(new Date().getTime() - 1000 * CreeperConfig.waitBeforeHealBurnt);
		while(iter.hasNext())
		{
			Iterator<CreeperBurntBlock> it = iter.next().iterator();
			while(it.hasNext()) {
				Date date = it.next().getTime();
				if(date.before(delay))
					it.remove();
				else
					break;
			}

		}
		
	}	

}
