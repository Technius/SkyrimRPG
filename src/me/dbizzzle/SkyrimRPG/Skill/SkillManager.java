package me.dbizzzle.SkyrimRPG.Skill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SkillManager 
{
	Logger log = Logger.getLogger("Minecraft");
	private ArrayList<SkillData> data = new ArrayList<SkillData>();
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
			getData(player).setProgress(s, 0);
			calculateLevel(player);
		} else {
			SkillData d = getData(player);
			d.setProgress(s, d.getSkillProgress(s) + 1);
		}
	}
	public int calculateLevel(Player player)
	{
		int lvl = getData(player).calculateLevel();
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
		for(Map.Entry<Skill, Integer> k :getData(player).getLevels())
		{
			tot = tot + k.getValue();
		}
		if(tot > cl * Skill.values().length)return true;
		return false;
	}
	public Set<Map.Entry<Skill, Integer>> getSkills (Player player)
	{
		return getData(player).getLevels();
	}
	public int getProgress(Skill skill, Player player)
	{
		return getData(player).getSkillProgress(skill);
	}
	public void setLevel(Skill skill, Player player, int level)
	{
		getData(player).setSkillLevel(skill, level);
	}
	public void incrementLevel(Skill skill, Player player)
	{
		SkillData d = getData(player);
		int l = d.getSkillLevel(skill) + 1;
		d.setSkillLevel(skill, l);
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
		for(SkillData d:data)d.cleanup();
		data.clear();
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
			int tl = 1;
			int m = 0;
			int pp = 0;
			ArrayList<String>dat = new ArrayList<String>();
			while((l=br.readLine())!= null)
			{
				if(l.startsWith("#"))continue;
				String tokens[] = l.split(":", 2);
				if(tokens.length != 2)continue;
				if(tokens[0].equalsIgnoreCase("level"))
				{
					if(tokens.length != 2)continue;
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
				else if(tokens[0].equalsIgnoreCase("magicka"))
				{
					if(tokens.length != 2)continue;
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
				else if(tokens[0].equalsIgnoreCase("perk points"))
				{
					if(tokens.length != 2)continue;
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
				else dat.add(l);
			}
			SkillData d = new SkillData(player);
			d.load(dat.toArray(new String[dat.size()]));
			addData(d);
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
		return getData(player).getSkillLevel(skill);
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
			for(String s:getData(player).save())
			{
				System.out.println(s);
				bw.write(s);
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
		for(Map.Entry<Skill,Integer> k:getData(player).getLevels())k.setValue(1);
		for(Map.Entry<Skill,Integer> k:getData(player).getProgress())k.setValue(0);
		level.put(player, 1);
		p.getPerkManager().setPoints(player, 0);
		p.getPerkManager().defaultPerks(player);
		p.getSpellManager().setMagicka(player, 0);
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
	public SkillData getData(Player player)
	{
		for(SkillData s:data)
		{
			if(s.getPlayer() == player)return s;
		}
		SkillData d = new SkillData(player);
		data.add(d);
		return d;
	}
	public void removeData(Player player)
	{
		SkillData d = getData(player);
		if(d != null)data.remove(d);
	}
	public void addData(SkillData d)
	{
		if(getData(d.getPlayer()) != null)return;
		data.add(d);
	}
}
