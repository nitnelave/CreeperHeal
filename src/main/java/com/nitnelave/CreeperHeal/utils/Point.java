package com.nitnelave.CreeperHeal.utils;

public class Point {
	public int x, y;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Point) {
			Point p = (Point) o;
			if(p.x == x && p.y == y)
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return x * 10000 + y;
	}
	
}
