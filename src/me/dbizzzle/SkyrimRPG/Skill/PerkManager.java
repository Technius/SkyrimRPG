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
		return SkillManager.level.get(player) - points.get(player);
	}
	public boolean hasEnough(Player player)
	{
		return SkillManager.level.get(player) > points.get(player);
	}
	public boolean canUnlock(Player player, Perk perk, int level)
	{
		int last = perk.getRequiredSkillLevels()[0];
		for(int c : perk.getRequiredSkillLevels())
		{
			if(c <= perk.getRequiredSkillLevels()[level - 1])last = c;
			else if(last <= perk.getRequiredSkillLevels()[level - 1])return true;
		}
		return true;
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
