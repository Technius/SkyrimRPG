package me.dbizzzle.SkyrimRPG.Skill;

/**
 * 
 * Represents a Skill
 *
 */
public enum Skill 
{
	ARCHERY, SWORDSMANSHIP, AXECRAFT, PICKPOCKETING, 
	LOCKPICKING, BLOCKING,
	CONJURATION, DESTRUCTION, RESTORATION;
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
