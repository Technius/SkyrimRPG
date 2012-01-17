package me.dbizzzle.SkyrimRPG.Skill;

public enum Perk 
{
	OVERDRAW(Skill.ARCHERY, 5, null, 20, 40, 60, 80, 100);
	private Skill parent;
	private int maxlevel;
	private int[] reqskilllvl;
	private Perk requisite;
	private Perk(Skill parent, int maxlevel, Perk required, int... rsl)
	{
		this.maxlevel = maxlevel;
		this.parent = parent;
		reqskilllvl = rsl;
		requisite = required;
	}
	/**
	 * Returns the max level of the perk
	 * @return
	 */
	public int getMaxLevel()
	{
		return maxlevel;
	}
	/**
	 * Returns the skill this perk belongs to
	 * @return The "parent" skill
	 */
	public Skill getSkill()
	{
		return parent;
	}
	/**
	 * Returns an array of the required skill levels
	 * @return An array of the required skill levels
	 */
	public int[] getRequiredSkillLevels()
	{
		return reqskilllvl;
	}
	/**
	 * Returns a "nice", user friendly version of the name
	 * @return The "nice" name
	 */
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
	/**
	 * Returns a perk if it has a requisite
	 * @return Perk if it has a requisite, otherwise null
	 */
	public Perk getRequiedPerk()
	{
		return requisite;
	}
}
