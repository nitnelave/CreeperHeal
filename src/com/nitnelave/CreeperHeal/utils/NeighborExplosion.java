package com.nitnelave.CreeperHeal.utils;

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

}
