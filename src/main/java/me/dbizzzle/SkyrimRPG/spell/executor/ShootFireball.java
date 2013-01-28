package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.Server;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ShootFireball extends SpellExecutor
{
	public void cast(Player player, Server server, PlayerData pd) 
	{
		int m = SkyrimRPG.instance().getSpellTimer().unchargeFireball(player);
		if(m == -1)return;
		player.sendMessage("Fireball shot!");
		final Vector direction = player.getEyeLocation().getDirection().multiply(2);
		Fireball fireball = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), 
			direction.getZ()), Fireball.class);
		fireball.setShooter(player);
		fireball.setYield(5F);
		fireball.setMetadata("fireball", new FixedMetadataValue(SkyrimRPG.instance(), true));
	}
	
}
