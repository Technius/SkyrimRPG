package me.dbizzzle.SkyrimRPG;

public enum Spell
{
	RAISE_ZOMBIE(2, 90, null),HEALING(4, 20, null),
	UFIREBALL(10101, 0, null), FIREBALL(1, 100, UFIREBALL),FLAMES(3, 20, null), 
	CONJURE_FLAME_ATRONACH(5, 100, null), FROSTBITE(6, 25, null);
	private int id;
	private int basecost;
	private Spell partner;
	private Spell(int id, int basecost, Spell partner)
	{
		this.id = id;
		this.basecost = basecost;
		this.partner = partner;
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
			else s1 = s1 + "_";
			boolean o = false;
			for(char c:t1.toCharArray())
			{
				if(!o)
				{
					o = true;
					s1 = "" + Character.toUpperCase(c);
				}
				else s1 = s1 + c;
			}
		}
		return s1;
	}
}