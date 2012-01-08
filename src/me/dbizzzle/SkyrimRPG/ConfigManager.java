package me.dbizzzle.SkyrimRPG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigManager 
{
	public static boolean useSpellbooks = true;
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
			}
			br.close();
		}
		catch(IOException ioe)
		{
			a.log.warning("[SkyrimRPG]Could not load config, using default values.");
		}
	}
}
