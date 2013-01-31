package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.Skill.Skill;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Healing extends SpellExecutor
{
	public void cast(Player player, Server server, PlayerData pd) 
	{
		int health = pd.getSkillLevel(Skill.RESTORATION)/20 + 2;
		if(health + player.getHealth() > player.getMaxHealth())health = player.getMaxHealth();
		else health = health + player.getHealth();
		player.setHealth(health);
		player.sendMessage(ChatColor.GREEN + "You have been healed " + ((float)health/2) + " hearts");
	}
}