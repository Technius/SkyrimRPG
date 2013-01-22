package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.spell.SpellManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Healing extends SpellExecutor
{
	public void cast(Player player, SpellManager sm) 
	{
		int health = sm.getPlugin().getSkillManager().getSkillLevel(Skill.RESTORATION, player)/20 + 2;
		if(health + player.getHealth() > 20)health = 20;
		player.setHealth(health + player.getHealth());
		player.sendMessage(ChatColor.GREEN + "You are healed for " + ((float)health/2) + " hearts");
	}
}