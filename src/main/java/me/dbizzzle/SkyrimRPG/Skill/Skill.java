package me.dbizzzle.SkyrimRPG.Skill;

/**
 * 
 * Represents a Skill
 *
 */
public enum Skill 
{
	ARCHERY(1), SWORDSMANSHIP(1), AXECRAFT(1),  BLOCKING(1), ARMOR(1),
	PICKPOCKETING(3), LOCKPICKING(3), SNEAK(3),
	CONJURATION(2), DESTRUCTION(2), RESTORATION(2), ENCHANTING(2);
	private Skill(int mode)
	{
		this.mode = mode;
	}
	private int mode;
	public static final int COMBAT = 1;
	public static final int MAGIC = 2;
	public static final int THIEF = 3;
	public String getName()
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
	public boolean isCombat()
	{
		return mode == 1;
	}
	public boolean isMagic()
	{
		return mode == 2;
	}
	public boolean isThief()
	{
		return mode == 3;
	}
	public static Skill getSkill(String skill)
	{
		Skill s = null;
		try
		{
			s = valueOf(skill.toUpperCase().replaceAll(" ", ""));
		}catch(IllegalArgumentException iae){}
		return s;
	}
}
