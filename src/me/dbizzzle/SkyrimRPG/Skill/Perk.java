package me.dbizzzle.SkyrimRPG.Skill;

/**
 * Represents a perk
 */
public enum Perk 
{
	OVERDRAW(Skill.ARCHERY, 5, null, 20, 40, 60, 80, 100), SWORDSMAN(Skill.SWORDSMANSHIP, 5, null, 20, 40, 60, 80, 100),
	BLADESMAN(Skill.SWORDSMANSHIP, 3, Perk.SWORDSMAN, 30 , 60, 90), SHIELD_WALL(Skill.BLOCKING, 5, null, 20, 40, 60, 80, 100)
	, DEFLECT_ARROWS(Skill.BLOCKING, 1, Perk.SHIELD_WALL, 30);
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
		//Some people don't use underscores-Below two lines are deprecated
		//String sl = toString().toLowerCase().replaceAll("_", " ");
		//String[] st = sl.split("[ ]");
		String[] st = toString().toLowerCase().split("[_]");
		String m = Character.toUpperCase(st[0].charAt(0)) + st[0].substring(1);
		for(String x: st)
		{
			if(x.equalsIgnoreCase(st[0]))continue;
			//Deprecated- Some people don't use underscores
			//m = m + " " + Character.toUpperCase(x.charAt(0)) + x.substring(1);
			m = m + "_" + Character.toUpperCase(x.charAt(0)) + x.substring(1);
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
