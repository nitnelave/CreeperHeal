package com.nitnelave.CreeperHeal.utils;

import org.bukkit.entity.Player;

/**
 * Utility class for player permissions and warnings.
 * 
 * @author nitnelave
 * 
 */
public class CreeperPlayer
{

    private final Player player;
    private boolean lava, tnt, fire, blacklist, spawnEggs, pvp;

    /**
     * The different infractions that are recorded for a player.
     */
    public enum WarningCause
    {
        LAVA, TNT, FIRE, BLACKLIST, SPAWN_EGG, PVP
    }

    /**
     * Constructor.
     * 
     * @param player
     *            The player represented by this class.
     */
    public CreeperPlayer(Player player)
    {
        this.player = player;
        loadPermissions();
    }

    /*
     * Load the player's permissions.
     */
    private void loadPermissions()
    {
        lava = CreeperPermissionManager.checkPermissions(player, true, "grief.warn.*", "grief.warn.lava");
        tnt = CreeperPermissionManager.checkPermissions(player, true, "grief.warn.*", "grief.warn.tnt");
        fire = CreeperPermissionManager.checkPermissions(player, true, "grief.warn.*", "grief.warn.fire");
        blacklist = CreeperPermissionManager.checkPermissions(player, true, "grief.warn.*", "grief.warn.blacklist");
        spawnEggs = CreeperPermissionManager.checkPermissions(player, true, "grief.warn.*", "grief.warn.spawnEggs");
        pvp = CreeperPermissionManager.checkPermissions(player, true, "grief.warn.*", "grief.warn.pvp");
    }

    /**
     * Get the player represented.
     * 
     * @return The player represented.
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Send a warning message to the player if appropriate.
     * 
     * @param player
     *            The recipient.
     * @param cause
     *            The reason for the message.
     * @param message
     *            The message send.
     */
    public void warnPlayer(Player player, WarningCause cause, String message)
    {
        switch (cause)
        {
        case BLACKLIST:
            if (blacklist)
                player.sendMessage(message);
            break;
        case TNT:
            if (tnt)
                player.sendMessage(message);
            break;
        case FIRE:
            if (fire)
                player.sendMessage(message);
            break;
        case LAVA:
            if (lava)
                player.sendMessage(message);
            break;
        case SPAWN_EGG:
            if (spawnEggs)
                player.sendMessage(message);
            break;
        case PVP:
            if (pvp)
                player.sendMessage(message);
            break;
        }
    }

    /**
     * Get whether the player receives any kind of warning.
     * 
     * @return True if the player has permission to receive a warning.
     */
    public boolean hasWarnings()
    {
        return lava || tnt || fire || blacklist || spawnEggs || pvp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((player == null) ? 0 : player.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof CreeperPlayer))
            return false;
        CreeperPlayer other = (CreeperPlayer) obj;
        return player.getName().equals(other.player.getName());
    }

}
