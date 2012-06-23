package me.dbizzzle.SkyrimRPG;

public enum Spell
{
	RAISE_ZOMBIE(2, 90),FIREBALL(1, 100),HEALING(4, 20),UFIREBALL(10101, 0), FLAMES(3, 20), 
	CONJURE_FLAME_ATRONACH(5, 100), FROSTBITE(6, 25);
	private int id;
	private int basecost;
	private Spell(int id, int basecost)
	{
		this.id = id;
		this.basecost = basecost;
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