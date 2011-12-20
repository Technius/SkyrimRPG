package me.dbizzzle.SkyrimRPG;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpellManager 
{
	public static List<Fireball>ftracker = new ArrayList<Fireball>();
	SkyrimRPG p;
	public SpellManager(SkyrimRPG p)
	{
		this.p = p;
	}
	public void shootFireball(Player shooter, int multiplier)
	{
		final Vector direction = shooter.getEyeLocation().getDirection().multiply(2);
		Fireball fireball = shooter.getWorld().spawn(shooter.getEyeLocation().add(direction.getX(), direction.getY(), 
			direction.getZ()), Fireball.class);
		fireball.setShooter(shooter);
		fireball.setYield(4F*(multiplier/100));
		ftracker.add(fireball);
	}
}
