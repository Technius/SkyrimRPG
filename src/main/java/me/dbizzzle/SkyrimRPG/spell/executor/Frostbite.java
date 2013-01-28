package me.dbizzzle.SkyrimRPG.spell.executor;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Frostbite extends SpellExecutor
{
	public void cast(Player player, Server server, PlayerData pd) 
	{
		for(int i = 0;i < 3; i++)
		{
			Snowball snowball = player.launchProjectile(Snowball.class);
			snowball.setShooter(player);
			double y = snowball.getVelocity().getY();
			Vector v = snowball.getVelocity().multiply(2.5);
			v.setY(y);
			snowball.setVelocity(v);
			snowball.setMetadata("frostbite", new FixedMetadataValue(SkyrimRPG.instance(), true));
		}
	}
}
