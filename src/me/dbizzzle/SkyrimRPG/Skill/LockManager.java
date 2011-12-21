package me.dbizzzle.SkyrimRPG.Skill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;

public class LockManager 
{
	public static HashMap<String, HashMap<Block,Integer>> locklist = new HashMap<String,HashMap<Block,Integer>>();
	SkyrimRPG r;
	public LockManager(SkyrimRPG r)
	{
		this.r = r;
	}
	public void saveLocks()
	{
		
	}
	public void saveLock(Player player)
	{
		File locks = new File(r.getDataFolder().getPath() + File.separator + "Locks");
		File s = new File(locks.getPath() + File.separator + player.getName() + ".txt");
		if(!locks.exists())locks.mkdir();
		try
		{
			FileOutputStream fos = new FileOutputStream(s);
			fos.flush();
			fos.close();
			BufferedWriter bw = new BufferedWriter(new FileWriter(s));
			for(Block b:locklist.get(player).keySet())
			{
				bw.write(player.getName() + ":" + b.getX() + "," + b.getY() + "," + b.getZ() + "," + b.getWorld() + "," + locklist.get(player).get(b).intValue());
				bw.newLine();
			}
		}
		catch(IOException ioe)
		{
			r.log.severe("[SkyrimRPG]Could not save \"" + player.getName() + "\"'s locks!");
		}
	}
	public void loadLocks()
	{
		File locks = new File(r.getDataFolder().getPath() + File.separator + "Locks");
		if(!locks.exists())
		{
			locks.mkdir();
			return;
		}
		try
		{
			File[] c = locks.listFiles();
			if(c==null)return;
			for(File f: c)
			{
				HashMap<Block,Integer> hm = new HashMap<Block,Integer>();
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String name = "Technius";
				String l;
				while((l=br.readLine()) != null)
				{
					if(l.startsWith("#"))continue;
					String[] t = l.split("[: ]+", 2);
					if(t.length != 2) continue;
					String[] s = t[1].split("[ ]+");
					String a = "";
					for(String x:s)a=a+x;
					String[] commas = a.split("[,]",5);
					if(commas.length != 5)continue;
					int level = 1;
					double x = 0;
					double y = 0;
					double z = 0;
					try{level = Integer.parseInt(commas[3]);}catch(NumberFormatException n){level = 1;}
					try
					{
						x = Double.parseDouble(commas[0]);
						y = Double.parseDouble(commas[1]);
						z = Double.parseDouble(commas[2]);
					}
					catch(NumberFormatException nfe){continue;}
					World w = r.getServer().getWorld(commas[4]);
					if(w==null)continue;
					Location loc = new Location(w,x,y,z);
					hm.put(loc.getBlock(), new Integer(level));
					name = t[0];
				}
				br.close();
				locklist.put(name, hm);
			}
		}
		catch(IOException ioe)
		{
			r.log.severe("[SkyrimRPG]Could not load locks!");
		}
	}
	public void addLock(Block b, Player owner, int level)
	{
		
	}
	public enum Level
	{
		NOVICE(1),APPRENTICE(2),ADEPT(3),EXPERT(4),MASTER(5);
		private Level(int c)
		{
			
		}
	}
}
