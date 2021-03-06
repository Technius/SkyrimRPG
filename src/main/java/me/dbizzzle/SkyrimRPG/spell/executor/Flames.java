package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Flames extends SpellExecutor
{
	public void cast(Player player, Server server, PlayerData pd) 
	{
		final Vector direction = player.getEyeLocation().getDirection().multiply(2);
		for(int i = 0;i < 3; i++)
		{
			SmallFireball fireball = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), SmallFireball.class);
			fireball.setShooter(player);
			fireball.setVelocity(direction.multiply(0.25));
			fireball.setYield(0);
			fireball.setIsIncendiary(false);
			fireball.setMetadata("flames", new FixedMetadataValue(SkyrimRPG.instance(), true));
		}
	}
}
