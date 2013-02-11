package com.nitnelave.CreeperHeal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.bukkit.ChatColor;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.utils.CreeperPlayer.WarningCause;

public class CreeperMessenger
{
	private static File pluginFolder;
	private static CreeperHeal plugin;
	private static Properties prop;
	private final static String[] variables = {"WORLD", "PLAYER", "TARGET", "MOB", "BLOCK", "AMOUNT"};


	public CreeperMessenger(File file, CreeperHeal plugin)
	{
		CreeperMessenger.plugin = plugin;
		pluginFolder = file;
		load();
	}

	private static void load()
	{
		prop = new Properties();
		File messageFile = new File(pluginFolder.getPath() + "/messages.properties");
		try
		{
			if(!messageFile.exists())
				createNewFile(messageFile);

			FileInputStream input = new FileInputStream(messageFile);
			prop.load(input);
			input.close();
		}
		catch (FileNotFoundException e)
		{
			CreeperLog.warning("[CreeperHeal] Failed to read file: messages.properties");
			CreeperLog.warning(e.getMessage());
		}
		catch (IOException e)
		{
			CreeperLog.warning("[CreeperHeal] Failed to read file: messages.properties");
			CreeperLog.warning(e.getMessage());
		}
	}



	private static void createNewFile(File file)
	{
		try {
			file.createNewFile();
			InputStream templateIn = plugin.getResource("messages.properties");
			OutputStream outStream = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = templateIn.read(bytes)) != -1) {
				outStream.write(bytes, 0, read);
			}

			templateIn.close();
			outStream.flush();
			outStream.close();
			CreeperLog.logInfo("[CreeperHeal] Default config created", 1);

		} catch (Exception e) {
			CreeperLog.warning("[CreeperHeal] Failed to create file: messages.properties");
			CreeperLog.warning(e.getMessage());
		}	    
	}


	private static String colorToChat(String message)
	{
		for(ChatColor c : ChatColor.values())
			message = message.replaceAll("\\{" + c.name() + "\\}", c.toString());
		return message;
	}


	public static String processMessage(String m, String... values)
	{
		String message = prop.getProperty(m);
		try {
			message = colorToChat(message);
		}catch (NullPointerException e) {
			CreeperLog.warning("Missing message property : " + m);
		}
		try{
			for(int i = 0; i < variables.length; i++)
				if(values[i] != null)
					message = message.replaceAll("\\{" + variables[i] + "\\}", values[i]);
		} catch(NullPointerException e) {
			CreeperLog.warning("[CreeperHeal] Wrong variable used in message " + m);
		}
		return message;
	}

	public static String getMessage(WarningCause cause, String offender, String world,
			boolean blocked, String data, boolean player)
	{
		switch(cause)
		{
		case LAVA:
			return processMessage((blocked?"block":"warn") + "-lava" + (blocked?(player?"-player":"-admin"):""), world, offender, null, null, null, null);
		case FIRE:
			return processMessage((blocked?"block":"warn") + "-flint-and-steel" + (blocked?(player?"-player":"-admin"):""), world, offender, null, null, null, null);
		case TNT:
			return processMessage((blocked?"block":"warn") + "-TNT" + (blocked?(player?"-player":"-admin"):""), world, offender, null, null, null, null);
		case BLACKLIST:
			return processMessage((blocked?"block":"warn") + "-place-blacklist" + (blocked?(player?"-player":"-admin"):""), world, offender, null, null, data, null);
		case SPAWN_EGG:
			return processMessage((blocked?"block":"warn") + "-spawn-eggs" + (blocked?(player?"-player":"-admin"):""), world, offender, null, data, null, null);
		case PVP:
			return processMessage((blocked?"block":"warn") + "-pvp" + (blocked?(player?"-player":"-admin"):""), world, offender, data, null, null, null);
		}
		return null;
	}

}
