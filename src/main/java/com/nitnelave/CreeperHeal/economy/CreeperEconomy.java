package com.nitnelave.CreeperHeal.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.nitnelave.CreeperHeal.utils.CreeperMessenger;

/**
 * Handler for economy.
 * 
 * @author nitnelave
 * 
 */
public class CreeperEconomy {

    private static Economy vaultEco = null;

    static
    {
        if (Bukkit.getServer ().getPluginManager ().getPlugin ("Vault") != null)
            setupVaultEconomy ();
    }

    private static boolean setupVaultEconomy () {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer ().getServicesManager ()
                .getRegistration (net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
        {
            vaultEco = economyProvider.getProvider ();
        }

        return (vaultEco != null);
    }

    /**
     * Make a player pay a certain amount of money, or give him the money. If
     * the amount is negative, the player will pay, and if it is positive, the
     * money will be given to him.
     * 
     * @param player
     *            The player.
     * @param amount
     *            The amount.
     * @throws VaultNotDetectedException
     *             If the Vault plugin is not active.
     * @throws TransactionFailedException
     *             If the transaction failed.
     */
    public static void playerTransaction (Player player, double amount) throws VaultNotDetectedException, TransactionFailedException {
        if (amount == 0)
            return;
        if (vaultEco == null)
            throw new VaultNotDetectedException ();
        else
        {
            EconomyResponse r;
            if (amount > 0)
                r = vaultEco.depositPlayer (player.getName (), amount);
            else
                r = vaultEco.withdrawPlayer (player.getName (), -amount);
            if (r.transactionSuccess ())
                player.sendMessage (CreeperMessenger.processMessage (amount > 0 ? "refunded" : "transaction-success", player.getWorld ().getName (),
                        player.getName (), null, null, null, Double.toString (amount)));
            else
                throw new TransactionFailedException ();
        }
    }

    /**
     * Get whether a player has enough money to pay the amount.
     * 
     * @param player
     *            The player.
     * @param amount
     *            The amount to check.
     * @return Whether the player has enough.
     * @throws VaultNotDetectedException
     *             If the Vault plugin is not active.
     */
    public static boolean playerHasEnough (Player player, double amount) throws VaultNotDetectedException {
        if (amount == 0)
            return true;
        if (vaultEco == null)
            throw new VaultNotDetectedException ();
        else
            return vaultEco.has (player.getName (), amount);
    }

}