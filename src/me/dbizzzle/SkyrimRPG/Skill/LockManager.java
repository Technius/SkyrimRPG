package me.dbizzzle.SkyrimRPG.Skill;

import java.io.File;
import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;

public class LockManager 
{
	public static HashMap<Player, HashMap<Block,Integer>> locklist = new HashMap<Player,HashMap<Block,Integer>>();
	SkyrimRPG r;
	public LockManager(SkyrimRPG r)
	{
		this.r = r;
	}
	public void loadLocks()
	{
		File locks = new File(r.getDataFolder().getPath() + File.separator + "Locks");
		if(!locks.exists())
		{
			locks.mkdir();
			return;
		}
	}
	public void addLock(Block b, Player owner, int level)
	{
		
	}
	public enum Level
	{
		NOVICE(1),APPRENTICE(2),ADEPT(3),EXPERT(4),MASTER(5);
		private int level;
		private Level(int c)
		{
			level = c;
		}
	}
}
