package me.dbizzzle.SkyrimRPG.spell;

import java.util.HashMap;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class SpellManager 
{
	public HashMap<Player, Zombie>czombie = new HashMap<Player,Zombie>();
	public HashMap<Player, LivingEntity>conjured = new HashMap<Player, LivingEntity>();
	private HashMap<Player, Spell>boundleft = new HashMap<Player,Spell>();
	private HashMap<Player, Spell>boundright = new HashMap<Player,Spell>();
	private SkyrimRPG p;
	public SpellManager(SkyrimRPG p)
	{
		this.p = p;
	}
	public void clearData()
	{
		boundleft.clear();
		boundright.clear();
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
	public SkyrimRPG getPlugin()
	{
		return p;
	}
}
