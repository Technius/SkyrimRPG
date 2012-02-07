package me.dbizzzle.SkyrimRPG;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigManager 
{
	public static boolean useSpellbooks = true;
	public static boolean enableLockpicking = true;
	public static boolean enablePickpocketing = true;
	public static boolean enablePickpocketingChance = false;
	SkyrimRPG a = null;
	public ConfigManager(SkyrimRPG s)
	{
		a = s;
	}
	public void loadConfig()
	{
		if(!a.checkFiles())a.createFiles();
		File file = new File(a.getDataFolder().getPath());
		if(!file.exists())file.mkdir();
		File config = new File(file.getPath() + File.separator + "config.txt");
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(config)));
			String l;
			while((l = br.readLine()) != null)
			{
				if(l.startsWith("#"))continue;
				String[] tokens = l.split("[:]", 2);
				if(tokens.length != 2)continue;
				if(tokens[0].equalsIgnoreCase("usespellbooks"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))useSpellbooks = false;
					else useSpellbooks = true;
				}
				if(tokens[0].equalsIgnoreCase("enablelockpicking"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enableLockpicking = false;
					else enableLockpicking = true;
				}
				if(tokens[0].equalsIgnoreCase("enablepickpocketing"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enablePickpocketing = false;
					else enablePickpocketing = true;
				}
				if(tokens[0].equalsIgnoreCase("enablepickpocketingchance"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enablePickpocketingChance = false;
					else enablePickpocketingChance = true;
				}
			}
			br.close();
		}
		catch(IOException ioe)
		{
			a.log.warning("[SkyrimRPG]Could not load config, using default values.");
		}
	}
	public boolean refreshConfig()
	{
		if(!a.checkFiles())a.createFiles();
		File file = new File(a.getDataFolder().getPath());
		if(!file.exists())file.mkdir();
		File config = new File(file.getPath() + File.separator + "config.txt");
		try
		{
			if(!config.exists())
			{
				FileOutputStream fos = new FileOutputStream(config);
				fos.flush();
				fos.close();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(config));
			bw.write("#SkyrimRPG configuration file");
			bw.newLine();
			bw.write("#Enable this if you want to use spellbooks");
			bw.newLine();
			bw.write("useSpellbooks: " + (useSpellbooks ? "true" : "false"));
			bw.newLine();
			bw.write("#Disable these if you don't like thievery");
			bw.newLine();
			bw.write("enableLockpicking: " + (enableLockpicking ? "true" : "false"));
			bw.newLine();
			bw.write("enablePickpocketing: " + (enablePickpocketing ? "true" : "false"));
			bw.newLine();
			bw.write("#If you want to allow pickpocketing to fail(inventory is not opened)");
			bw.newLine();
			bw.write("enablePickpocketingChance: " + (enablePickpocketingChance ? "true" : "false"));
			bw.newLine();
			bw.flush();
			bw.close();
			return true;
		}
		catch(IOException ioe)
		{
			return false;
		}
	}
}
