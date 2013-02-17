package com.nitnelave.CreeperHeal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer.WarningCause;

/**
 * A class to handle custom messages to players.
 * 
 * @author nitnelave
 * 
 */
public class CreeperMessenger {
    /*
     * The plugin folder, to create the messages.properties file.
     */
    private static File pluginFolder;
    private static JavaPlugin plugin;
    /*
     * The properties generated from reading the messages file.
     */
    private static Properties prop;
    /*
     * Variables to be replaced in the message.
     */
    private final static String[] variables = {"WORLD", "PLAYER", "TARGET", "MOB", "BLOCK"};

    private static List<CreeperPlayer> warnList = Collections.synchronizedList (new LinkedList<CreeperPlayer> ());

    /**
     * Constructor. Loads the messages from the file.
     * 
     * @param file
     * @param plugin
     */
    public CreeperMessenger (JavaPlugin plugin) {
        CreeperMessenger.plugin = plugin;
        pluginFolder = plugin.getDataFolder ();
        load ();
        populateWarnList ();
    }

    /*
     * Load the messages from the file.
     */
    private static void load () {
        prop = new Properties ();
        File messageFile = new File (pluginFolder.getPath () + "/messages.properties");
        try
        {
            if (!messageFile.exists ())
                createNewFile (messageFile);

            FileInputStream input = new FileInputStream (messageFile);
            prop.load (input);
            input.close ();
        } catch (FileNotFoundException e)
        {
            CreeperLog.warning ("[CreeperHeal] Failed to read file: messages.properties");
            e.printStackTrace ();
        } catch (IOException e)
        {
            CreeperLog.warning ("[CreeperHeal] Failed to read file: messages.properties");
            e.printStackTrace ();
        }
    }

    /*
     * In case of missing file, create it and load the default file.
     */
    private static void createNewFile (File file) {
        try
        {
            file.createNewFile ();
            InputStream templateIn = plugin.getResource ("messages.properties");
            OutputStream outStream = new FileOutputStream (file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = templateIn.read (bytes)) != -1)
                outStream.write (bytes, 0, read);

            templateIn.close ();
            outStream.flush ();
            outStream.close ();
            CreeperLog.logInfo ("[CreeperHeal] Default config created", 1);

        } catch (Exception e)
        {
            CreeperLog.warning ("[CreeperHeal] Failed to create file: messages.properties");
            e.printStackTrace ();
        }
    }

    /*
     * Replace the color keywords in the messages by the chat codes equivalents.
     */
    private static String colorToChat (String message) {
        for (ChatColor c : ChatColor.values ())
            message = message.replaceAll ("\\{" + c.name () + "\\}", c.toString ());
        return message;
    }

    /**
     * Create a formatted string ready for sending to a player or to the
     * console. All the instances of formatting keywords are replaced by the
     * values provided if specified.
     * 
     * @param type
     *            The message type, corresponding to the property key in the
     *            file.
     * @param values
     *            Values to replace the formatting keywords.
     * @return A formatted string for chat.
     */
    public static String processMessage (String type, String... values) {
        String message = prop.getProperty (type);
        try
        {
            message = colorToChat (message);
        } catch (NullPointerException e)
        {
            CreeperLog.warning ("Missing message property : " + type);
        }
        try
        {
            for (int i = 0; i < variables.length; i++)
                if (values[i] != null)
                    message = message.replaceAll ("\\{" + variables[i] + "\\}", values[i]);
        } catch (NullPointerException e)
        {
            CreeperLog.warning ("[CreeperHeal] Wrong variable used in message " + type);
        }
        return message;
    }

    /**
     * Get the message that should be sent to a player/console after an event,
     * as a formatted string.
     * 
     * @param cause
     *            The reason for the message.
     * @param offender
     *            The name of the offender.
     * @param world
     *            The world in which the offense happened.
     * @param blocked
     *            Whether the action was blocked.
     * @param data
     *            The data describing the offense.
     * @param player
     *            Whether the message is meant for a player or for the admin.
     * @return A formatted message.
     */
    public static String getMessage (WarningCause cause, String offender, String world, boolean blocked, String data, boolean player) {
        String prefix = blocked ? "block-" : "warn-";
        String suffix = blocked ? (player ? "-player" : "-admin") : "";
        String message = null;
        switch (cause)
        {
            case LAVA:
                message = processMessage (prefix + "lava" + suffix, world, offender, null, null, null);
                break;
            case FIRE:
                message = processMessage (prefix + "flint-and-steel" + suffix, world, offender, null, null, null);
                break;
            case TNT:
                message = processMessage (prefix + "TNT" + suffix, world, offender, null, null, null);
                break;
            case BLACKLIST:
                message = processMessage (prefix + "place-blacklist" + suffix, world, offender, null, null, data);
                break;
            case SPAWN_EGG:
                message = processMessage (prefix + "spawn-eggs" + suffix, world, offender, null, data, null);
                break;
            case PVP:
                message = processMessage (prefix + "pvp" + suffix, world, offender, data, null, null);
                break;
        }
        return message;
    }

    /**
     * Initialize the warn list with all concerned players.
     */
    private static void populateWarnList () {
        warnList.clear ();
        for (Player p : Bukkit.getServer ().getOnlinePlayers ())
            registerPlayer (new CreeperPlayer (p));
    }

    /**
     * Send a warning to a player, and alert all those concerned by the message.
     * 
     * @param cause
     *            The reason for the message.
     * @param offender
     *            The offending player.
     * @param blocked
     *            Whether the action was blocked.
     * @param material
     *            Some info about the message.
     */
    public static void warn (WarningCause cause, Player offender, boolean blocked, String material) {
        String message = CreeperMessenger.getMessage (cause, offender.getName (), offender.getWorld ().getName (), blocked, material, false);
        SimpleDateFormat f = new SimpleDateFormat ("HH:mm:ss");
        if (CreeperConfig.logWarnings)
            CreeperLog.record ("[" + f.format (new Date ()) + "] " + ChatColor.stripColor (message));
        message = ChatColor.RED + message;
        offender.sendMessage (CreeperMessenger.getMessage (cause, offender.getName (), offender.getWorld ().getName (), blocked, material, true));
        for (CreeperPlayer cp : warnList)
            cp.warnPlayer (cp.getPlayer (), cause, message);

    }


    /**
     * Remove a player from the list of players that are warned of an
     * infraction.
     * 
     * @param player
     *            The player to remove.
     */
    public static void removeFromWarnList (CreeperPlayer player) {
        warnList.remove (player);
    }

    /**
     * Check if the player can be warned about infractions, and register him.
     * 
     * @param player
     *            The player to add.
     */
    public static void registerPlayer (CreeperPlayer player) {
        if (player.hasWarnings ())
            warnList.add (player);

    }

}
