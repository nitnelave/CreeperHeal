package com.nitnelave.CreeperHeal.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FakeBlockTask implements Runnable{
	private int type;
	private byte data;
	private Location loc;
	
	public FakeBlockTask(Location loc, int type, byte data) {
		this.loc = loc;
		this.type = type;
		this.data = data;
	}
	
	@Override
    public void run()
    {
	    for(Player p : Bukkit.getServer().getOnlinePlayers())
	    	p.sendBlockChange(loc, type, data);
    }

}
