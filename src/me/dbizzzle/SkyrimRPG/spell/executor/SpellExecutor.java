package me.dbizzzle.SkyrimRPG.spell.executor;

import org.bukkit.entity.Player;

import me.dbizzzle.SkyrimRPG.spell.SpellManager;

public abstract class SpellExecutor 
{
	public abstract void cast(Player player, SpellManager sm);
}
