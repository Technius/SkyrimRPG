package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.PlayerData;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public abstract class SpellExecutor 
{
	public abstract void cast(Player player, Server server, PlayerData pd);
}
