package me.dbizzzle.SkyrimRPG.Skill;

public class Archery extends Skill
{
	private int level;
	/**
	 * Creates a new instance of archery with the number of levels
	 * @param level The level of this skill
	 * @throws IllegalArgumentException When the level is higher than 100 or lower than 0
	 */
	public Archery(int level)
	{
		if(level > 100 || level < 0) throw new IllegalArgumentException("Level cannot be higher than 100 or lower than 0");
		this.level = level;
	}
	/**
	 * Creates a new instance of archery with a level of 0
	 */
	public Archery()
	{
		this.level = 0;
	}
	public int getLevel()
	{
		return level;
	}
	public Skill getType() 
	{
		return new Archery();
	}
}
