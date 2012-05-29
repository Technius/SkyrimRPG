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
		String sl = toString().toLowerCase();
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