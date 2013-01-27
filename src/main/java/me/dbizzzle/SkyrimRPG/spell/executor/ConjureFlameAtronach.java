package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.spell.SpellManager;

import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ConjureFlameAtronach extends SpellExecutor
{
	public void cast(Player player, SpellManager sm) 
	{
		if(sm.conjured.containsKey(player))sm.conjured.get(player).remove();
		Blaze blaze = (Blaze)player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.BLAZE);
		int alevel = sm.getPlugin().getSkillManager().getSkillLevel(Skill.CONJURATION, player);
		blaze.setHealth(blaze.getHealth()/2 + alevel/10);
		sm.conjured.put(player, blaze);
		player.sendMessage("You conjure up a flame atronach to fight for you");
	}
}
