package me.dbizzzle.SkyrimRPG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.ChatColor;
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
	public static HashMap<Player, Spell>boundleft = new HashMap<Player,Spell>();
	public static HashMap<Player, Spell>boundright = new HashMap<Player,Spell>();
	public static HashMap<Player, Integer>magicka = new HashMap<Player,Integer>();
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
		if(!hasEnough(player, 60))
		{
			magickaWarning(player, "Raise Zombie");
			return;
		}
		else magicka.put(player, magicka.get(player) - 60);
		if(czombie.containsKey(player))czombie.get(player).remove();
		Zombie zombie = (Zombie)player.getWorld().spawnCreature(player.getEyeLocation(), CreatureType.ZOMBIE);
		int alevel = SkillManager.getSkillLevel("Conjuration", player);
		zombie.setHealth(zombie.getHealth()/2 + alevel/10);
		czombie.put(player,zombie);
	}
	public enum Spell
	{
		RAISE_ZOMBIE,FIREBALL,HEALING,UFIREBALL;
	}
	public boolean hasSpell(Player player, Spell spell)
	{
		return spells.get(player).contains(spell);
	}
	public void resetSpells(Player player)
	{
		spells.put(player, new ArrayList<Spell>());
	}
	public boolean castSpell(Spell spell, Player player)
	{
		switch(spell)
		{
		case FIREBALL:
			p.st.chargeFireball(player);
			player.sendMessage("Charging Fireball...");
			return true;
		case RAISE_ZOMBIE:
			raiseZombie(player);
			player.sendMessage("You conjure up a zombie follower to fight for you");
			return true;
		case UFIREBALL:
			int m = p.st.unchargeFireball(player);
			if(m == -1) return false;
			p.sm.shootFireball(player, m);
			player.sendMessage("Fireball shot!");
			return true;
		default:
			return false;
		}
	}
	public void magickaWarning(Player p, String s)
	{
		p.sendMessage(ChatColor.RED + "You do not have enough magicka to cast " + s + "!");
	}
	public boolean hasEnough(Player p, int magicka)
	{
		if(SpellManager.magicka.get(p) >= magicka)return true;
		return false;
	}
	public void addSpell(Player p, Spell s)
	{
		if(!spells.get(p).contains(s))spells.get(p).add(s);
	}
	public boolean removeSpell(Player p, Spell s)
	{
		return spells.get(p).remove(s);
	}
}
