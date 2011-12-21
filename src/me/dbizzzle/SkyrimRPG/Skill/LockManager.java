package me.dbizzzle.SkyrimRPG.Skill;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;

public class LockManager 
{
	public static HashMap<Player, Block> locklist = new HashMap<Player,Block>();
	SkyrimRPG r;
	public LockManager(SkyrimRPG r)
	{
		this.r = r;
	}
	public void loadLocks()
	{
		
	}
}
