package com.nitnelave.CreeperHeal;


import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreeperCommandManager implements CommandExecutor
{
	private CreeperHeal plugin;
	private final static String green = ChatColor.GREEN.toString(), purple = ChatColor.DARK_PURPLE.toString();

	public CreeperCommandManager(CreeperHeal instance)
	{
		plugin = instance;
	}



	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

		if(args.length != 0) 
		{        //if it's just /ch, display help

			boolean allWorlds = false;
			WorldConfig current_world = getConfig().world_config.get(args[args.length - 1]);   //the last argument can be a world

			if(current_world == null) 
			{		//if the last argument was not a world

				if(sender instanceof Player)
					current_world = plugin.loadWorld( ((Player)sender).getWorld());		//get the player's world
				else
				{										//or get the first (normal) world
					current_world = plugin.loadWorld(plugin.getServer().getWorlds().get(0));
					sender.sendMessage("No world specified, defaulting to " + current_world.getName());
					allWorlds = true;
				}
			}

			String cmd = args[0];	//command argument


			if(cmd.equalsIgnoreCase("creeper"))
				current_world.creepers = booleanCmd(current_world.creepers, args, "Creepers explosions", sender);

			else if(cmd.equalsIgnoreCase("TNT"))        //same as above
				current_world.tnt = booleanCmd(current_world.tnt, args, "TNT explosions", sender);

			else if(cmd.equalsIgnoreCase("fire"))
				current_world.fire = booleanCmd(current_world.fire, args, "Burnt blocks", sender);

			else if(cmd.equalsIgnoreCase("ghast"))
				current_world.ghast = booleanCmd(current_world.ghast, args, "Ghast fireballs explosions", sender);

			else if(cmd.equalsIgnoreCase("magical"))
				current_world.magical = booleanCmd(current_world.magical, args, "Magical explosions", sender);

			else if(cmd.equalsIgnoreCase("interval"))
				getConfig().waitBeforeHeal = integerCmd(getConfig().waitBeforeHeal, args, "block destroyed in an explosion", sender);

			else if(cmd.equalsIgnoreCase("burnInterval"))
				getConfig().waitBeforeHealBurnt = integerCmd(getConfig().waitBeforeHealBurnt, args, "burnt block", sender);

			else if(cmd.equalsIgnoreCase("forceHeal") || cmd.equalsIgnoreCase("heal"))
				forceCmd(args, "explosions", sender, current_world, allWorlds);

			else if(cmd.equalsIgnoreCase("healBurnt"))
				forceCmd(args, "burnt blocks", sender, current_world, allWorlds);

			else if(cmd.equalsIgnoreCase("healNear"))
				healNear(sender, args);

			else if(cmd.equalsIgnoreCase("trap")) {
				if(plugin.creeperTrap == null)
				{
					sender.sendMessage("You have to install the CreeperTrap plugin to use traps");
					return true;
				}
				if(args.length == 2 && sender instanceof Player) 
				{
					if(args[1].equalsIgnoreCase("create") || args[1].equalsIgnoreCase("make"))
						try
					{
							plugin.createTrap((Player)sender);
					}
					catch (VaultNotDetectedException e)
					{
						sender.sendMessage(ChatColor.RED + "[CreeperTrap] Vault is required for all economy transactions");
						e.printStackTrace();
					}
					catch (TransactionFailedException e)
					{
						sender.sendMessage(ChatColor.RED + "[CreeperTrap] Critical error in the transaction");
						e.printStackTrace();
					}
					else if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete"))
						plugin.deleteTrap((Player)sender);
					else if(args[1].equalsIgnoreCase("removeall") || args[1].equalsIgnoreCase("deleteall"))
						plugin.deleteAllTraps(sender, ((Player)sender).getName());
					else
						sender.sendMessage("/ch trap (create|remove)");
				}
				else
				{
					if(args.length > 1 && (args[1].equalsIgnoreCase("removeall") || args[1].equalsIgnoreCase("deleteall")))
					{
						if(args.length == 3)
							plugin.deleteAllTraps(sender, args[2]);
						else
							sender.sendMessage("/ch trap removeAll (player)");
					}
					else if(!(sender instanceof Player))
						sender.sendMessage("Player only command");
					else
						sender.sendMessage("/ch trap (create|remove|removeall)");		//misused the command, display the help
				}

			}

			else if(cmd.equalsIgnoreCase("reload"))
				getConfig().load();

			else if(cmd.equalsIgnoreCase("help"))
				sendHelp(sender);

			else 
			{        // /ch something gets back to the help
				sender.sendMessage("/ch help");
				return true;
			}

			getConfig().write();		//in case of a change of setting via a command, write it to the file
		}
		else {
			sender.sendMessage("/ch help");
			return true;
		}

		return true;		//always return true as I display my own help
	}


	private void sendHelp(CommandSender sender) {		//displays the help according to the permissions of the player
		sender.sendMessage("CreeperHeal -- Repair explosions damage and make traps");
		sender.sendMessage("--------------------------------------------");

		boolean admin = true, heal = true, trap = true, healNear = true, healNearSelf = true;

		if(sender instanceof Player){
			Player player = (Player) sender;
			admin = checkPermissions(player, "admin");
			heal = admin || checkPermissions(player, "heal");
			trap = checkPermissions(player, "trap.create", "trap.*");
			healNear = heal || checkPermissions(player, "heal.near.all");
			healNearSelf = checkPermissions(player, "heal.near.self");

		}

		if(!(admin || heal || trap))
			sender.sendMessage(getMessage("plugin-help-no-commands", null, sender.getName(), null, null, null, null));

		if(admin){
			sender.sendMessage(green + "/ch reload :" + purple + " reloads the config from the file.");
			sender.sendMessage(green + "/ch creeper (on/off) (world) :" + purple + " toggles creeper explosion replacement");
			sender.sendMessage(green + "/ch TNT (on/off) (world) :" + purple + " same for TNT");
			sender.sendMessage(green + "/ch Ghast (on/off) (world) :" + purple + " same for Ghast fireballs");
			sender.sendMessage(green + "/ch magical (on/off) :" + purple + " same for \"magical\" explosions.");
			sender.sendMessage(green + "/ch fire (on/off) (world) :" + purple + " same for fire");
			sender.sendMessage(green + "/ch interval [seconds] :" + purple + " Sets the interval before an explosion is replaced to x seconds");
			sender.sendMessage(green + "/ch burnInterval [seconds] :" + purple + " Same for a block burnt");
		}

		if(heal){
			sender.sendMessage(green + "/ch heal (world) :" + purple + " Heals all explosions in the world, or in every world.");
			sender.sendMessage(green + "/ch healBurnt (world) :" + purple + " Heal all burnt blocks in the world, or in every world.");
		}

		if(healNear || healNearSelf)
			sender.sendMessage(green + "/ch healNear" + (healNear?" (player)":"") + " :" + purple + " Heals all explosions around" + (healNear?" the given player":""));


		if(trap && plugin.creeperTrap != null)
			sender.sendMessage(getMessage("plugin-help-traps", null, sender.getName(), null, null, null, null));


	}


	private boolean booleanCmd(Boolean creepers, String[] args, String msg, CommandSender sender) 
	{		//changes a setting true/false
		if(sender instanceof Player) 
		{
			if(!checkPermissions((Player)sender, "admin")) {
				sender.sendMessage(ChatColor.RED + "You don’t have the permission");
				return creepers;
			}
		}
		boolean return_value = false;

		if(args.length == 1)
			return_value = creepers;
		else if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true"))
			return_value = true;
		else if(args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("false"))
			return_value = false;
		else {
			sender.sendMessage("/ch " + args[0] + " (on|off|time)");
			sender.sendMessage("Toggles " + msg + " replacement on/off");
			return creepers;
		}
		sender.sendMessage(ChatColor.GREEN + msg + " replacement set to : "+return_value);
		return return_value;

	}

	private int integerCmd(int current, String[] args, String msg, CommandSender sender) 
	{		//changes a setting with a number
		if(sender instanceof Player) {
			if(!checkPermissions((Player) sender, "admin")) {
				sender.sendMessage(getMessage("no-permission-command", null, sender.getName(), null, null, null, null));
				return current;
			}
		}
		if(args.length == 2){
			int interval = 0;
			try {
				interval = Integer.parseInt(args[1]);
			}
			catch (Exception e) {
				sender.sendMessage("/ch " + args[0] + " [seconds]");
				sender.sendMessage("Sets the interval before replacing a " + msg);
				return current;
			}
			sender.sendMessage(ChatColor.GREEN+ "New interval set to : "+interval + "seconds");

			return interval;
		}
		else {
			sender.sendMessage("/ch " + args[0] + " [seconds]");
			sender.sendMessage("Sets the interval before replacing a " + msg);
			return current;
		}
	}

	public void forceCmd(String[] args, String msg, CommandSender sender, WorldConfig current_world, boolean allWorlds) 
	{
		if(allWorlds)
		{
			for(World w : plugin.getServer().getWorlds())
			{
				WorldConfig wc = plugin.loadWorld(w);
				forceCmd(args, msg, sender, wc, false);
			}
		}
		else
		{
			String cmd = args[0];

			if(sender instanceof Player) 
			{
				if(!checkPermissions((Player)sender, "heal", "admin")) 
				{
					sender.sendMessage(getMessage("no-permission-command", null, sender.getName(), null, null, null, null));
					return;
				}
			}   

			long since = 0;               
			if(args.length > 1){
				try{
					since = Long.parseLong(args[1]);
				}
				catch (Exception e) {
					sender.sendMessage("/ch " + cmd + " (seconds) (world_name | all)");
					sender.sendMessage("If a time is specified, heals all " + msg + " since x seconds ago. Otherwise, heals all.");
					return;
				}
			}
			boolean burnt = cmd.equalsIgnoreCase("healBurnt");
			if(args.length >2) {
				if(args[2].equalsIgnoreCase("all")) {
					for(WorldConfig w : getConfig().world_config.values()) {
						if(burnt)
							plugin.force_replace_burnt(since, w);
						else
							plugin.force_replace(since, w);
					}
				}
				else {
					if(burnt)
						plugin.force_replace_burnt(since, current_world);
					else
						plugin.force_replace(since, current_world);
				}
			}
			else {
				if(burnt)
					plugin.force_replace_burnt(since, current_world);
				else
					plugin.force_replace(since, current_world);
			}

			sender.sendMessage(ChatColor.GREEN + "Explosions healed");
		}
	}




	private CreeperConfig getConfig()
	{
		return plugin.config;
	}


	private void healNear(CommandSender sender, String[] args)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			boolean hasPermission = checkPermissions(player, "heal", "admin");
			Player target;
			if(args.length > 1)
			{
				hasPermission = hasPermission || checkPermissions(player, "heal.near.all");
				if(!hasPermission)
				{
					player.sendMessage(getMessage("no-permission-command", player.getWorld().getName(), sender.getName(), null, null, null, null));
					return;
				}
				target = plugin.getServer().getPlayer(args[1]);
				if(target == null)
				{
					player.sendMessage(ChatColor.RED + "This player is not online. /ch healNear <someone>");
					return;
				}

			}
			else
			{
				hasPermission = hasPermission || checkPermissions(player, "heal.near.self");
				if(!hasPermission)
				{
					sender.sendMessage(getMessage("no-permission-command", null, sender.getName(), null, null, null, null));
					return;
				}
				target = player;
			}
			plugin.replaceNear(target);

		}
	}

	private boolean checkPermissions(Player player, String... nodes)
	{
		return plugin.getPermissionManager().checkPermissions(player, true, nodes);
	}

	private String getMessage(String message, String... values)
	{
		return plugin.messenger.processMessage(message, values);
	}



}
