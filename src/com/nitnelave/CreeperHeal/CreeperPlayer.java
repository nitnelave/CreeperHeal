package com.nitnelave.CreeperHeal;

import org.bukkit.entity.Player;


public class CreeperPlayer
{

	private CreeperHeal plugin;
	private Player player;
	protected boolean lava, tnt, fire, blacklist, spawnEggs, pvp;

	public enum WarningCause {
		LAVA, TNT, FIRE, BLACKLIST, SPAWN_EGG, PVP
	}

	public CreeperPlayer(Player player, CreeperHeal plugin)
	{
		this.player = player;
		this.plugin = plugin;
		loadPermissions();
	}

	private void loadPermissions()
	{
		lava = plugin.checkPermissions(player, false, "grief.warn.*", "grief.warn.lava");
		tnt = plugin.checkPermissions(player, false, "grief.warn.*", "grief.warn.tnt");
		fire = plugin.checkPermissions(player, false, "grief.warn.*", "grief.warn.fire");
		blacklist = plugin.checkPermissions(player, false, "grief.warn.*", "grief.warn.blacklist");
		spawnEggs = plugin.checkPermissions(player, false, "grief.warn.*", "grief.warn.spawnEggs");
		pvp = plugin.checkPermissions(player, false, "grief.warn.*", "grief.warn.pvp");
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
