package com.nitnelave.CreeperHeal.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class CreeperLog
{
	private static File file;
	private final static Logger log = Logger.getLogger("Minecraft");            //to output messages to the console/log
	private static int logLevel = -42;
	private static boolean debug = false;


	public CreeperLog(File f)
	{
		file = f;
		debug = CreeperConfig.debug;
	}

	public static void record(String s)
	{
		try{
			// Create file 
			FileWriter fstream = new FileWriter(file,true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(getDate() + s);
			out.newLine();
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			log.log(Level.SEVERE, "Error: " + e.getMessage());
		}
	}

	public static void warning(String s) {
		log.warning("[CreeperHeal] " + s);
		record("[WARNING] " + s);
	}


	public static void logInfo(String msg, int level)
	{
		if(logLevel == -42) 
			if(CreeperConfig.logLevel != -42)
				logLevel = CreeperConfig.logLevel;
		if(level<=logLevel)
		{
			log.info("[CreeperHeal] "+msg);
			record("[INFO] " + msg);
		}
	}

	public static void severe(String s) {
		log.log(Level.SEVERE, "[CreeperHeal] " + s);
		record("[SEVERE]" + s);
	}

	private static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss ");
		return dateFormat.format(new Date());
	}

	public static void debug(String string) {
		if(debug)
			log.info("[DEBUG] " + string);
	}

}
