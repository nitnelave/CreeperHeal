package com.nitnelave.CreeperHeal.utils;

import org.bukkit.entity.Player;

import com.nitnelave.CreeperHeal.CreeperHeal;


public class CreeperPlayer
{

	private Player player;
	protected boolean lava, tnt, fire, blacklist, spawnEggs, pvp;

	public enum WarningCause {
		LAVA, TNT, FIRE, BLACKLIST, SPAWN_EGG, PVP
	}

	public CreeperPlayer(Player player, CreeperHeal plugin)
	{
		this.player = player;
		loadPermissions();
	}

	/* Used to compare CreeperPlayers, to remove from a list. */
	public CreeperPlayer(Player player) {
		this.player = player;
	}

	private void loadPermissions()
	{
		lava = CreeperPermissionManager.checkPermissions(player, false, "grief.warn.*", "grief.warn.lava");
		tnt = CreeperPermissionManager.checkPermissions(player, false, "grief.warn.*", "grief.warn.tnt");
		fire = CreeperPermissionManager.checkPermissions(player, false, "grief.warn.*", "grief.warn.fire");
		blacklist = CreeperPermissionManager.checkPermissions(player, false, "grief.warn.*", "grief.warn.blacklist");
		spawnEggs = CreeperPermissionManager.checkPermissions(player, false, "grief.warn.*", "grief.warn.spawnEggs");
		pvp = CreeperPermissionManager.checkPermissions(player, false, "grief.warn.*", "grief.warn.pvp");
	}

	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	public void warnPlayer(Player player, WarningCause cause, String message)
    {
	    if(cause == WarningCause.BLACKLIST && blacklist || cause == WarningCause.TNT && tnt || cause == WarningCause.FIRE && fire
	    		|| cause == WarningCause.LAVA && lava || cause == WarningCause.SPAWN_EGG && spawnEggs || cause == WarningCause.PVP && pvp)
	    	player.sendMessage(message);
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CreeperPlayer))
			return false;
		CreeperPlayer other = (CreeperPlayer) obj;
		return player.getName().equals(other.player.getName());
	}

}
