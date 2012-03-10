package me.dbizzzle.SkyrimRPG;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

public class SpellManager 
{
	public static List<Fireball>ftracker = new ArrayList<Fireball>();
	public static HashMap<Player, Zombie>czombie = new HashMap<Player,Zombie>();
	public static HashMap<Player, LivingEntity>conjured = new HashMap<Player, LivingEntity>();
	public static HashMap<Player, List<Spell>>spells = new HashMap<Player, List<Spell>>();
	public static HashMap<Player, Spell>boundleft = new HashMap<Player,Spell>();
	public static HashMap<Player, Spell>boundright = new HashMap<Player,Spell>();
	public static HashMap<Player, Integer>magicka = new HashMap<Player,Integer>();
	public static List<SmallFireball>flames = new ArrayList<SmallFireball>();
	public static List<Snowball>frostbite = new ArrayList<Snowball>();
	SkyrimRPG p;
	public SpellManager(SkyrimRPG p)
	{
		this.p = p;
	}
	public void cflameatronach(Player player)
	{
		if(!hasEnough(player, 100))
		{
			magickaWarning(player, "Conjure Flame Atronach");
			return;
		}
		else
		{
			magicka.put(player, magicka.get(player) - 100);
			if(conjured.containsKey(player))conjured.get(player).remove();
			Blaze blaze = (Blaze)player.getWorld().spawnCreature(player.getEyeLocation(), EntityType.BLAZE);
			int alevel = SkillManager.getSkillLevel(Skill.CONJURATION, player);
			blaze.setHealth(blaze.getHealth()/2 + alevel/10);
			conjured.put(player, blaze);
			player.sendMessage("You conjure up a flame atronach to fight for you");
		}
	}
	public void heal(Player p)
	{
		if(!hasEnough(p, 20))
		{
			magickaWarning(p, "Healing");
			return;
		}
		else
		{
			magicka.put(p, magicka.get(p) - 20);
			int health = SkillManager.skills.get(p).get(Skill.RESTORATION)/20 + 2;
			if(health + p.getHealth() > 20)health = 20 - p.getHealth();
			p.setHealth(health + p.getHealth());
			p.sendMessage(ChatColor.GREEN + "You are healed for " + health/2 + " hearts");
		}
	}
	public void flames(Player player)
	{
		if(!hasEnough(player, 20))
		{
			magickaWarning(player, "Flames");
			return;
		}
		else
		{
			magicka.put(player, magicka.get(player) - 20);
			final Vector direction = player.getEyeLocation().getDirection().multiply(2);
			for(int i = 0;i < 3; i++)
			{
				SmallFireball fireball = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), SmallFireball.class);
				fireball.setShooter(player);
				fireball.setVelocity(direction.multiply(0.25));
				fireball.setYield(0);
				fireball.setIsIncendiary(false);
				flames.add(fireball);
			}
		}
	}
	public void shootFireball(Player shooter, int multiplier)
	{
		shooter.sendMessage("Fireball shot!");
		final Vector direction = shooter.getEyeLocation().getDirection().multiply(2);
		Fireball fireball = shooter.getWorld().spawn(shooter.getEyeLocation().add(direction.getX(), direction.getY(), 
			direction.getZ()), Fireball.class);
		fireball.setShooter(shooter);
		fireball.setYield(4F*(multiplier/100));
		ftracker.add(fireball);
	}
	public void frostBite(Player player)
	{
		if(!hasEnough(player, 25))
		{
			magickaWarning(player, "Frostbite");
			return;
		}
		else
		{
			magicka.put(player, magicka.get(player) - 25);
			for(int i = 0;i < 3; i++)
			{
				Snowball snowball = player.launchProjectile(Snowball.class);
				snowball.setShooter(player);
				double y = snowball.getVelocity().getY();
				Vector v = snowball.getVelocity().multiply(2.5);
				v.setY(y);
				snowball.setVelocity(v);
				frostbite.add(snowball);
			}
		}
	}
	public void raiseZombie(Player player)
	{
		if(!hasEnough(player, 90))
		{
			magickaWarning(player, "Raise Zombie");
			return;
		}
		else
		{
			player.sendMessage("You conjure up a zombie follower to fight for you");
			magicka.put(player, magicka.get(player) - 60);
			if(czombie.containsKey(player))czombie.get(player).remove();
			Zombie zombie = (Zombie)player.getWorld().spawnCreature(player.getEyeLocation(), EntityType.ZOMBIE);
			int alevel = SkillManager.getSkillLevel(Skill.CONJURATION, player);
			zombie.setHealth(zombie.getHealth()/2 + alevel/10);
			czombie.put(player,zombie);
		}
	}
	public enum Spell
	{
		RAISE_ZOMBIE(2),FIREBALL(1),HEALING(4),UFIREBALL(10101), FLAMES(3), CONJURE_FLAME_ATRONACH(5), FROSTBITE(6);
		private int id;
		private Spell(int id)
		{
			this.id = id;
		}
		public int getId(Spell s)
		{
			return id;
		}
		public static Spell getById(int id)
		{
			for(Spell s:Spell.values())
			{
				if(s.getId(s) == id)return s;
			}
			return null;
		}
		public String getDisplayName()
		{
			String sl = toString().toLowerCase().replaceAll("_", " ");
			String[] st = sl.split("[ ]");
			String m = Character.toUpperCase(st[0].charAt(0)) + st[0].substring(1);
			for(String x: st)
			{
				if(x.equalsIgnoreCase(st[0]))continue;
				m = m + " " + Character.toUpperCase(x.charAt(0)) + x.substring(1);
			}
			return m;
		}
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
			return true;
		case RAISE_ZOMBIE:
			raiseZombie(player);
			return true;
		case UFIREBALL:
			int m = p.st.unchargeFireball(player);
			if(m == -1) return false;
			p.sm.shootFireball(player, m);
			return true;
		case FLAMES:
			flames(player);
			return true;
		case CONJURE_FLAME_ATRONACH:
			cflameatronach(player);
			return true;
		case HEALING:
			heal(player);
			return true;
		case FROSTBITE:
			frostBite(player);
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
		else return false;
	}
	public void addSpell(Player p, Spell s)
	{
		saveSpells(p);
		if(!spells.get(p).contains(s))spells.get(p).add(s);
	}
	public boolean removeSpell(Player p, Spell s)
	{
		saveSpells(p);
		return spells.get(p).remove(s);
	}
	public boolean useBook(Player p, int id)
	{
		Spell s = Spell.getById(id);
		if(s == null)return false;
		if(hasSpell(p, s))
		{
			p.sendMessage(ChatColor.RED + "You already know how to cast " + s.toString());
			return false;
		}
		addSpell(p, s);
		p.sendMessage(ChatColor.GREEN + "You have learned how to cast " + s.toString());
		return true;
	}
	public void saveSpells(Player p)
	{
		List<Spell> l = spells.get(p);
		File magic = new File(this.p.getDataFolder().getPath() + File.separator + "Magic");
		if(!magic.exists())magic.mkdir();
		File sf = new File(magic.getPath() + File.separator + p.getName() + ".txt");
		try
		{
			if(!sf.exists())
			{
				FileOutputStream fos = new FileOutputStream(sf);
				fos.flush();
				fos.close();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(sf));
			bw.write("#Spells list for SkyrimRPG, do NOT edit unless you like errors.");
			bw.newLine();
			for(Spell x:l)
			{
				bw.write(x.toString());
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch(IOException ioe)
		{
			this.p.log.severe("[SkyrimRPG]FAILED TO SAVE SPELLS");
		}
	}
	public void loadSpells(Player p)
	{
		List<Spell>l = new ArrayList<Spell>();
		File magic = new File(this.p.getDataFolder().getPath() + File.separator + "Magic");
		if(!magic.exists())magic.mkdir();
		File sf = new File(magic.getPath() + File.separator + p.getName() + ".txt");
		try
		{
			if(!sf.exists())
			{
				spells.put(p, l);
				return;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sf)));
			String s;
			while((s=br.readLine()) != null)
			{
				if(s.startsWith("#"))continue;
				s.replaceAll(" ", "");
				l.add(Spell.valueOf(s));
			}
			spells.put(p, l);
		}
		catch(IOException ioe)
		{
			this.p.log.severe("[SkyrimRPG]FAILED TO LOAD SPELLS");
		}
	}
}
