package me.dbizzzle.SkyrimRPG.Skill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;
import org.bukkit.entity.Player;

public class SkillManager 
{
	public static HashMap<Player, HashMap<String, Integer>> skills = new HashMap<Player, HashMap<String, Integer>>();
	public static HashMap<Player, HashMap<String, Integer>> progress = new HashMap<Player, HashMap<String, Integer>>();
	public static HashMap<Player, Integer> level = new HashMap<Player,Integer>();
	SkyrimRPG p = null;
	public static HashMap<String, Integer> getSkills (Player player)
	{
		return skills.get(player);
	}
	public static int getProgress(String skill, Player player)
	{
		return progress.get(player).get(skill).intValue();
	}
	public static int getSkillLevel(String skill, Player player)
	{
		return skills.get(player).get(skill).intValue();
	}
	public void incrementLevel(String skill, Player player)
	{
		int l = skills.get(player).get(skill).intValue() + 1;
		skills.get(player).put(skill, Integer.valueOf(l));
		player.sendMessage(skill + " increased to level " + l);
	}
	public void loadSkills(Player player)
	{
		File plugindir = new File(p.getDataFolder().getPath());
		File players = new File(plugindir.getPath() + File.separator + "Players");
		File save = new File(players.getPath() + File.separator + player.getName() + ".txt");
		if(!plugindir.exists())plugindir.mkdir();
		if(!players.exists())players.mkdir();
		if(!save.exists())
		{
			resetSkills(player);
			return;
		}
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(save)));
			String l;
			HashMap<String, Integer> pr = new HashMap<String, Integer>();
			HashMap<String, Integer> sk = new HashMap<String, Integer>();
			while((l=br.readLine())!= null)
			{
				if(l.startsWith("#"))continue;
				String delim = "[:]";
				if(l.startsWith("Archery"))
				{
					String tokens[] = l.split(delim, 2);
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("Archery"))continue;
					delim = "[ ]+";
					String[] spaced = tokens[1].split(delim);
					String x = "";
					for(String p:spaced)x=x+p;
					String[] sep = x.split("[,]",2);
					if(sep.length != 2) continue;
					int level = 1;
					int progress = 0;
					try
					{
						level = Integer.parseInt(sep[0]);
						progress = Integer.parseInt(sep[1]);
					}
					catch(NumberFormatException nfe)
					{
						level = 1;
						progress = 0;
					}
					pr.put("Archery", Integer.valueOf(progress));
					sk.put("Archery", Integer.valueOf(level));
				}
				if(l.startsWith("Swordsmanship"))
				{
					String tokens[] = l.split(delim, 2);
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("Swordsmanship"))continue;
					delim = "[ ]+";
					String[] spaced = tokens[1].split(delim);
					String x = "";
					for(String p:spaced)x=x+p;
					String[] sep = x.split("[,]",2);
					if(sep.length != 2) continue;
					int level = 1;
					int progress = 0;
					try
					{
						level = Integer.parseInt(sep[0]);
						progress = Integer.parseInt(sep[1]);
					}
					catch(NumberFormatException nfe)
					{
						level = 1;
						progress = 0;
					}
					pr.put("Swordsmanship", Integer.valueOf(progress));
					sk.put("Swordsmanship", Integer.valueOf(level));
				}
				if(l.startsWith("PickPocket"))
				{
					String tokens[] = l.split(delim, 2);
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("PickPocket"))continue;
					delim = "[ ]+";
					String[] spaced = tokens[1].split(delim);
					String x = "";
					for(String p:spaced)x=x+p;
					String[] sep = x.split("[,]",2);
					if(sep.length != 2) continue;
					int level = 1;
					int progress = 0;
					try
					{
						level = Integer.parseInt(sep[0]);
						progress = Integer.parseInt(sep[1]);
					}
					catch(NumberFormatException nfe)
					{
						level = 1;
						progress = 0;
					}
					pr.put("PickPocket", Integer.valueOf(progress));
					sk.put("PickPocket", Integer.valueOf(level));
				}
				skills.put(player, sk);
				progress.put(player, pr);
			}
			br.close();
		}
		catch(IOException ioe)
		{
			resetSkills(player);
		}
	}
	public void setPlugin(SkyrimRPG plugin)
	{
		p = plugin;
	}
	public void saveSkills(Player player)
	{
		File plugindir = new File(p.getDataFolder().getPath());
		File players = new File(plugindir.getPath() + File.separator + "Players");
		File save = new File(players.getPath() + File.separator + player.getName() + ".txt");
		if(!plugindir.exists())plugindir.mkdir();
		if(!players.exists())players.mkdir();
		try
		{
			if(!save.exists())
			{
				FileOutputStream fos = new FileOutputStream(save);
				fos.flush();
				fos.close();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(save));
			bw.write("#Skill: level, progress");
			bw.newLine();
			bw.write("Archery: " + getSkillLevel("Archery", player) + "," + getProgress("Archery",player));
			bw.newLine();
			bw.write("Swordsmanship: " + getSkillLevel("Swordsmanship", player) + "," + getProgress("Swordsmanship",player));
			bw.newLine();
			bw.write("PickPocket: " + getSkillLevel("PickPocket", player) + "," + getProgress("PickPocket", player));
			bw.newLine();
			bw.flush();
			bw.close();
		}
		catch(IOException ioe)
		{
			p.log.severe("[SkyrimRPG] Could not save player data!");
		}
	}
	public void resetSkills(Player player)
	{
		HashMap<String, Integer> sk = new HashMap<String, Integer>();
		sk.put("PickPocket", Integer.valueOf(1));
		sk.put("Archery", Integer.valueOf(1));
		sk.put("Swordsmanship", Integer.valueOf(1));
		sk.put("Lockpicking", Integer.valueOf(1));
		sk.put("Destruction", Integer.valueOf(1));
		HashMap<String, Integer> pr = new HashMap<String,Integer>();
		pr.put("Archery", Integer.valueOf(0));
		pr.put("PickPocket", Integer.valueOf(0));
		pr.put("Swordsmanship", Integer.valueOf(0));
		pr.put("Lockpicking", Integer.valueOf(0));
		pr.put("Destruction", Integer.valueOf(0));
		skills.put(player, sk);
		progress.put(player, pr);
	}
	/**
	 * Calculates if the player levels up or not
	 * @param player The player
	 * @param skill The skill that the player is "practicing"
	 * @return True if the player is leveling up, otherwise false
	 */
	public boolean processExperience(Player player, String skill)
	{
		if(skill.equalsIgnoreCase("Archery"))
		{
			int alevel = SkillManager.getSkillLevel("Archery", player);
			int pro = SkillManager.getProgress("Archery", player);
			int t = 5;
			for(int i = 1;i<alevel;i++)
			{
				t=t+2;
			}
			if(pro >= t)
			{
				return true;
			}
			return false;
		}
		else if(skill.equalsIgnoreCase("Swordsmanship"))
		{
			int alevel = SkillManager.getSkillLevel("Swordsmanship", player);
			int pro = SkillManager.getProgress("Swordsmanship", player);
			int t = 5;
			for(int i = 1;i<alevel;i++)
			{
				t=t+2;
			}
			if(pro >= t)
			{
				return true;
			}
			return false;
		} 
		else if (skill.equalsIgnoreCase("PickPocket"))
		{
			int alevel = SkillManager.getSkillLevel("PickPocket", player);
			int pro = SkillManager.getProgress("PickPocket", player);
			int t = 5;
			
			for(int i = 1;i<alevel;i++) {
				t=t+2;
			}
			
			if(pro >= t) {
				return true;
			}
			return false;
		}
		else if (skill.equalsIgnoreCase("Destruction"))
		{
			int alevel = SkillManager.getSkillLevel("Destruction", player);
			int pro = SkillManager.getProgress("Destruction", player);
			int t = 5;
			
			for(int i = 1;i<alevel;i++) {
				t=t+2;
			}
			
			if(pro >= t) {
				return true;
			}
			return false;
		}
		return false;
	}
}
