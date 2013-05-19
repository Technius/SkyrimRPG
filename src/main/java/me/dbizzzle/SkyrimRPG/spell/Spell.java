package me.dbizzzle.SkyrimRPG.spell;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.spell.executor.ChargeFireball;
import me.dbizzzle.SkyrimRPG.spell.executor.ConjureFlameAtronach;
import me.dbizzzle.SkyrimRPG.spell.executor.Flames;
import me.dbizzzle.SkyrimRPG.spell.executor.Frostbite;
import me.dbizzzle.SkyrimRPG.spell.executor.Healing;
import me.dbizzzle.SkyrimRPG.spell.executor.RaiseZombie;
import me.dbizzzle.SkyrimRPG.spell.executor.ShootFireball;
import me.dbizzzle.SkyrimRPG.spell.executor.SpellExecutor;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public enum Spell
{
	RAISE_ZOMBIE(2, 90, null, new RaiseZombie()),
	HEALING(4, 20, null, new Healing()),
	UFIREBALL(10101, 0, null, new ShootFireball()),
	FIREBALL(1, 100, UFIREBALL, new ChargeFireball()),
	FLAMES(3, 20, null, new Flames()),
	CONJURE_FLAME_ATRONACH(5, 100, null, new ConjureFlameAtronach()),
	FROSTBITE(6, 25, null, new Frostbite());
	private int id;
	private int basecost;
	private Spell partner;
	private SpellExecutor executor;
	private Spell(int id, int basecost, Spell partner, SpellExecutor executor)
	{
		this.id = id;
		this.basecost = basecost;
		this.partner = partner;
		this.executor = executor;
	}
	public static Spell get(String spell)
	{
		try
		{
			return valueOf(spell.toUpperCase().trim().replaceAll(" ", "_"));
		}catch(IllegalArgumentException iae){}
		return null;
	}
	public int getId(Spell s)
	{
		return id;
	}
	public int getBaseCost()
	{
		return basecost;
	}
	public static Spell getById(int id)
	{
		for(Spell s:Spell.values())
		{
			if(s.getId(s) == id)return s;
		}
		return null;
	}
	public Spell getPartner()
	{
		return partner;
	}
	public String getDisplayName()
	{
		return toString().replaceAll("_", " ");
	}
	public String toString()
	{
		String s1 = "";
		String[] t = name().toLowerCase().split("_");
		boolean a = false;
		for(String t1: t)
		{
			if(!a)a = true;
			else s1 = s1 + " ";
			s1 = s1 + Character.toUpperCase(t1.charAt(0)) + t1.substring(1);
		}
		return s1;
	}
	public void cast(Player player, Server server, PlayerData pd)
	{
		if(executor != null)executor.cast(player, server, pd);
	}
}