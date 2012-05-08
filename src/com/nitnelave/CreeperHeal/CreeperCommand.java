package com.nitnelave.CreeperHeal;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CreeperCommand extends Command
{
    protected final CommandExecutor owner;

	public CreeperCommand(String[] aliases, String desc, String usage, CommandExecutor owner)
    {
        super(aliases[0], desc, usage, Arrays.asList(aliases));
        this.owner = owner;
    }

	@Override
    public boolean execute(CommandSender sender, String label, String[] args)
    {
	    return owner.onCommand(sender, this, label, args);
    }

}
