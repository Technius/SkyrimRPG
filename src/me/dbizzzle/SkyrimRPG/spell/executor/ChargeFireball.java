package me.dbizzzle.SkyrimRPG.spell.executor;

import org.bukkit.entity.Player;

import me.dbizzzle.SkyrimRPG.spell.SpellManager;

public class ChargeFireball extends SpellExecutor
{
	public void cast(Player player, SpellManager sm) 
	{
		sm.getPlugin().getSpellTimer().chargeFireball(player);
	}
}
