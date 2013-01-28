package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class ChargeFireball extends SpellExecutor
{
	public void cast(Player player, Server server, PlayerData pd) 
	{
		SkyrimRPG.instance().getSpellTimer().chargeFireball(player);
	}
}
