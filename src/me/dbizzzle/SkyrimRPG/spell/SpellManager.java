package me.dbizzzle.SkyrimRPG.spell;

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

import me.dbizzzle.SkyrimRPG.SkyrimRPG;
import me.dbizzzle.SkyrimRPG.Skill.Skill;

import org.bukkit.ChatColor;
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
	public HashMap<Player, Zombie>czombie = new HashMap<Player,Zombie>();
	public HashMap<Player, LivingEntity>conjured = new HashMap<Player, LivingEntity>();
	private HashMap<Player, List<Spell>>spells = new HashMap<Player, List<Spell>>();
	private HashMap<Player, Spell>boundleft = new HashMap<Player,Spell>();
	private HashMap<Player, Spell>boundright = new HashMap<Player,Spell>();
	private HashMap<Player, Integer>magicka = new HashMap<Player,Integer>();
	private SkyrimRPG p;
	public SpellManager(SkyrimRPG p)
	{
		this.p = p;
	}
	public int getMagicka(Player player)
	{
		return magicka.get(player);
	}
	public void setMagicka(Player player, int magicka)
	{
		this.magicka.put(player, magicka);
	}
	public void subtractMagicka(Player player, int magicka)
	{
		if(this.magicka.get(player) - magicka < 0)this.magicka.put(player, 0);
		else this.magicka.put(player, this.magicka.get(player) - magicka);
	}
	public boolean hasSpell(Player player, Spell spell)
	{
		return spells.get(player).contains(spell);
	}
	public void resetSpells(Player player)
	{
		spells.put(player, new ArrayList<Spell>());
	}
	private boolean checkMagicka(Player player, Spell spell)
	{
		if(magicka.get(player) < spell.getBaseCost())
		{
			player.sendMessage(ChatColor.RED + "You need at least " + spell.getBaseCost() + " magicka to cast " + spell.getDisplayName() + "; You have " + getMagicka(player) + "!");
			return false;
		}
		return true;
	}
	public void castSpell(Spell spell, Player player)
	{
		if(!checkMagicka(player, spell))return;
		if(spell != null && player != null)
		{
			subtractMagicka(player, spell.getBaseCost());
			spell.cast(player, this);
		}
	}
	public void magickaWarning(Player p, String s)
	{
		p.sendMessage(ChatColor.RED + "You do not have enough magicka to cast " + s + "!");
	}
	public boolean hasEnough(Player p, int magicka)
	{
		if(this.magicka.get(p) >= magicka)return true;
		else return false;
	}
	public void addSpell(Player p, Spell s)
	{
		if(!spells.get(p).contains(s))spells.get(p).add(s);
		saveSpells(p);
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
		if(spells.get(p).isEmpty())
		{
			if(sf.exists())sf.delete();
			return;
		}
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
				bw.write(x.name());
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch(IOException ioe)
		{
			this.p.log.severe("[SkyrimRPG]FAILED TO SAVE SPELLS: " + ioe.getMessage());
		}
	}
	public void clearData()
	{
		spells.clear();
		boundleft.clear();
		boundright.clear();
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
				Spell sp = Spell.get(s);
				if(sp != null)l.add(sp);
			}
			spells.put(p, l);
		}
		catch(IOException ioe)
		{
			this.p.log.severe("[SkyrimRPG]FAILED TO LOAD SPELLS");
		}
	}
	public void bindRight(Player player, Spell s)
	{
		if(s == null)boundright.remove(player);
		else boundright.put(player, s);
	}
	public void bindLeft(Player player, Spell s)
	{
		if(s == null)boundleft.remove(player);
		else boundleft.put(player, s);
	}
	public Spell getBoundLeft(Player player)
	{
		return boundleft.get(player);
	}
	public Spell getBoundRight(Player player)
	{
		return boundright.get(player);
	}
	public Spell[] getSpells(Player player)
	{
		return spells.get(player).toArray(new Spell[spells.get(player).size()]);
	}
	public SkyrimRPG getPlugin()
	{
		return p;
	}
}
