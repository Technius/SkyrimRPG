package me.dbizzzle.SkyrimRPG.Skill;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class SkillManager 
{
	public static HashMap<Player, HashMap<String, Integer>> skills = new HashMap<Player, HashMap<String, Integer>>();
	public static HashMap<String, Integer> getSkills (Player player)
	{
		return skills.get(player);
	}
	public void incrementLevel(String skill, Player player)
	{
		int l = skills.get(player).get(skill).intValue();
		skills.get(player).put(skill, Integer.valueOf(l));
	}
	public void loadSkills(Player player)
	{
		
	}
	public void resetSkills(Player player)
	{
		HashMap<String, Integer> sk = new HashMap<String, Integer>();
		sk.put("Archery", Integer.valueOf(0));
		skills.put(player, sk);
	}
}
