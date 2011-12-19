package me.dbizzzle.SkyrimRPG.Skill;

public abstract class Skill 
{
	public abstract Skill getType();
	public abstract int getLevel();
	public static final int MAX_LEVEL = 100;
	public static final int BATTLE = 1;
	public static final int MAGIC = 2;
	public static final int STEALTH = 3;
}
