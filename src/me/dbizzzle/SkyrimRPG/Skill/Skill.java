package me.dbizzzle.SkyrimRPG.Skill;

/**
 * 
 * Represents a Skill
 *
 */
public enum Skill 
{
	ARCHERY, SWORDSMANSHIP, AXECRAFT,  BLOCKING, ARMOR,
	PICKPOCKETING, LOCKPICKING, SNEAK,
	CONJURATION, DESTRUCTION, RESTORATION, ENCHANTING;
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
