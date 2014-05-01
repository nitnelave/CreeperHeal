package com.nitnelave.CreeperHeal.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * This class is used for all the outputting to the console and to players.
 * 
 * @author nitnelave
 * 
 */
public abstract class CreeperLog
{
    /*
     * Log file, for outputting warnings to a file.
     */
    private static File logFile;
    /*
     * Logger, for outputting to the console.
     */
    private final static Logger log = Logger.getLogger("Minecraft");
    /*
     * The verbosity level. Initialized at -42 as an arbitrary value, to detect
     * that it hasn't been loaded from the config yet.
     */
    private static int logLevel = -42;
    /*
     * Whether to output debug messages.
     */
    private static boolean debug = false;

    static
    {
        File warningLogFile = new File(CreeperHeal.getCHFolder() + "/log.txt");
        if (!warningLogFile.exists())
            FileUtils.createNewFile(warningLogFile);

        logFile = warningLogFile;
        debug = CreeperConfig.getBool(CfgVal.DEBUG);
    }

    /**
     * Write a message to the log file, prepended by the date.
     * 
     * @param message
     *            The message to be recorded.
     */
    public static void record(String message)
    {
        try
        {
            FileWriter fstream = new FileWriter(logFile, true);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(getDate() + message);
            out.newLine();
            out.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Output a warning to the console and to the log file.
     * 
     * @param message
     *            The message to be output.
     */
    public static void warning(String message)
    {
        log.warning("[CreeperHeal] " + message);
        record("[WARNING] " + message);
    }

    /**
     * Display an information message to the console, if the verbosity is high
     * enough, i.e. if level is lower or equals to the log level.
     * 
     * @param msg
     *            The message to be logged.
     * @param level
     *            The corresponding verbosity of the message.
     */
    public static void logInfo(String msg, int level)
    {
        if (logLevel == -42)
            logLevel = CreeperConfig.getInt(CfgVal.LOG_LEVEL);
        if (level <= logLevel)
        {
            log.info("[CreeperHeal] " + msg);
            record("[INFO] " + msg);
        }
    }

    /**
     * Output a SEVERE message to the console and record it.
     * 
     * @param message
     *            The message to be output.
     */
    public static void severe(String message)
    {
        log.log(Level.SEVERE, "[CreeperHeal] " + message);
        record("[SEVERE]" + message);
    }

    /**
     * Get a simple time formated date.
     * 
     * @return a time with the HH:mm:ss format
     */
    private static String getDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
        return dateFormat.format(new Date());
    }

    /**
     * Output a debug message, if the debug setting is true.
     * 
     * @param message
     *            The message to output.
     */
    public static void debug(String message)
    {
        if (debug)
            log.info("[DEBUG] " + message);
    }

    /**
     * Display the type and the location of a block in a formatted way. If force
     * is true, then it is a warning (as part of a warning message). Otherwise
     * it is a debug message.
     * 
     * @param block
     *            The block whose information is displayed.
     * @param force
     *            Whether it is a warning or a debug message.
     */
    public static void displayBlockLocation(Block block, boolean force)
    {
        Location loc = block.getLocation();
        String s = block.getType() + " at " + loc.getBlockX() + "; " + loc.getBlockY() + "; "
                   + loc.getBlockZ();
        if (force)
            warning(s);
        else
            debug(s);
    }

    public static void setDebug(boolean bool)
    {
        debug = bool;
        log.info("debug: " + debug);
    }

}
