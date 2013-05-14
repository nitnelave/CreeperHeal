package com.nitnelave.CreeperHeal.command;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import com.nitnelave.CreeperHeal.PluginHandler;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WCfgVal;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;

/**
 * The command manager for CreeperHeal.
 * 
 * @author nitnelave
 * 
 */
public class CreeperCommandManager implements CommandExecutor {
    private final static String GREEN = ChatColor.GREEN.toString (), PURPLE = ChatColor.DARK_PURPLE.toString ();

    /**
     * Register commands.
     */
    public static void registerCommands () {
        CommandMap commandMap = null;
        try
        {
            Field field = SimplePluginManager.class.getDeclaredField ("commandMap");
            field.setAccessible (true);
            commandMap = (CommandMap) (field.get (Bukkit.getServer ().getPluginManager ()));
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace ();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace ();
        }

        String[] aliases = {"CreeperHeal", CreeperConfig.getAlias ()};
        CreeperCommand com = new CreeperCommand (aliases, "", "", new CreeperCommandManager ());

        if (commandMap != null)
            commandMap.register ("_", com);

    }

    /*
     * (non-Javadoc)
     * @see
     * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
     * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length != 0)
        {
            String cmd = args[0];
            if (cmd.equalsIgnoreCase ("trap"))
                return PluginHandler.trapCommand (sender, args);

            //the last argument can be a world
            WorldConfig currentWorld = null;
            World w = Bukkit.getWorld (args[args.length - 1]);
            if (w != null)
                currentWorld = CreeperConfig.getWorld (args[args.length - 1]);

            if (currentWorld == null)
                if (sender instanceof Player)
                    currentWorld = CreeperConfig.getWorld (((Player) sender).getWorld ());

            if (cmd.equalsIgnoreCase ("creeper"))
                booleanCmd (currentWorld, WCfgVal.CREEPERS, args, "Creepers explosions", sender);

            else if (cmd.equalsIgnoreCase ("TNT"))
                booleanCmd (currentWorld, WCfgVal.TNT, args, "TNT explosions", sender);

            else if (cmd.equalsIgnoreCase ("fire"))
                booleanCmd (currentWorld, WCfgVal.FIRE, args, "Burnt blocks", sender);

            else if (cmd.equalsIgnoreCase ("ghast"))
                booleanCmd (currentWorld, WCfgVal.GHAST, args, "Ghast fireballs explosions", sender);

            else if (cmd.equalsIgnoreCase ("custom"))
                booleanCmd (currentWorld, WCfgVal.CUSTOM, args, "Magical explosions", sender);

            else if (cmd.equalsIgnoreCase ("waitBeforeHeal") || cmd.equalsIgnoreCase ("wbh"))
                integerCmd (CfgVal.WAIT_BEFORE_HEAL, args, "Sets the interval before replacing a block destroyed in an explosion", "seconds", sender);

            else if (cmd.equalsIgnoreCase ("waitBeforeHealBurnt") || cmd.equalsIgnoreCase ("wbhb"))
                integerCmd (CfgVal.WAIT_BEFORE_HEAL_BURNT, args, "Sets the interval before replacing a burnt block", "seconds", sender);

            else if (cmd.equalsIgnoreCase ("blockPerBlockInterval") || cmd.equalsIgnoreCase ("bpbi"))
            {
                integerCmd (CfgVal.BLOCK_PER_BLOCK_INTERVAL, args, "Sets the interval between block replacement", "ticks", sender);
                ExplodedBlockManager.rescheduleTask ();
            }

            else if (cmd.equalsIgnoreCase ("forceHeal") || cmd.equalsIgnoreCase ("heal"))
                forceCmd (args, "explosions", sender, currentWorld);

            else if (cmd.equalsIgnoreCase ("healBurnt"))
                forceCmd (args, "burnt blocks", sender, currentWorld);

            else if (cmd.equalsIgnoreCase ("healNear"))
                healNear (sender, args);

            else if (cmd.equalsIgnoreCase ("reload"))
                CreeperConfig.load ();

            else if (cmd.equalsIgnoreCase ("help"))
                sendHelp (sender);
            else if (cmd.equalsIgnoreCase ("on"))
                enable (true, currentWorld, sender);
            else if (cmd.equalsIgnoreCase ("off"))
                enable (false, currentWorld, sender);

            else
            {
                sender.sendMessage ("/ch help");
                return true;
            }

            //in case of a change of setting via a command, write it to the file
            CreeperConfig.write ();
        }
        else
        {
            sender.sendMessage ("/ch help");
            return true;
        }

        return true;
    }

    /*
     * Displays the help according to the permissions of the player.
     */
    private void sendHelp (CommandSender sender) {
        sender.sendMessage ("CreeperHeal -- Repair explosions damage and make traps");
        sender.sendMessage ("--------------------------------------------");

        boolean admin = true, heal = true, healNear = true, healNearOther = true;

        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            admin = checkPermissions (player, "admin");
            heal = admin || checkPermissions (player, "heal");
            healNearOther = heal || checkPermissions (player, "heal.near.all");
            healNear = healNearOther || checkPermissions (player, "heal.near.self");

        }

        if (!(admin || healNear))
            sender.sendMessage (getMessage ("plugin-help-no-commands", null, sender.getName (), null, null, null, null));
        else
        {
            if (admin)
            {
                sender.sendMessage (GREEN + "/ch reload :" + PURPLE + " reloads the config from the file.");
                sender.sendMessage (GREEN + "/ch (creeper|TNT|ghast|custom|fire) (on/off) (world) :" + PURPLE + " toggle replacement");
                sender.sendMessage (GREEN + "/ch [on|off] (world) :" + PURPLE + " toggle CH for this world.");
                sender.sendMessage (GREEN + "/ch (waitBeforeHeal|wbh) [seconds] :" + PURPLE + " Sets the interval before an explosion is replaced to x seconds");
                sender.sendMessage (GREEN + "/ch (waitBeforeHealBurnt|wbhb) [seconds] :" + PURPLE + " Same for a burnt block");
                sender.sendMessage (GREEN + "/ch (blockPerBlockInterval|bpbi) [ticks] :" + PURPLE + " Sets the block replacement rate");
            }

            if (heal)
            {
                sender.sendMessage (GREEN + "/ch heal (world) :" + PURPLE + " Heals all explosions.");
                sender.sendMessage (GREEN + "/ch healBurnt (world) :" + PURPLE + " Heal all burnt blocks.");
            }

            if (healNear)
                sender.sendMessage (GREEN + "/ch healNear" + (healNearOther ? " (player)" : "") + " :" + PURPLE + " Heals all explosions around"
                        + (healNearOther ? " the given player" : ""));
        }
    }

    /**
     * Handle the commands concerning boolean settings.
     * 
     * @param world
     *            The world in which to change the setting.
     * @param key
     *            The setting to change.
     * @param args
     *            The arguments of the command.
     * @param setting
     *            The name of the setting.
     * @param sender
     *            The sender who performed the command.
     */
    private void booleanCmd (WorldConfig world, WCfgVal key, String[] args, String setting, CommandSender sender) {
        if (sender instanceof Player && !checkPermissions ((Player) sender, "admin"))
            sender.sendMessage (getMessage ("no-permission-command", null, sender.getName (), null, null, null, null));

        if (args.length == 1)
            world.setBool (key, !world.getBool (key));
        else if (args[1].equalsIgnoreCase ("on") || args[1].equalsIgnoreCase ("true"))
            world.setBool (key, true);
        else if (args[1].equalsIgnoreCase ("off") || args[1].equalsIgnoreCase ("false"))
            world.setBool (key, false);
        else
        {
            sender.sendMessage ("/ch " + args[0] + " (on|off|time)");
            sender.sendMessage ("Toggles " + setting + " replacement on/off");
            return;
        }
        sender.sendMessage (ChatColor.GREEN + setting + " replacement set to : " + world.getBool (key));
    }

    /**
     * Handle commands concerning integer settings.
     * 
     * @param key
     *            The setting to change.
     * @param args
     *            The command arguments.
     * @param setting
     *            The name of the setting.
     * @param sender
     *            The command's sender.
     * @return The new value of the setting.
     */
    private void integerCmd (CfgVal key, String[] args, String help, String unit, CommandSender sender) {
        if (sender instanceof Player && !checkPermissions ((Player) sender, "admin"))
            sender.sendMessage (getMessage ("no-permission-command", null, sender.getName (), null, null, null, null));

        if (args.length == 2)
        {
            int interval = 0;
            try
            {
                interval = Integer.parseInt (args[1]);
            } catch (NumberFormatException e)
            {
                sender.sendMessage ("/ch " + args[0] + " [" + unit + "]");
                sender.sendMessage (help);
                return;
            }
            sender.sendMessage (ChatColor.GREEN + "New interval set to : " + interval + " " + unit);

            CreeperConfig.setInt (key, interval);
        }
        else
        {
            sender.sendMessage ("/ch " + args[0] + " [" + unit + "]");
            sender.sendMessage (help);
        }
    }

    /**
     * Handle force commands (i.e. force instant replacement of blocks).
     * 
     * @param args
     *            The command arguments.
     * @param msg
     *            The name of the type of blocks.
     * @param sender
     *            The command's sender.
     * @param currentWorld
     *            The world to which the command must be applied. If null, apply
     *            to all worlds.
     */
    private void forceCmd (String[] args, String msg, CommandSender sender, WorldConfig currentWorld) {
        String cmd = args[0];

        if (sender instanceof Player && !checkPermissions ((Player) sender, "heal", "admin"))
        {
            sender.sendMessage (getMessage ("no-permission-command", null, sender.getName (), null, null, null, null));
            return;
        }

        boolean burnt = cmd.equalsIgnoreCase ("healBurnt");
        if (currentWorld == null)
            if (burnt)
                BurntBlockManager.forceReplaceBurnt ();
            else
                ExplodedBlockManager.forceReplace ();
        else if (burnt)
            BurntBlockManager.forceReplaceBurnt (currentWorld);
        else
            ExplodedBlockManager.forceReplace (currentWorld);

        sender.sendMessage (ChatColor.GREEN + "Explosions healed");
    }

    /**
     * Replace all explosions near a player.
     * 
     * @param sender
     *            The sender. If it is the console, then the command is ignored.
     * @param args
     *            The command arguments.
     */
    private void healNear (CommandSender sender, String[] args) {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            boolean hasPermission = checkPermissions (player, "heal", "admin", "heal.near.all");
            Player target;
            if (args.length > 1)
            {
                if (!hasPermission)
                {
                    player.sendMessage (getMessage ("no-permission-command", player.getWorld ().getName (), sender.getName (), null, null, null, null));
                    return;
                }
                target = Bukkit.getServer ().getPlayer (args[1]);
                if (target == null)
                {
                    player.sendMessage (ChatColor.RED + "This player is not online. /ch healNear <someone>");
                    return;
                }

            }
            else
            {
                hasPermission = hasPermission || checkPermissions (player, "heal.near.self");
                if (!hasPermission)
                {
                    sender.sendMessage (getMessage ("no-permission-command", null, sender.getName (), null, null, null, null));
                    return;
                }
                target = player;
            }
            ExplodedBlockManager.replaceNear (target);
        }
        else if (args.length > 1)
        {
            Player target = Bukkit.getServer ().getPlayer (args[1]);
            if (target == null)
            {
                sender.sendMessage (ChatColor.RED + "This player is not online. /ch healNear <someone>");
                return;
            }
            ExplodedBlockManager.replaceNear (target);

        }
        else
            sender.sendMessage (ChatColor.RED + "Please give the target's name.");
    }

    /*
     * Enable of disable CreeperHeal in a world.
     */
    private void enable (boolean enable, WorldConfig currentWorld, CommandSender sender) {
        boolean hasPerm = true;
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            hasPerm = checkPermissions (player, "admin");
        }
        if (hasPerm)
        {
            if (currentWorld == null)
            {
                for (World w : Bukkit.getWorlds ())
                    enable (enable, CreeperConfig.getWorld (w), sender);
                return;
            }
            currentWorld.setBool (WCfgVal.WORLD_ON, enable);
            sender.sendMessage (GREEN + (enable ? "En" : "Dis") + "abled CH in world : " + currentWorld.getName ());
        }
    }

    /*
     * Check if the player has at least one of the permissions.
     */
    private boolean checkPermissions (Player player, String... nodes) {
        return CreeperPermissionManager.checkPermissions (player, false, nodes);
    }

    /*
     * Get the formatted message to send to a player.
     */
    private String getMessage (String message, String... values) {
        return CreeperMessenger.processMessage (message, values);
    }

}
