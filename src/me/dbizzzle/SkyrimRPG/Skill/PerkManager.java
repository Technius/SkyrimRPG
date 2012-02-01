package me.dbizzzle.SkyrimRPG.Skill;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class PerkManager 
{
	private me.dbizzzle.SkyrimRPG.SkyrimRPG p;
	public PerkManager(me.dbizzzle.SkyrimRPG.SkyrimRPG s)
	{
		p = s;
	}
	public static HashMap<Player, HashMap<Perk, Integer>> perks = new HashMap<Player, HashMap<Perk, Integer>>();
	public static HashMap<Player, Integer>points = new HashMap<Player, Integer>();
	public int getPoints(Player player)
	{
		return SkillManager.level.get(player) - points.get(player) - 1;
	}
	public boolean hasEnough(Player player)
	{
		return getPoints(player) != 0;
	}
	public boolean canUnlock(Player player, Perk perk, int level)
	{
		if(perk == null)throw new IllegalArgumentException("Perk is null");
		if(SkillManager.skills.get(player).get(perk.getSkill()) > perk.getRequiredSkillLevels()[level - 1])return false;
		if(!perks.get(player).containsKey(perk.getRequiedPerk()) && perk.getRequiedPerk() != null)return false;
		return points.get(player) < SkillManager.level.get(player);
	}
	public void unlock(Player player, Perk perk, int level)
	{
		perks.get(player).put(perk, level);
		points.put(player, points.get(player) + 1);
	}
	public List<Perk> getPerksBySkill(Skill s)
	{
		List<Perk> l = new ArrayList<Perk>();
		for(Perk as:Perk.values())if(as.getSkill() == s)l.add(as);
		return l;
	}
	public void loadPerks(Player player)
	{
		File file = new File(p.getDataFolder().getPath());
		if(!file.exists())file.mkdir();
		File perks = new File(file.getPath() + File.separator + "Perks");
		if(!perks.exists())perks.mkdir();
		File plist = new File(perks.getPath() + File.separator + player.getName() + ".txt");
		if(!plist.exists())defaultPerks(player);
		else
		{
			try
			{
				HashMap<Perk, Integer> pt = new HashMap<Perk, Integer>();
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream (plist)));
				String l;
				while((l=br.readLine())!= null)
				{
					if(l.startsWith("#"))continue;
					String[] tokens = l.replaceAll(" ", "").split("[,]", 2);
					if(tokens.length != 2)continue;
					Perk p;
					int level;
					try
					{
						p = Perk.valueOf(tokens[0].toUpperCase());
					}
					catch(IllegalArgumentException iae)
					{
						continue;
					}
					try
					{
						level = Integer.parseInt(tokens[1]);
					}
					catch(NumberFormatException nfe)
					{
						continue;
					}
					pt.put(p, level);
				}
				PerkManager.perks.put(player, pt);
			}
			catch(IOException ioe)
			{
				p.log.severe("[SkyrimRPG]COULD NOT LOAD \"" + player.getName() + "\"'s PERKS!");
			}
		}
	}
	public void savePerks(Player player)
	{
		File file = new File(p.getDataFolder().getPath());
		if(!file.exists())file.mkdir();
		File perks = new File(file.getPath() + File.separator + "Perks");
		if(!perks.exists())perks.mkdir();
		File plist = new File(perks.getPath() + File.separator + player.getName() + ".txt");
		try
		{
			if(!plist.exists())
			{
				FileOutputStream fos = new FileOutputStream(plist);
				fos.flush();
				fos.close();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(plist));
			bw.write("#Don't edit this unless you like errors");
			bw.newLine();
			for(Perk p:PerkManager.perks.get(player).keySet())
			{
				bw.write(p.toString() + "," + PerkManager.perks.get(player).get(p));
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch(IOException ioe)
		{
			p.log.severe("[SkyrimRPG]COULD NOT SAVE \"" + player.getName() + "\"'s PERKS!!!");
		}
	}
	public void defaultPerks(Player player)
	{
		HashMap<Perk, Integer> pt = new HashMap<Perk, Integer>();
		perks.put(player, pt);
	}
}
