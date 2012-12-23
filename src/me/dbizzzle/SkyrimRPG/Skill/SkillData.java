package me.dbizzzle.SkyrimRPG.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

public class SkillData 
{
	private Player player;
	private HashMap<Skill, Integer> skills = new HashMap<Skill, Integer>();
	private HashMap<Skill, Integer> progress = new HashMap<Skill, Integer>();
	public SkillData(Player player)
	{
		this.player = player;
	}
	public int getSkillLevel(Skill s)
	{
		if(!skills.containsKey(s))return 1;
		return skills.get(s);
	}
	public int getSkillProgress(Skill s)
	{
		if(!progress.containsKey(s))return 0;
		return progress.get(s);
	}
	public void setSkillLevel(Skill s, int level)
	{
		skills.put(s, level);
	}
	public void setProgress(Skill s, int progress)
	{
		this.progress.put(s, progress);
	}
	public Player getPlayer()
	{
		return player;
	}
	public String[] save()
	{
		ArrayList<String> s = new ArrayList<String>();
		for(Map.Entry<Skill, Integer>e:skills.entrySet())
		{
			s.add(e.getKey().getName() + ":" + e.getValue() + "," + getSkillProgress(e.getKey()));
		}
		return s.toArray(new String[s.size()]);
	}
	public void load(String[] data)
	{
		for(String s:data)
		{
			String[] t = s.split(":");
			if(t.length != 2)continue;
			Skill sk = Skill.getSkill(t[0]);
			if(sk == null)continue;
			String[] ts = t[1].split(",");
			if(ts.length != 2)continue;
			int lv;
			int prog;
			try
			{
				lv = Integer.parseInt(ts[0].replaceAll(" ", ""));
				prog = Integer.parseInt(ts[1].replaceAll(" ", ""));
			}catch(NumberFormatException nfe){continue;}
			setSkillLevel(sk, lv);
			setProgress(sk, prog);
			System.out.println(lv + "," + prog + ";" + getSkillLevel(sk) + "," + getSkillProgress(sk));
		}
	}
	public int calculateLevel()
	{
		int lvl = 0;
		int tot = 0;
		for(Skill s:skills.keySet())
		{
			tot = tot + skills.get(s);
			while(tot >= 8)
			{
				lvl = lvl + 1;
				tot = tot - 8;
			}
		}
		return lvl;
	}
	public Set<Map.Entry<Skill, Integer>> getLevels()
	{
		return skills.entrySet();
	}
	public Set<Map.Entry<Skill, Integer>>getProgress()
	{
		return progress.entrySet();
	}
	public void cleanup()
	{
		player = null;
		skills.clear();
		progress.clear();
	}
}
