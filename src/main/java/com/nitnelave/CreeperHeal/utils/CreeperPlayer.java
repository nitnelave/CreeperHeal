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

}
