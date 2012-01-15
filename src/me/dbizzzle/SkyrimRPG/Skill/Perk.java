package me.dbizzzle.SkyrimRPG.Skill;

public enum Perk 
{
	OVERDRAW(Skill.ARCHERY, 5, 20, 40, 60, 80, 100);
	private Skill parent;
	private int maxlevel;
	private int[] reqskilllvl;
	private Perk(Skill parent, int maxlevel, int... rsl)
	{
		this.maxlevel = maxlevel;
		this.parent = parent;
		reqskilllvl = rsl;
	}
	public int getMaxLevel()
	{
		return maxlevel;
	}
	public Skill getSkill()
	{
		return parent;
	}
	public int[] getRequiredSkillLevels()
	{
		return reqskilllvl;
	}
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
}
