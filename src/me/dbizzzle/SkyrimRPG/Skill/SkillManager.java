package me.dbizzzle.SkyrimRPG.Skill;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class SkillManager 
{
	public static HashMap<Player, HashMap<String, Integer>> skills = new HashMap<Player, HashMap<String, Integer>>();
	public static HashMap<Player, HashMap<String, Integer>> progress = new HashMap<Player, HashMap<String, Integer>>();
	public static HashMap<Player, Integer> level = new HashMap<Player,Integer>();
	
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
		
	}
	public void resetSkills(Player player)
	{
		HashMap<String, Integer> sk = new HashMap<String, Integer>();
		sk.put("Archery", Integer.valueOf(1));
		HashMap<String, Integer> pr = new HashMap<String,Integer>();
		pr.put("Archery", Integer.valueOf(0));
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
		return false;
	}
}
