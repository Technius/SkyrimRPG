package me.dbizzzle.SkyrimRPG.Skill;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SkillUtil 
{
	public static void processExperience(PlayerData data, Player player, Skill skill, int exp, SkyrimRPG plugin)
	{
		if(data.processSkillExperience(skill, exp, plugin))
		{
			player.sendMessage(ChatColor.GREEN + skill.getName() + " increased to " + data.getSkillLevel(skill));
			if(data.processLevelExperience())
			{
				player.sendMessage(ChatColor.GREEN + "You are now level " + data.getLevel() + "!");
			}
		}
	}
}
