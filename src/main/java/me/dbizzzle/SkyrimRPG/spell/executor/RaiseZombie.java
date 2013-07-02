package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.SkyrimRPG;
import me.dbizzzle.SkyrimRPG.skill.Skill;
import me.dbizzzle.SkyrimRPG.spell.SpellManager;

import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class RaiseZombie extends SpellExecutor
{
	public void cast(Player player, Server server, PlayerData pd) 
	{
		player.sendMessage("You conjure up a zombie follower to fight for you");
		SpellManager sm = SkyrimRPG.instance().getSpellManager();
		if(sm.czombie.containsKey(player))sm.czombie.get(player).remove();
		Zombie zombie = (Zombie)player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ZOMBIE);
		int alevel = pd.getSkillLevel(Skill.CONJURATION);
		double h = zombie.getHealth()/2 + alevel/10;
		zombie.setMaxHealth(h);
		zombie.setHealth(h);
		zombie.setCustomName(player.getName() + "'s Zombie");
		zombie.setCustomNameVisible(true);
		sm.czombie.put(player,zombie);
	}
}
