package me.dbizzzle.SkyrimRPG.Skill;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class SkillManager 
{
	public static HashMap<Player, HashMap<Skill, Integer>> skills = new HashMap<Player, HashMap<Skill, Integer>>();
	public static HashMap<Skill, Integer> getSkills (Player player)
	{
		return skills.get(player);
	}
}
