package me.dbizzzle.SkyrimRPG.Skill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream (plist)));
			}
			catch(IOException ioe)
			{
				p.log.severe("[SkyrimRPG]COULD NOT LOAD \"" + player.getName() + "\"'s PERKS!");
			}
		}
	}
	public void savePerks(Player player)
	{
		
	}
	public void defaultPerks(Player player)
	{
		HashMap<Perk, Integer> pt = new HashMap<Perk, Integer>();
		pt.put(Perk.OVERDRAW, 0);
		perks.put(player, pt);
	}
}
