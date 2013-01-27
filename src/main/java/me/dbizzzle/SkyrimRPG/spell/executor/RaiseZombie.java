package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.spell.SpellManager;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class RaiseZombie extends SpellExecutor
{
	public void cast(Player player, SpellManager sm) 
	{
		player.sendMessage("You conjure up a zombie follower to fight for you");
		if(sm.czombie.containsKey(player))sm.czombie.get(player).remove();
		Zombie zombie = (Zombie)player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ZOMBIE);
		int alevel = sm.getPlugin().getSkillManager().getSkillLevel(Skill.CONJURATION, player);
		zombie.setHealth(zombie.getHealth()/2 + alevel/10);
		sm.czombie.put(player,zombie);
	}
}
