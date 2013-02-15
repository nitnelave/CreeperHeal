package com.nitnelave.CreeperHeal;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nitnelave.CreeperHeal.economy.CreeperEconomy;
import com.nitnelave.CreeperHeal.economy.TransactionFailedException;
import com.nitnelave.CreeperHeal.economy.VaultNotDetectedException;
import com.nitnelave.CreeperHeal.utils.CreeperMessenger;
import com.nitnelave.CreeperHeal.utils.CreeperPermissionManager;
import com.nitnelave.CreeperTrap.CreeperTrap;

public class CreeperTrapHandler {
    /*
     * HashSet for aiming.
     */
    private final static HashSet<Byte> TRANSPARENT_BLOCKS;

    static
    {
        ArrayList<Byte> tmpList = new ArrayList<Byte> ();
        for (Material m : Material.values ())
            if (m.isTransparent ())
                tmpList.add ((byte) m.getId ());
        TRANSPARENT_BLOCKS = new HashSet<Byte> (tmpList);
    }

    protected static CreeperTrap creeperTrap = null;

    public CreeperTrapHandler (CreeperTrap cTrap) {
        creeperTrap = cTrap;
    }

    public static boolean isLoaded() {
        return creeperTrap != null;
    }


    public static boolean deleteTrap(Player p)
    {
        boolean delete_own, delete_all = checkPermissions(p, false, "trap.remove.all", "trap.*");
        delete_own = delete_all;
        if(!delete_own)
            delete_own = checkPermissions(p, false, "trap.remove.own");
        if(delete_own) {

            Block block = p.getTargetBlock (TRANSPARENT_BLOCKS, 10);

            if(block.getType() == Material.TNT) {

                String owner = creeperTrap.getTrapOwner(block.getLocation());

                if(owner == null) {

                    p.sendMessage(CreeperMessenger.processMessage("target-not-trap", p.getWorld().getName(), p.getName(), null, null, null, null));
                    return false;
                }

                else if(owner.equalsIgnoreCase(p.getName())){

                    creeperTrap.deleteTrap(block.getLocation());

                    p.sendMessage(CreeperMessenger.processMessage("trap-removed", p.getWorld().getName(), p.getName(), null, null, null, null));
                    return true;

                }
                else if(delete_all) {

                    creeperTrap.deleteTrap(block.getLocation());

                    p.sendMessage(CreeperMessenger.processMessage("trap-removed", p.getWorld().getName(), p.getName(), null, null, null, null));
                    return true;
                }

                else {
                    p.sendMessage(CreeperMessenger.processMessage("cant-remove-trap", p.getWorld().getName(), p.getName(), null, null, null, null));
                    return false;
                }

            }

            else {
                p.sendMessage(CreeperMessenger.processMessage("trap-not-TNT", p.getWorld().getName(), p.getName(), null, null, null, null));
                return false;
            }

        }

        else {
            p.sendMessage(CreeperMessenger.processMessage("trap-protected", p.getWorld().getName(), p.getName(), null, null, null, null));
            return false;

        }
    }


    public static void deleteAllTraps(CommandSender sender, String target)
    {
        boolean delete_own, delete_all;
        if(sender instanceof Player)
        {
            Player p = (Player) sender;
            delete_all = checkPermissions(p, false, "trap.remove.all", "trap.*");
            delete_own = delete_all;
            if(!delete_own)
                delete_own = checkPermissions(p, false, "trap.remove.own");
        }
        else
            delete_own = delete_all = true;

        if(delete_own && sender instanceof Player && sender.getName().equals(target))
            creeperTrap.deleteAll(target);
        else if (delete_all) {
            if (Bukkit.getServer ().getPlayer (target).hasPlayedBefore ())
                creeperTrap.deleteAll(target);
            else
                sender.sendMessage("This player doesn't exist");

        }
        else
            sender.sendMessage(CreeperMessenger.processMessage("no-permission-command", null, sender.getName(), null, null, null, null));
    }


    public static int getMaxTraps(Player player)
    {
        return creeperTrap.getMaxTraps(player);
    }

    public static boolean isTrap(Block block)
    {
        if(creeperTrap == null)
            return false;
        else
            return creeperTrap.isTrap(block);
    }

    public static void createTrap(Player p) throws VaultNotDetectedException, TransactionFailedException
    {
        if(checkPermissions(p, false,"trap.create", "trap.*")) {
            Block block = p.getTargetBlock (TRANSPARENT_BLOCKS, 10);
            if(block.getType() == Material.TNT) {
                String owner = creeperTrap.getTrapOwner(block);
                if(owner == null) {
                    double cost = creeperTrap.getTrapFee(p);
                    if(checkPermissions(p, false, "trap.bypass.fee"))
                        cost = 0;
                    if(playerHasEnough(p, cost))
                    {
                        boolean bypassMaxTraps = checkPermissions(p, false, "trap.bypass.maxTraps");
                        if(creeperTrap.createTrap(block.getLocation(), p.getName(), bypassMaxTraps))
                        {
                            finePlayer(p, cost);
                            if(cost == 0)
                                p.sendMessage(CreeperMessenger.processMessage("trap-success", p.getWorld().getName(), p.getName(), null, null, null, null));
                        }
                        else
                            p.sendMessage(CreeperMessenger.processMessage("too-many-traps", p.getWorld().getName(), p.getName(), null, null, null, Integer.toString(getMaxTraps(p))));
                    }
                    else
                        p.sendMessage(CreeperMessenger.processMessage("not-enough-money", p.getWorld().getName(), p.getName(), null, null, null, Double.toString(cost)));

                }
                else if(owner.equalsIgnoreCase(p.getName()))
                    p.sendMessage(CreeperMessenger.processMessage("trap-already-registered", p.getWorld().getName(), p.getName(), null, null, null, null));
                else
                    p.sendMessage(CreeperMessenger.processMessage("cant-remove-trap", p.getWorld().getName(), p.getName(), owner, null, null, null));
            }
            else
                p.sendMessage(CreeperMessenger.processMessage("trap-not-TNT", p.getWorld().getName(), p.getName(), null, null, null, null));
        }
        else
            p.sendMessage(CreeperMessenger.processMessage("no-permission-trap", p.getWorld().getName(), p.getName(), null, null, null, null));
    }


    private static void finePlayer(Player p, double cost) throws VaultNotDetectedException, TransactionFailedException
    {
        CreeperEconomy.playerTransaction(p, -cost);
    }


    private static boolean playerHasEnough(Player p, double cost) throws VaultNotDetectedException
    {
        return CreeperEconomy.playerHasEnough(p, cost);
    }

    private static boolean checkPermissions(Player player, boolean warning, String... nodes)
    {
        return CreeperPermissionManager.checkPermissions(player, warning, nodes);
    }


    public static void refund(Player p, double amount) throws VaultNotDetectedException, TransactionFailedException
    {
        CreeperEconomy.playerTransaction(p, amount);
    }

}
