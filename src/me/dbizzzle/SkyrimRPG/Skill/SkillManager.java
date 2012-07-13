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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SkillManager 
{
	Logger log = Logger.getLogger("Minecraft");
	private HashMap<Player, HashMap<Skill, Integer>> skills = new HashMap<Player, HashMap<Skill, Integer>>();
	private HashMap<Player, HashMap<Skill, Integer>> progress = new HashMap<Player, HashMap<Skill, Integer>>();
	private HashMap<Player, Integer> level = new HashMap<Player,Integer>();
	private SkyrimRPG p = null;
	public SkillManager(SkyrimRPG a)
	{
		p = a;
	}
	public int getLevel(Player player)
	{
		return level.get(player);
	}
	public void calculateLevel(Player player, Skill s)
	{
		if (processExperience(player, s)) {
			incrementLevel(s, player);
			progress.get(player).put(s, 0);
			calculateLevel(player);
		} else {
			progress.get(player).put(s, progress.get(player).get(s) + 1);
		}
	}
	public int calculateLevel(Player player)
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
	public boolean isLevelingUp(Player player)
	{
		int tot = 0;
		int cl = level.get(player).intValue();
		for(Skill s:skills.get(player).keySet())
		{
			tot = tot + skills.get(player).get(s).intValue();
		}
		if(tot > cl * Skill.values().length)return true;
		return false;
	}
	public HashMap<Skill, Integer> getSkills (Player player)
	{
		return skills.get(player);
	}
	public int getProgress(Skill skill, Player player)
	{
		return progress.get(player).get(skill).intValue();
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
		p.getSpellManager().loadSpells(player);
		p.getPerkManager().loadPerks(player);
	}
	public void clearData()
	{
		skills.clear();
		progress.clear();
		level.clear();
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
				if(l.startsWith("Archery"))loadSkill(tokens, Skill.ARCHERY, pr, sk);
				else if(l.startsWith("Swordsmanship"))loadSkill(tokens, Skill.SWORDSMANSHIP, pr, sk);
				else if(l.startsWith("PickPocket") || l.startsWith("Pickpocketing"))loadSkill(tokens, Skill.PICKPOCKETING, pr, sk);
				else if(l.startsWith("Destruction"))loadSkill(tokens, Skill.DESTRUCTION, pr, sk);
				else if(l.startsWith("Conjuration"))loadSkill(tokens, Skill.CONJURATION, pr, sk);
				else if(l.startsWith("Lockpicking"))loadSkill(tokens, Skill.LOCKPICKING, pr, sk);
				else if(l.startsWith("Axecraft"))loadSkill(tokens, Skill.AXECRAFT, pr, sk);
				else if(l.startsWith("Blocking"))loadSkill(tokens, Skill.BLOCKING, pr, sk);
				else if(l.startsWith("Enchanting"))loadSkill(tokens, Skill.ENCHANTING, pr, sk);
				else if(l.startsWith("Restoration"))loadSkill(tokens, Skill.RESTORATION, pr, sk);
				else if(l.startsWith("Sneak"))loadSkill(tokens, Skill.SNEAK, pr, sk);
				else if(l.startsWith("Armor"))loadSkill(tokens, Skill.ARMOR, pr, sk);
				else if(l.startsWith("Level"))
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
				else if(l.startsWith("Magicka"))
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
				else if(l.startsWith("Perk Points"))
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
			}
			for(Skill s:Skill.values())
			{
				if(!sk.containsKey(s))sk.put(s, 1);
				if(!pr.containsKey(s))pr.put(s, 0);
			}
			skills.put(player, sk);
			progress.put(player, pr);
			level.put(player, tl);
			p.getPerkManager().setPoints(player, pp);
			p.getSpellManager().setMagicka(player, m);
			br.close();
		}
		catch(IOException ioe)
		{
			resetSkills(player);
		}
	}
	public int getSkillLevel(Skill skill, Player player)
	{
		return skills.get(player).get(skill).intValue();
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
			bw.write("Magicka: " + p.getSpellManager().getMagicka(player));
			bw.newLine();
			bw.write("#SPENT Perk Points");
			bw.newLine();
			bw.write("Perk Points: " + p.getPerkManager().getPoints(player));
			bw.newLine();
			bw.write("#Skill: level, progress");
			bw.newLine();
			for(Skill s:Skill.values())
			{
				bw.write(s.getName() + ": " + getSkillLevel(s, player) + "," + getProgress(s, player));
				bw.newLine();
			}
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
		p.getSpellManager().saveSpells(player);
		p.getPerkManager().savePerks(player);
	}
	public void resetSkills(Player player)
	{
		HashMap<Skill, Integer> sk = new HashMap<Skill, Integer>();
		HashMap<Skill, Integer> pr = new HashMap<Skill, Integer>();
		for(Skill s:Skill.values())
		{
			sk.put(s, 1);
			pr.put(s, 0);
		}
		skills.put(player, sk);
		progress.put(player, pr);
		level.put(player, 1);
		p.getPerkManager().setPoints(player, 0);
		p.getPerkManager().defaultPerks(player);
		p.getSpellManager().setMagicka(player, 0);
	}
	private void loadSkill(String[] tokens, Skill skill, HashMap<Skill, Integer> pr, HashMap<Skill, Integer> sk)
	{
		if(tokens.length != 2) return;
		String x = tokens[1].replaceAll(" ", "");
		String[] sep = x.split("[,]",2);
		if(sep.length != 2) return;
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
		pr.put(skill, Integer.valueOf(progress));
		sk.put(skill, Integer.valueOf(level));
	}
	/**
	 * Calculates if the player levels up or not
	 * @param player The player
	 * @param skill The skill that the player is "practicing"
	 * @return True if the player is leveling up, otherwise false
	 */
	public boolean processExperience(Player player, Skill skill)
	{
		int alevel = getSkillLevel(skill, player);
		int scap = p.getConfigManager().skillLevelCap;
		if(alevel >= scap && scap > 0)return false;
		int pro = getProgress(skill, player);
		int t = 5;
		for(int i = 1;i<alevel;i++)
		{
			t=t+2;
		}
		if(skill == Skill.ARMOR)t *=4;
		if(pro >= t)
		{
			return true;
		}
		return false;
	}
}
