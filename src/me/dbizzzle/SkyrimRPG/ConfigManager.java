package me.dbizzzle.SkyrimRPG;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import me.dbizzzle.SkyrimRPG.Skill.Skill;

import org.bukkit.World;

public class ConfigManager 
{
	private ConfigKey<Integer> skillLevelCap = new ConfigKey<Integer>("skillLevelCap", 0);
	private ConfigKey<Boolean> useSpellBooks = new ConfigKey<Boolean>("useSpellBooks", true);
	public static boolean enableLockpicking = true;
	public static boolean enablePickpocketing = true;
	public static boolean enablePickpocketingChance = false;
	public static boolean enableSneakMessage = false;
	public static boolean enableLpCd = true;
	public static boolean enablePpCd = true;
	public static int wand = 0;
	public static int PickpocketingCooldown = 10;
	public static int LockpickingCooldown = 10;
	public boolean debug = false;
	public static boolean announceDevBuild = true;
	public static ArrayList<World> disabledWorlds = new ArrayList<World>();
	public static ArrayList<Skill> disabledSkills = new ArrayList<Skill>();
	private ConfigKey<Boolean> spellPerms = new ConfigKey<Boolean>("useSpellPermissions", false);
	private SkyrimRPG a = null;
	public ConfigManager(SkyrimRPG s)
	{
		a = s;
	}
	public Object getValue(String key)
	{
		if(key.equalsIgnoreCase(spellPerms.getKey()))return spellPerms.getValue();
		else if(key.equalsIgnoreCase(skillLevelCap.getKey()))return skillLevelCap.getValue();
		else if(key.equalsIgnoreCase(useSpellBooks.getKey()))return useSpellBooks.getValue();
		return null;
	}
	public void loadConfig()
	{
		if(!a.checkFiles())refreshConfig();
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
				if(l.isEmpty())continue;
				String[] tokens = l.split("[:]", 2);
				if(tokens.length != 2)continue;
				if(tokens[0].equalsIgnoreCase("usespellbooks"))useSpellBooks.data = tokens[1].replaceAll(" ", "").equalsIgnoreCase("true");
				else if(tokens[0].equalsIgnoreCase("enablelockpicking"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enableLockpicking = false;
					else enableLockpicking = true;
				}
				else if(tokens[0].equalsIgnoreCase("enablepickpocketing"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enablePickpocketing = false;
					else enablePickpocketing = true;
				}
				else if(tokens[0].equalsIgnoreCase("enablelockpickingcooldown"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enableLpCd = false;
					else enableLpCd = true;
				}
				else if(tokens[0].equalsIgnoreCase("announcedevbuild"))
				{
					announceDevBuild = tokens[1].replaceAll(" ", "").equalsIgnoreCase("true");
				}
				else if(tokens[0].equalsIgnoreCase("enablepickpocketingcooldown"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enablePpCd = false;
					else enablePpCd = true;
				}
				else if(tokens[0].equalsIgnoreCase("enablepickpocketingchance"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enablePickpocketingChance = false;
					else enablePickpocketingChance = true;
				}
				else if(tokens[0].equalsIgnoreCase("pickpocketingcooldown"))
				{
					try{PickpocketingCooldown = Integer.parseInt(tokens[1].replaceAll(" ", ""));}catch(NumberFormatException nfe){PickpocketingCooldown = 0;}
				}
				else if(tokens[0].equalsIgnoreCase("lockpickingcooldown"))
				{
					try{LockpickingCooldown = Integer.parseInt(tokens[1].replaceAll(" ", ""));}catch(NumberFormatException nfe){LockpickingCooldown = 0;}
				}
				else if(tokens[0].equalsIgnoreCase("enablesneakmessage"))
				{
					if(tokens[1].replaceAll(" ", "").equalsIgnoreCase("false"))enableSneakMessage = false;
					else enableSneakMessage = true;
				}
				else if(tokens[0].equalsIgnoreCase("debugmode"))debug = tokens[1].replaceAll(" ", "").equalsIgnoreCase("true");
				else if(tokens[0].equalsIgnoreCase("skilllevelcap"))
				{
					try{skillLevelCap.data = Integer.parseInt(tokens[1].replaceAll(" ", ""));}catch(NumberFormatException nfe){skillLevelCap.data = 0;}
				}
				else if(tokens[0].equalsIgnoreCase("castingtool"))
				{
					try{wand = Integer.parseInt(tokens[1].replaceAll(" ", ""));}catch(NumberFormatException nfe){wand = 0;}
				}
				else if(tokens[0].equalsIgnoreCase("disabledworlds"))
				{
					String[] a = tokens[1].split("[,]+");
					for(String x:a)
					{
						World w = this.a.getServer().getWorld(x);
						if(w != null && !disabledWorlds.contains(w))disabledWorlds.add(w);
					}
				}
				else if(tokens[0].equalsIgnoreCase("disabledskills"))
				{
					String[] a = tokens[1].split("[,]+");
					for(String x:a)
					{
						Skill s;
						try{s = Skill.valueOf(x.toUpperCase().replaceAll(" ", ""));}catch(IllegalArgumentException iae){continue;}
						if(!disabledSkills.contains(s))disabledSkills.add(s);
					}
				}
				else if(tokens[0].equalsIgnoreCase("useSpellPermissions"))spellPerms.data = tokens[1].replaceAll(" ", "").equalsIgnoreCase("true");
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
			bw.write("#SkyrimRPG configuration file generated by version " + a.getDescription().getVersion());
			bw.newLine();
			bw.write("#General Settings");
			bw.newLine();
			bw.write("#Enable this if you want to use spellbooks");
			bw.newLine();
			bw.write("useSpellbooks: " + (useSpellBooks.data ? "true" : "false"));
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
			bw.write("#Lockpicking and Pickpocketing cooldowns(in seconds), don't work if disabled");
			bw.newLine();
			bw.write("PickpocketingCooldown: " + PickpocketingCooldown);
			bw.newLine();
			bw.write("LockpickingCooldown: " + LockpickingCooldown);
			bw.newLine();
			bw.write("#Enable/disable pickpocketing and lockpicking cooldowns");
			bw.newLine();
			bw.write("enablePickpocketingCooldown: " + (enablePpCd ? "true" : "false"));
			bw.newLine();
			bw.write("enableLockpickingCooldown: " + (enableLpCd ? "true" : "false"));
			bw.newLine();
			bw.write("#Item id of the casting tool(the item used for bindspell, default hands)");
			bw.newLine();
			bw.write("castingTool: " + wand);
			bw.newLine();
			bw.write("#Enable/disable those annoying sneak messages");
			bw.newLine();
			bw.write("enableSneakMessage: " + (enableSneakMessage ? "true" : "false"));
			bw.newLine();
			bw.write("#Enable/disable debug mode(Spams your console with useless info, not to be used unless you got errors)");
			bw.newLine();
			bw.write("debugMode: " + (debug ? "true" : "false"));
			bw.newLine();
			bw.write("#The maximum level of all skills, put 0 for infinite");
			bw.newLine();
			bw.write("skillLevelCap: 0");
			bw.newLine();
			bw.write("#Announce in the console and to players with skyrimrpg.newversion when a new dev build is out");
			bw.newLine();
			bw.write("announceDevBuild: true");
			bw.newLine();
			bw.write("#If a player needs \"skyrimrpg.spells.<spellname>\" or \"skyrimrpg.spells.*\" in order to cast a spell");
			bw.newLine();
			bw.write("useSpellPermissions: false");
			bw.newLine();
			bw.newLine();
			bw.write("#\"List\" settings");
			bw.newLine();
			bw.write("#Worlds this plugin is disabled on (Comma seperated list)");
			bw.newLine();
			String a = "";
			for(World w:disabledWorlds)
			{
				if(a.isEmpty())a = w.getName();
				else a = a + "," + w.getName();
			}
			bw.write("disabledWorlds: " + a);
			bw.newLine();
			a = "";
			for(Skill s:disabledSkills)
			{
				if(a.isEmpty())a = s.toString();
				else a = a + "," + s.toString();
			}
			bw.write("disabledSkills: " + a);
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
	private class ConfigKey<T>
	{
		private String key;
		private T data;
		public ConfigKey(String key, T data)
		{
			this.key = key;
			this.data = data;
		}
		public String getKey()
		{
			return key;
		}
		public T getValue()
		{
			return data;
		}
	}
}
