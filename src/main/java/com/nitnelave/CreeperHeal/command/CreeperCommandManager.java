package com.nitnelave.CreeperHeal.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nitnelave.CreeperHeal.CreeperTrapHandler;
import com.nitnelave.CreeperHeal.block.BurntBlockManager;
import com.nitnelave.CreeperHeal.block.ExplodedBlockManager;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.economy.TransactionFailedException;
import com.nitnelave.CreeperHeal.economy.VaultNotDetectedException;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;

/**
 * The command manager for CreeperHeal.
 * 
 * @author nitnelave
 * 
 */
public class CreeperCommandManager implements CommandExecutor {
    private final static String green = ChatColor.GREEN.toString (), purple = ChatColor.DARK_PURPLE.toString ();

    /*
     * (non-Javadoc)
     * @see
     * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
     * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
        //if it's just /ch, display help
        if (args.length != 0)
        {

            boolean allWorlds = false;
            //the last argument can be a world
            WorldConfig currentWorld = CreeperConfig.world_config.get (args[args.length - 1]);

            if (currentWorld == null)
                if (sender instanceof Player)
                    currentWorld = CreeperConfig.loadWorld (((Player) sender).getWorld ());
                else
                {
                    currentWorld = CreeperConfig.loadWorld (Bukkit.getServer ().getWorlds ().get (0));
                    sender.sendMessage ("No world specified, defaulting to " + currentWorld.getName ());
                    allWorlds = true;
                }

            String cmd = args[0];

            if (cmd.equalsIgnoreCase ("creeper"))
                currentWorld.creepers = booleanCmd (currentWorld.creepers, args, "Creepers explosions", sender);

            else if (cmd.equalsIgnoreCase ("TNT"))
                currentWorld.tnt = booleanCmd (currentWorld.tnt, args, "TNT explosions", sender);

            else if (cmd.equalsIgnoreCase ("fire"))
                currentWorld.fire = booleanCmd (currentWorld.fire, args, "Burnt blocks", sender);

            else if (cmd.equalsIgnoreCase ("ghast"))
                currentWorld.ghast = booleanCmd (currentWorld.ghast, args, "Ghast fireballs explosions", sender);

            else if (cmd.equalsIgnoreCase ("magical"))
                currentWorld.magical = booleanCmd (currentWorld.magical, args, "Magical explosions", sender);

            else if (cmd.equalsIgnoreCase ("interval"))
                CreeperConfig.waitBeforeHeal = integerCmd (CreeperConfig.waitBeforeHeal, args, "block destroyed in an explosion", sender);

            else if (cmd.equalsIgnoreCase ("burnInterval"))
                CreeperConfig.waitBeforeHealBurnt = integerCmd (CreeperConfig.waitBeforeHealBurnt, args, "burnt block", sender);

            else if (cmd.equalsIgnoreCase ("forceHeal") || cmd.equalsIgnoreCase ("heal"))
                forceCmd (args, "explosions", sender, allWorlds ? null : currentWorld);

            else if (cmd.equalsIgnoreCase ("healBurnt"))
                forceCmd (args, "burnt blocks", sender, allWorlds ? null : currentWorld);

            else if (cmd.equalsIgnoreCase ("healNear"))
                healNear (sender, args);

            else if (cmd.equalsIgnoreCase ("trap"))
            {
                if (!CreeperTrapHandler.isLoaded ())
                {
                    sender.sendMessage ("You have to install the CreeperTrap plugin to use traps");
                    return true;
                }
                if (args.length == 2 && sender instanceof Player)
                {
                    if (args[1].equalsIgnoreCase ("create") || args[1].equalsIgnoreCase ("make"))
                        try
                    {
                            CreeperTrapHandler.createTrap ((Player) sender);
                    } catch (VaultNotDetectedException e)
                    {
                        sender.sendMessage (ChatColor.RED + "[CreeperTrap] Vault is required for all economy transactions");
                        e.printStackTrace ();
                    } catch (TransactionFailedException e)
                    {
                        sender.sendMessage (ChatColor.RED + "[CreeperTrap] Critical error in the transaction");
                        e.printStackTrace ();
                    }
                    else if (args[1].equalsIgnoreCase ("remove") || args[1].equalsIgnoreCase ("delete"))
                        CreeperTrapHandler.deleteTrap ((Player) sender);
                    else if (args[1].equalsIgnoreCase ("removeall") || args[1].equalsIgnoreCase ("deleteall"))
                        CreeperTrapHandler.deleteAllTraps (sender, ((Player) sender).getName ());
                    else
                        sender.sendMessage ("/ch trap (create|remove)");
                }
                else if (args.length > 1 && (args[1].equalsIgnoreCase ("removeall") || args[1].equalsIgnoreCase ("deleteall")))
                {
                    if (args.length == 3)
                        CreeperTrapHandler.deleteAllTraps (sender, args[2]);
                    else
                        sender.sendMessage ("/ch trap removeAll (player)");
                }
                else if (!(sender instanceof Player))
                    sender.sendMessage ("Player only command");
                else
                    //misused the command, display the help
                    sender.sendMessage ("/ch trap (create|remove|removeall)");

            }

            else if (cmd.equalsIgnoreCase ("reload"))
                CreeperConfig.load ();

            else if (cmd.equalsIgnoreCase ("help"))
                sendHelp (sender);

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

        boolean admin = true, heal = true, trap = true, healNear = true, healNearSelf = true;

        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            admin = checkPermissions (player, "admin");
            heal = admin || checkPermissions (player, "heal");
            trap = checkPermissions (player, "trap.create", "trap.*");
            healNear = heal || checkPermissions (player, "heal.near.all");
            healNearSelf = checkPermissions (player, "heal.near.self");

        }

        if (!(admin || heal || trap))
            sender.sendMessage (getMessage ("plugin-help-no-commands", null, sender.getName (), null, null, null, null));

        if (admin)
        {
            sender.sendMessage (green + "/ch reload :" + purple + " reloads the config from the file.");
            sender.sendMessage (green + "/ch creeper (on/off) (world) :" + purple + " toggles creeper explosion replacement");
            sender.sendMessage (green + "/ch TNT (on/off) (world) :" + purple + " same for TNT");
            sender.sendMessage (green + "/ch Ghast (on/off) (world) :" + purple + " same for Ghast fireballs");
            sender.sendMessage (green + "/ch magical (on/off) :" + purple + " same for \"magical\" explosions.");
            sender.sendMessage (green + "/ch fire (on/off) (world) :" + purple + " same for fire");
            sender.sendMessage (green + "/ch interval [seconds] :" + purple + " Sets the interval before an explosion is replaced to x seconds");
            sender.sendMessage (green + "/ch burnInterval [seconds] :" + purple + " Same for a block burnt");
        }

        if (heal)
        {
            sender.sendMessage (green + "/ch heal (world) :" + purple + " Heals all explosions in the world, or in every world.");
            sender.sendMessage (green + "/ch healBurnt (world) :" + purple + " Heal all burnt blocks in the world, or in every world.");
        }

        if (healNear || healNearSelf)
            sender.sendMessage (green + "/ch healNear" + (healNear ? " (player)" : "") + " :" + purple + " Heals all explosions around"
                    + (healNear ? " the given player" : ""));

        if (trap && !CreeperTrapHandler.isLoaded ())
            sender.sendMessage (getMessage ("plugin-help-traps", null, sender.getName (), null, null, null, null));

    }

    /**
     * Handle the commands concerning boolean settings.
     * 
     * @param curValue
     *            The current value of the setting.
     * @param args
     *            The arguments of the command.
     * @param setting
     *            The name of the setting.
     * @param sender
     *            The sender who performed the command.
     * @return The new value.
     */
    private boolean booleanCmd (Boolean curValue, String[] args, String setting, CommandSender sender) {
        if (sender instanceof Player && !checkPermissions ((Player) sender, "admin"))
        {
            sender.sendMessage (getMessage ("no-permission-command", null, sender.getName (), null, null, null, null));
            return curValue;
        }
        boolean returnValue;

        if (args.length == 1)
            returnValue = curValue;
        else if (args[1].equalsIgnoreCase ("on") || args[1].equalsIgnoreCase ("true"))
            returnValue = true;
        else if (args[1].equalsIgnoreCase ("off") || args[1].equalsIgnoreCase ("false"))
            returnValue = false;
        else
        {
            sender.sendMessage ("/ch " + args[0] + " (on|off|time)");
            sender.sendMessage ("Toggles " + setting + " replacement on/off");
            return curValue;
        }
        sender.sendMessage (ChatColor.GREEN + setting + " replacement set to : " + returnValue);
        return returnValue;

    }

    /**
     * Handle commands concerning integer settings.
     * 
     * @param current
     *            The current value.
     * @param args
     *            The command arguments.
     * @param setting
     *            The name of the setting.
     * @param sender
     *            The command's sender.
     * @return The new value of the setting.
     */
    private int integerCmd (int current, String[] args, String setting, CommandSender sender) {
        if (sender instanceof Player && !checkPermissions ((Player) sender, "admin"))
        {
            sender.sendMessage (getMessage ("no-permission-command", null, sender.getName (), null, null, null, null));
            return current;
        }
        if (args.length == 2)
        {
            int interval = 0;
            try
            {
                interval = Integer.parseInt (args[1]);
            } catch (Exception e)
            {
                sender.sendMessage ("/ch " + args[0] + " [seconds]");
                sender.sendMessage ("Sets the interval before replacing a " + setting);
                return current;
            }
            sender.sendMessage (ChatColor.GREEN + "New interval set to : " + interval + "seconds");

            return interval;
        }
        else
        {
            sender.sendMessage ("/ch " + args[0] + " [seconds]");
            sender.sendMessage ("Sets the interval before replacing a " + setting);
            return current;
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
        if (currentWorld == null)
            for (World w : Bukkit.getServer ().getWorlds ())
            {
                WorldConfig wc = CreeperConfig.loadWorld (w);
                forceCmd (args, msg, sender, wc);
            }
        else
        {
            String cmd = args[0];

            if (sender instanceof Player && !checkPermissions ((Player) sender, "heal", "admin"))
            {
                sender.sendMessage (getMessage ("no-permission-command", null, sender.getName (), null, null, null, null));
                return;
            }

            long since = 0;
            if (args.length > 1)
                try
            {
                    since = Long.parseLong (args[1]);
            } catch (Exception e)
            {
                sender.sendMessage ("/ch " + cmd + " (seconds) (world_name | all)");
                sender.sendMessage ("If a time is specified, heals all " + msg + " since x seconds ago. Otherwise, heals all.");
                return;
            }

            boolean burnt = cmd.equalsIgnoreCase ("healBurnt");
            if (args.length > 2)
            {
                if (args[2].equalsIgnoreCase ("all"))
                {
                    for (WorldConfig w : CreeperConfig.world_config.values ())
                        if (burnt)
                            BurntBlockManager.forceReplaceBurnt (since, w);
                        else
                            ExplodedBlockManager.forceReplace (since, w);
                }
                else if (burnt)
                    BurntBlockManager.forceReplaceBurnt (since, currentWorld);
                else
                    ExplodedBlockManager.forceReplace (since, currentWorld);
            }
            else if (burnt)
                BurntBlockManager.forceReplaceBurnt (since, currentWorld);
            else
                ExplodedBlockManager.forceReplace (since, currentWorld);

            sender.sendMessage (ChatColor.GREEN + "Explosions healed");
        }
    }

    /**
     * Replace all explosions near a player.
     * @param sender The sender. If it is the console, then the command is ignored.
     * @param args The command arguments.
     */
    private void healNear (CommandSender sender, String[] args) {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            boolean hasPermission = checkPermissions (player, "heal", "admin");
            Player target;
            if (args.length > 1)
            {
                hasPermission = hasPermission || checkPermissions (player, "heal.near.all");
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
