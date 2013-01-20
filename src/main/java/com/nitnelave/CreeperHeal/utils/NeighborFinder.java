package com.nitnelave.CreeperHeal.utils;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Location;

public abstract class NeighborFinder<T> {
	
	public static final int BLOCK_SIZE = 64;
	protected static int count = 0;
	protected HashMap<Point, LinkedList<T>> map = new HashMap<Point, LinkedList<T>>();
	
	
	public void addElement(T el, double x, double y) {
		Point p = new Point((int)(x/BLOCK_SIZE), (int)(y/BLOCK_SIZE));
		if(map.get(p) == null)
			map.put(p, new LinkedList<T>());
		map.get(p).add(el);
	}
	
	public void removeElement(T el, double x, double y) {
		Point p = new Point((int)(x/BLOCK_SIZE), (int)(y/BLOCK_SIZE));
		LinkedList<T> list = map.get(p);
		if(list == null)
			return;
		list.remove(el);
		if(list.isEmpty())
			map.remove(p);
	}
	
	public boolean hasNeighbor(Location loc) {
		int x = ((int)loc.getX())/BLOCK_SIZE, y = ((int)loc.getZ())/BLOCK_SIZE;
		LinkedList<T> list = map.get(new Point(x, y));
		if(hasNeighbor(loc, list))
			return true;
		for(int i = -1; i < 2; i++) {
			for(int j = -1; j < 2; j++) {
				if( i == 0 && j == 0)
					continue;
				list = map.get(new Point(x + i, y + j));
				if(hasNeighbor(loc, list))
					return true;
			}
		}
		return false;
	}

	protected abstract boolean hasNeighbor(Location loc, LinkedList<T> list);
		
	public boolean isEmpty() {
		return map.isEmpty();
	}
}
