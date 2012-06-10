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
	private HashMap<Player, HashMap<Perk, Integer>> perks = new HashMap<Player, HashMap<Perk, Integer>>();
	private HashMap<Player, Integer>points = new HashMap<Player, Integer>();
	/**
	 * 
	 * @param player - The player to check the points for
	 * @return The amount of available points the player can spend.  Calculated with (level - spent points - 1)
	 */
	public int getPoints(Player player)
	{
		return p.getSkillManager().getLevel(player) - points.get(player) - 1;
	}
	/**
	 * 
	 * @param player - The player to set the points for
	 * @param points The SPENT points.  Not to be confused to getPoints
	 */
	public void setPoints(Player player, int points)
	{
		this.points.put(player, points);
	}
	/**
	 * 
	 * @param player - The player to check against
	 * @return True if the player doesn't have 0 points, otherwise false
	 */
	public boolean hasEnough(Player player)
	{
		return getPoints(player) != 0;
	}
	/**
	 * 
	 * @param player - The player to check against
	 * @param perk - The perk that the player wants t ounlock
	 * @param level - The level of the perk
	 * @return True if the player can unlock the perk, otherwise false
	 */
	public boolean canUnlock(Player player, Perk perk, int level)
	{
		if(perk == null)throw new IllegalArgumentException("Perk is null");
		if(p.getSkillManager().getSkillLevel(perk.getSkill(), player) < perk.getRequiredSkillLevels()[level - 1])return false;
		if(perk.getRequiedPerk() != null && !perks.get(player).containsKey(perk.getRequiedPerk()))return false;
		return true;
	}
	/**
	 * 
	 * @param player - The player
	 * @return All the perks the player has
	 */
	public Perk[] getPerks(Player player)
	{
		java.util.Set<Perk> s = perks.get(player).keySet();
		return s.toArray(new Perk[s.size()]);
	}
	/**
	 * 
	 * @param player - The player who is unlocking the perk
	 * @param perk - The perk the player is unlocking
	 * @param level - The level of the perk
	 */
	public void unlock(Player player, Perk perk, int level)
	{
		perks.get(player).put(perk, level);
		points.put(player, points.get(player) + 1);
	}
	/**
	 * 
	 * @param s - The skill that the perks belong to
	 * @return - All the perks that belong to the skill
	 */
	public List<Perk> getPerksBySkill(Skill s)
	{
		List<Perk> l = new ArrayList<Perk>();
		for(Perk as:Perk.values())if(as.getSkill() == s)l.add(as);
		return l;
	}
	/**
	 * 
	 * @param player - The player
	 * @param perk - The perk
	 * @return True if the player has the perk, otherwise false
	 */
	public boolean hasPerk(Player player, Perk perk)
	{
		return perks.get(player).containsKey(perk);
	}
	/**
	 * Adds a perk to the player.  Can be used in the same manner as if this method were a "set" method
	 * @param player - The player
	 * @param perk - The perk that will be added to the player
	 * @param level - The level of the perk
	 */
	public void addPerk(Player player, Perk perk, int level)
	{
		if(level > perk.getMaxLevel())throw new IllegalArgumentException("The perk" + perk.getName() + " has a maximum level of" + level);
		if(level <= 0)throw new IllegalArgumentException("Perk level for " + perk.getName() + " must be between 1 and " + perk.getMaxLevel());
		perks.get(player).put(perk, level);
	}
	/**
	 * 
	 * @param player - The player
	 * @param perk - The perk to remove
	 * @return True if the player had the perk, otherwise false
	 */
	public boolean removePerk(Player player, Perk perk)
	{
		return perks.get(player).remove(perk) != null;
	}
	/**
	 * 
	 * @param player - The player
	 * @param perk - The perk
	 * @return The level of the perk if the player has it, otherwise 0
	 */
	public int getPerkLevel(Player player, Perk perk)
	{
		if(!hasPerk(player, perk))return 0;
		return perks.get(player).get(perk);
	}
	/**
	 * Load the perks the player has, overwrites existing data
	 * @param player - The player
	 */
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
				this.perks.put(player, pt);
			}
			catch(IOException ioe)
			{
				p.log.severe("[SkyrimRPG]COULD NOT LOAD \"" + player.getName() + "\"'s PERKS!");
			}
		}
	}
	/**
	 * Saves the perks that the player has
	 * @param player - The player
	 */
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
			for(Perk p:this.perks.get(player).keySet())
			{
				bw.write(p.toString() + "," + this.perks.get(player).get(p));
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
	/**
	 * Resets all the player's perks
	 * @param player - The player
	 */
	public void defaultPerks(Player player)
	{
		HashMap<Perk, Integer> pt = new HashMap<Perk, Integer>();
		perks.put(player, pt);
	}
	/**
	 * Clears ALL data.  Do not use casually
	 */
	public void clearData()
	{
		perks.clear();
		points.clear();
	}
}
