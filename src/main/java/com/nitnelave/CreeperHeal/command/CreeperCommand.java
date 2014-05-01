package com.nitnelave.CreeperHeal.command;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Custom command.
 * 
 * @author nitnelave
 * 
 */
public class CreeperCommand extends Command
{
    private final CommandExecutor owner;

    /**
     * Constructor. Sets the CommandExecutor associated with CreeperHeal.
     * 
     * @param aliases
     *            The list of aliases to the command.
     * @param desc
     *            The description of the command.
     * @param usage
     *            The usage for the command.
     * @param owner
     *            The CommandExecutor associated.
     */
    public CreeperCommand(String[] aliases, String desc, String usage, CommandExecutor owner)
    {
        super(aliases[0], desc, usage, Arrays.asList(aliases));
        this.owner = owner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.bukkit.command.Command#execute(org.bukkit.command.CommandSender,
     * java.lang.String, java.lang.String[])
     */
    @Override
    public boolean execute(CommandSender sender, String label, String[] args)
    {
        return owner.onCommand(sender, this, label, args);
    }

}
