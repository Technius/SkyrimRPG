package me.dbizzzle.SkyrimRPG.spell.executor;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import me.dbizzzle.SkyrimRPG.spell.SpellManager;

public class ShootFireball extends SpellExecutor
{
	public void cast(Player player, SpellManager sm) 
	{
		int m = sm.getPlugin().getSpellTimer().unchargeFireball(player);
		if(m == -1)return;
		player.sendMessage("Fireball shot!");
		final Vector direction = player.getEyeLocation().getDirection().multiply(2);
		Fireball fireball = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), 
			direction.getZ()), Fireball.class);
		fireball.setShooter(player);
		fireball.setYield(5F);
		fireball.setMetadata("fireball", new FixedMetadataValue(sm.getPlugin(), true));
	}
	
}
