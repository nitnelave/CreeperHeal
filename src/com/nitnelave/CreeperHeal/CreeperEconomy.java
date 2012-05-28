package com.nitnelave.CreeperHeal;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class CreeperEconomy
{

	private CreeperHeal plugin;
	private static Economy vaultEco = null;


	public CreeperEconomy(CreeperHeal instance)
	{
		plugin = instance;
		
		if(plugin.getServer().getPluginManager().getPlugin("Vault") != null)
			setupVaultEconomy();
			
	}
	
	private boolean setupVaultEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
        	vaultEco = economyProvider.getProvider();
        }

        return (vaultEco != null);
    }

	public void finePlayer(Player p, double cost) throws VaultNotDetectedException, TransactionFailedException
    {
		if(cost == 0) return;
		if(vaultEco == null)
			throw new VaultNotDetectedException();
		else
		{
			EconomyResponse r = vaultEco.withdrawPlayer(p.getName(), cost);
			if(r.transactionSuccess())
				p.sendMessage(plugin.messenger.processMessage("transaction-success", p.getWorld().getName(), p.getName(), null, null, null, Double.toString(cost)));
			else
				throw new TransactionFailedException();
		}
    }

	public boolean playerHasEnough(Player p, double cost) throws VaultNotDetectedException
    {
		if(cost == 0) return true;
		if(vaultEco == null)
			throw new VaultNotDetectedException();
		else
			return vaultEco.has(p.getName(), cost);
    }
	
	public void refundPlayer(Player p, double amount) throws VaultNotDetectedException, TransactionFailedException
	{
		if(amount == 0) return;
		if(vaultEco == null)
			throw new VaultNotDetectedException();
		else
		{
			EconomyResponse r = vaultEco.depositPlayer(p.getName(), amount);
			if(r.transactionSuccess())
				p.sendMessage(plugin.messenger.processMessage("refunded", p.getWorld().getName(), p.getName(), null, null, null, Double.toString(amount)));
			else
				throw new TransactionFailedException();
		}
	}
	

}
