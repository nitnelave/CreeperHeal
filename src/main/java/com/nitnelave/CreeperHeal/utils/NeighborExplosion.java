package com.nitnelave.CreeperHeal.utils;

import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.World;

import com.nitnelave.CreeperHeal.block.CreeperExplosion;

public class NeighborExplosion extends NeighborFinder<CreeperExplosion>{

	@Override
	protected boolean hasNeighbor(Location loc, LinkedList<CreeperExplosion> list) {
		if(list == null)
			return false;
		World w = loc.getWorld();
		for(CreeperExplosion cEx : list) {
			Location l = cEx.getLocation();
			if(l.getWorld() == w && loc.distance(cEx.getLocation()) < cEx.getRadius())
				return true;
		}
		return false;
	}
	
	public void clean () {
		Iterator<LinkedList<CreeperExplosion>> iter = map.values().iterator();
		while (iter.hasNext())
		{
			LinkedList<CreeperExplosion> list = iter.next();
			Iterator<CreeperExplosion> it = list.iterator ();
			while (it.hasNext())
			{
				CreeperExplosion e = it.next();
				if(e.getBlockList().isEmpty())
					it.remove();
			}
			if(list.isEmpty())
				iter.remove();
		}
	}

}
