package me.dbizzzle.SkyrimRPG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

public class SpellManager 
{
	public static List<Fireball>ftracker = new ArrayList<Fireball>();
	public static HashMap<Player, Zombie>czombie = new HashMap<Player,Zombie>();
	public static HashMap<Player, List<Spell>>spells = new HashMap<Player, List<Spell>>();
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
	public void raiseZombie(Player player)
	{
		Zombie zombie = (Zombie)player.getWorld().spawnCreature(player.getEyeLocation(), CreatureType.ZOMBIE);
		int alevel = SkillManager.getSkillLevel("Conjuration", player);
		zombie.setHealth(zombie.getHealth()/2 + alevel/10);
		czombie.put(player,zombie);
	}
	public enum Spell
	{
		RAISE_ZOMBIE,FIREBALL,HEALING;
	}
	public boolean hasSpell(Player player, Spell spell)
	{
		return spells.get(player).contains(spell);
	}
}
