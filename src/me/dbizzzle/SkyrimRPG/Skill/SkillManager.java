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
import java.util.logging.Logger;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;
import me.dbizzzle.SkyrimRPG.SpellManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SkillManager 
{
	Logger log = Logger.getLogger("Minecraft");
	public static HashMap<Player, HashMap<Skill, Integer>> skills = new HashMap<Player, HashMap<Skill, Integer>>();
	public static HashMap<Player, HashMap<Skill, Integer>> progress = new HashMap<Player, HashMap<Skill, Integer>>();
	public static HashMap<Player, Integer> level = new HashMap<Player,Integer>();
	SkyrimRPG p = null;
	public SkillManager()
	{
		
	}
	public SkillManager(SkyrimRPG a)
	{
		p = a;
	}
	public static int calculateLevel(Player player)
	{
		int lvl = 0;
		int tot = 0;
		for(Skill s:skills.get(player).keySet())
		{
			tot = tot + skills.get(player).get(s).intValue();
			while(tot >= 8)
			{
				lvl = lvl + 1;
				tot = tot - 8;
			}
		}
		if(lvl != level.get(player).intValue())
		{
			level.put(player, lvl);
			player.sendMessage(ChatColor.GOLD + "You are now level " + level.get(player).intValue());
		}
		return level.get(player).intValue();
	}
	public static boolean isLevelingUp(Player player)
	{
		int tot = 0;
		int cl = level.get(player).intValue();
		for(Skill s:skills.get(player).keySet())
		{
			tot = tot + skills.get(player).get(s).intValue();
		}
		if(tot > cl * 7)return true;
		return false;
	}
	public static HashMap<Skill, Integer> getSkills (Player player)
	{
		return skills.get(player);
	}
	public static int getProgress(Skill skill, Player player)
	{
		return progress.get(player).get(skill).intValue();
	}
	public static int getSkillLevel(Skill skill, Player player)
	{
		return skills.get(player).get(skill).intValue();
	}
	public void setLevel(Skill skill, Player player, int level)
	{
		skills.get(player).put(skill, Integer.valueOf(level));
	}
	public void incrementLevel(Skill skill, Player player)
	{
		int l = skills.get(player).get(skill).intValue() + 1;
		skills.get(player).put(skill, Integer.valueOf(l));
		player.sendMessage(skill.getName() + " increased to level " + l);
	}
	public void loadData(Player player)
	{
		loadSkills(player);
		SpellManager sm = new SpellManager(p);
		sm.loadSpells(player);
		PerkManager pm = new PerkManager(p);
		pm.loadPerks(player);
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
			HashMap<Skill, Integer> pr = new HashMap<Skill, Integer>();
			HashMap<Skill, Integer> sk = new HashMap<Skill, Integer>();
			int tl = 1;
			int m = 0;
			int pp = 0;
			while((l=br.readLine())!= null)
			{
				if(l.startsWith("#"))continue;
				String delim = "[:]";
				String tokens[] = l.split(delim, 2);
				if(tokens.length != 2) continue;
				if(l.startsWith("Archery"))
				{
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("Archery"))continue;
					String x = tokens[1].replaceAll(" ", "");
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
					pr.put(Skill.ARCHERY, Integer.valueOf(progress));
					sk.put(Skill.ARCHERY, Integer.valueOf(level));
				}
				if(l.startsWith("Swordsmanship"))
				{
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
					pr.put(Skill.SWORDSMANSHIP, Integer.valueOf(progress));
					sk.put(Skill.SWORDSMANSHIP, Integer.valueOf(level));
				}
				if(l.startsWith("PickPocket"))
				{
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
					pr.put(Skill.PICKPOCKETING, Integer.valueOf(progress));
					sk.put(Skill.PICKPOCKETING, Integer.valueOf(level));
				}
				if(l.startsWith("Destruction"))
				{
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("Destruction"))continue;
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
					pr.put(Skill.DESTRUCTION, Integer.valueOf(progress));
					sk.put(Skill.DESTRUCTION, Integer.valueOf(level));
				}
				if(l.startsWith("Conjuration"))
				{
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("Conjuration"))continue;
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
					pr.put(Skill.CONJURATION, Integer.valueOf(progress));
					sk.put(Skill.CONJURATION, Integer.valueOf(level));
				}
				if(l.startsWith("Lockpicking"))
				{
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("Lockpicking"))continue;
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
					pr.put(Skill.LOCKPICKING, Integer.valueOf(progress));
					sk.put(Skill.LOCKPICKING, Integer.valueOf(level));
				}
				if(l.startsWith("Axecraft"))
				{
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("Axecraft"))continue;
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
					pr.put(Skill.AXECRAFT, Integer.valueOf(progress));
					sk.put(Skill.AXECRAFT, Integer.valueOf(level));
				}
				if(l.startsWith("Blocking"))
				{
					if(tokens.length != 2) continue;
					if(!tokens[0].equalsIgnoreCase("Blocking"))continue;
					String x = tokens[1].replaceAll(" ", "");
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
					pr.put(Skill.BLOCKING, Integer.valueOf(progress));
					sk.put(Skill.BLOCKING, Integer.valueOf(level));
				}
				if(l.startsWith("Level"))
				{
					if(tokens.length != 2)continue;
					if(!tokens[0].equalsIgnoreCase("Level"))continue;
					try
					{
						String s = tokens[1].replaceAll(" ", "");
						tl = Integer.parseInt(s);
					}
					catch(NumberFormatException nfe)
					{
						tl = 1;
					}
				}
				if(l.startsWith("Magicka"))
				{
					if(tokens.length != 2)continue;
					if(!tokens[0].equalsIgnoreCase("Magicka"))continue;
					try
					{
						String s = tokens[1].replaceAll(" ", "");
						m = Integer.parseInt(s);
					}
					catch(NumberFormatException nfe)
					{
						m = 0;
					}
				}
				if(l.startsWith("Perk Points"))
				{
					if(tokens.length != 2)continue;
					if(!tokens[0].equalsIgnoreCase("Perk Points"))continue;
					try
					{
						String s = tokens[1].replaceAll(" ", "");
						pp = Integer.parseInt(s);
					}
					catch(NumberFormatException nfe)
					{
						pp = 0;
					}
				}
				if(sk.get(Skill.ARCHERY)== null)sk.put(Skill.ARCHERY, 1);
				if(sk.get(Skill.SWORDSMANSHIP)== null)sk.put(Skill.SWORDSMANSHIP, 1);
				if(sk.get(Skill.PICKPOCKETING)== null)sk.put(Skill.PICKPOCKETING, 1);
				if(sk.get(Skill.DESTRUCTION)== null)sk.put(Skill.DESTRUCTION, 1);
				if(sk.get(Skill.LOCKPICKING)== null)sk.put(Skill.LOCKPICKING, 1);
				if(sk.get(Skill.CONJURATION)== null)sk.put(Skill.CONJURATION, 1);
				if(sk.get(Skill.AXECRAFT)== null)sk.put(Skill.AXECRAFT, 1);
				if(!sk.containsKey(Skill.BLOCKING))sk.put(Skill.BLOCKING, 1);
				//
				if(pr.get(Skill.ARCHERY)== null)pr.put(Skill.ARCHERY, 0);
				if(pr.get(Skill.SWORDSMANSHIP)== null)pr.put(Skill.SWORDSMANSHIP, 0);
				if(pr.get(Skill.PICKPOCKETING)== null)pr.put(Skill.PICKPOCKETING, 0);
				if(pr.get(Skill.DESTRUCTION)== null)pr.put(Skill.DESTRUCTION, 0);
				if(pr.get(Skill.LOCKPICKING)== null)pr.put(Skill.LOCKPICKING, 0);
				if(pr.get(Skill.CONJURATION)== null)pr.put(Skill.CONJURATION, 0);
				if(pr.get(Skill.AXECRAFT)== null)pr.put(Skill.AXECRAFT, 0);
				if(!pr.containsKey(Skill.BLOCKING))pr.put(Skill.BLOCKING, 0);
				skills.put(player, sk);
				progress.put(player, pr);
				level.put(player, tl);
				PerkManager.points.put(player, pp);
				SpellManager.magicka.put(player, m);
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
			bw.write("#Level");
			bw.newLine();
			bw.write("Level: " + level.get(player));
			bw.newLine();
			bw.write("#Magicka");
			bw.newLine();
			bw.write("Magicka: " + SpellManager.magicka.get(player));
			bw.newLine();
			bw.write("#SPENT Perk Points");
			bw.newLine();
			bw.write("Perk Points: " + PerkManager.points.get(player));
			bw.newLine();
			bw.write("#Skill: level, progress");
			bw.newLine();
			bw.write("Archery: " + getSkillLevel(Skill.ARCHERY, player) + "," + getProgress(Skill.ARCHERY,player));
			bw.newLine();
			bw.write("Swordsmanship: " + getSkillLevel(Skill.SWORDSMANSHIP, player) + "," + getProgress(Skill.SWORDSMANSHIP, player));
			bw.newLine();
			bw.write("PickPocket: " + getSkillLevel(Skill.PICKPOCKETING, player) + "," + getProgress(Skill.PICKPOCKETING, player));
			bw.newLine();
			bw.write("Destruction: " + getSkillLevel(Skill.DESTRUCTION, player) + "," + getProgress(Skill.DESTRUCTION, player));
			bw.newLine();
			bw.write("Conjuration: " + getSkillLevel(Skill.CONJURATION, player) + "," + getProgress(Skill.CONJURATION, player));
			bw.newLine();
			bw.write("Lockpicking: " + getSkillLevel(Skill.LOCKPICKING, player) + "," + getProgress(Skill.LOCKPICKING, player));
			bw.newLine();
			bw.write("Axecraft: " + getSkillLevel(Skill.AXECRAFT, player) + "," + getProgress(Skill.AXECRAFT, player));
			bw.newLine();
			bw.write("Blocking: " + getSkillLevel(Skill.BLOCKING, player) + "," + getProgress(Skill.BLOCKING, player));
			bw.newLine();
			bw.flush();
			bw.close();
		}
		catch(IOException ioe)
		{
			p.log.severe("[SkyrimRPG] Could not save player data!");
		}
	}
	public void saveData(Player player)
	{
		saveSkills(player);
		SpellManager sm = new SpellManager(p);
		sm.saveSpells(player);
		PerkManager pm = new PerkManager(p);
		pm.savePerks(player);
	}
	public void resetSkills(Player player)
	{
		HashMap<Skill, Integer> sk = new HashMap<Skill, Integer>();
		sk.put(Skill.PICKPOCKETING, Integer.valueOf(1));
		sk.put(Skill.ARCHERY, Integer.valueOf(1));
		sk.put(Skill.SWORDSMANSHIP, Integer.valueOf(1));
		sk.put(Skill.LOCKPICKING, Integer.valueOf(1));
		sk.put(Skill.DESTRUCTION, Integer.valueOf(1));
		sk.put(Skill.CONJURATION, Integer.valueOf(1));
		sk.put(Skill.ARCHERY, Integer.valueOf(1));
		sk.put(Skill.BLOCKING, Integer.valueOf(1));
		HashMap<Skill, Integer> pr = new HashMap<Skill, Integer>();
		pr.put(Skill.PICKPOCKETING, Integer.valueOf(1));
		pr.put(Skill.ARCHERY, Integer.valueOf(1));
		pr.put(Skill.SWORDSMANSHIP, Integer.valueOf(1));
		pr.put(Skill.LOCKPICKING, Integer.valueOf(1));
		pr.put(Skill.DESTRUCTION, Integer.valueOf(1));
		pr.put(Skill.CONJURATION, Integer.valueOf(1));
		pr.put(Skill.ARCHERY, Integer.valueOf(1));
		pr.put(Skill.BLOCKING, Integer.valueOf(1));
		skills.put(player, sk);
		progress.put(player, pr);
		level.put(player, 1);
		SpellManager.magicka.put(player, 0);
	}
	/**
	 * Calculates if the player levels up or not
	 * @param player The player
	 * @param skill The skill that the player is "practicing"
	 * @return True if the player is leveling up, otherwise false
	 */
	public boolean processExperience(Player player, Skill skill)
	{
		int alevel = SkillManager.getSkillLevel(skill, player);
		int pro = SkillManager.getProgress(skill, player);
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
}
