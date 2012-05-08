package com.nitnelave.CreeperHeal;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class CreeperPlayer
{

	private CreeperHeal plugin;
	private Player player;
	protected boolean lava, tnt, fire, blacklist, spawnEggs;

	public enum WarningCause {
		LAVA, TNT, FIRE, BLACKLIST, SPAWN_EGG
	}

	public CreeperPlayer(Player player, CreeperHeal plugin)
	{
		this.player = player;
		this.plugin = plugin;
		loadPermissions();
	}

	private void loadPermissions()
	{
		lava = plugin.checkPermissions(player, false, "warn.*", "warn.lava");
		tnt = plugin.checkPermissions(player, false, "warn.*", "warn.tnt");
		fire = plugin.checkPermissions(player, false, "warn.*", "warn.fire");
		blacklist = plugin.checkPermissions(player, false, "warn.*", "warn.blacklist");
		spawnEggs = plugin.checkPermissions(player, false, "warn.*", "warn.spawnEggs");
	}

	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	public void warnPlayer(WarningCause cause, String offender, Location loc, boolean blocked, String material)
	{
		boolean force = false;
		if(cause == WarningCause.LAVA && lava)
		{
			material = "LAVA";
			force = true;
			cause = WarningCause.BLACKLIST;
		}
		else if(cause == WarningCause.FIRE && fire)
		{
			player.sendMessage(ChatColor.RED + "Player " + offender + (blocked?" was prevented from starting":" has started") + " a fire in world : " + loc.getWorld().getName());
		}
		else if(cause == WarningCause.TNT && tnt)
		{
			material = "TNT";
			force = true;
			cause = WarningCause.BLACKLIST;
		}
		else if(cause == WarningCause.SPAWN_EGG && spawnEggs)
		{
			player.sendMessage(ChatColor.RED + "Player " + offender + (blocked?"tried to spawn":"spawned") + " a " + material + "in world : " + loc.getWorld().getName());
		}
		if(cause == WarningCause.BLACKLIST && (blacklist || force))
		{
			player.sendMessage(ChatColor.RED + "Player " + offender + (blocked?" was prevented from placing ":" has placed ") + material + " in world : " + loc.getWorld().getName());
		}
	}

}
