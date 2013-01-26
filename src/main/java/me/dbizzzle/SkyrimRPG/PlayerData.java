package me.dbizzzle.SkyrimRPG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.spell.Spell;

public class PlayerData 
{
	private String player;
	private HashMap<Skill, Integer> skills = new HashMap<Skill, Integer>();
	private HashMap<Skill, Integer> progress = new HashMap<Skill, Integer>();
	private ArrayList<Spell> spells = new ArrayList<Spell>();
	private int level = 1;
	public PlayerData(String player)
	{
		this.player = player;
	}
	public int getSkillLevel(Skill s)
	{
		if(s == null)throw new IllegalArgumentException("Skill cannot be null");
		if(!skills.containsKey(s))return 1;
		return skills.get(s);
	}
	public int getSkillProgress(Skill s)
	{
		if(s == null)throw new IllegalArgumentException("Skill cannot be null");
		if(!progress.containsKey(s))return 0;
		return progress.get(s);
	}
	public void setSkillLevel(Skill s, int level)
	{
		if(s == null)throw new IllegalArgumentException("Skill cannot be null");
		if(level < 0)throw new IllegalArgumentException("Skill level cannot be negative");
		skills.put(s, level);
	}
	public void setSkillProgress(Skill s, int progress)
	{
		if(s == null)throw new IllegalArgumentException("Skill cannot be null");
		if(progress < 0)throw new IllegalArgumentException("Skill progress cannot be negative");
		this.progress.put(s, progress);
	}
	public String getPlayer()
	{
		return player;
	}
	public StringConfig save()
	{
		StringConfig data = new StringConfig();
		for(Map.Entry<Skill, Integer>e:skills.entrySet())
		{
			data.setIntList("skill-" + e.getKey().name(), e.getValue(), getSkillProgress(e.getKey()));
		}
		ArrayList<String>spellnames = new ArrayList<String>();
		for(Spell s:spells)
		{
			spellnames.add(s.name());
		}
		data.setStringList("spells", spellnames.toArray(new String[spellnames.size()]));
		data.setInt("level", level);
		return data;
	}
	public void load(StringConfig data)
	{
		for(Skill skill: Skill.values())
		{
			if(data.hasKey("skill-" + skill.name()))
			{
				int[] i = data.getIntList("skill-" + skill.name(), new int[0]);
				if(i == null || i.length != 2)continue;
				setSkillLevel(skill, i[0]);
				setSkillProgress(skill, i[1]);
			}
		}
		String[] spells = data.getStringList("spells", new String[0]);
		if(spells != null)
		{
			for(int i = 0; i < spells.length; i ++)
			{
				Spell spell = Spell.get(spells[i]);
				if(spell != null)addSpell(spell);
			}
		}
		level = data.getInt("level", 1);
	}
	public String[] saveOld()
	{
		ArrayList<String> s = new ArrayList<String>();
		for(Map.Entry<Skill, Integer>e:skills.entrySet())
		{
			s.add(e.getKey().getName() + ":" + e.getValue() + "," + getSkillProgress(e.getKey()));
		}
		return s.toArray(new String[s.size()]);
	}
	public void loadOld(String[] data)
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
			setSkillProgress(sk, prog);
		}
	}
	public Set<Map.Entry<Skill, Integer>> getLevels()
	{
		return skills.entrySet();
	}
	public Set<Map.Entry<Skill, Integer>>getProgress()
	{
		return progress.entrySet();
	}
	public void addSpell(Spell s)
	{
		if(s == null)throw new IllegalArgumentException("Spell cannot be null");
		if(!spells.contains(s))spells.add(s);
	}
	public void removeSpell(Spell s)
	{
		if(s == null)throw new IllegalArgumentException("Spell cannot be null");
		if(spells.contains(s))spells.remove(s);
	}
	public boolean hasSpell(Spell s)
	{
		if(s == null)throw new IllegalArgumentException("Spell cannot be null");
		return spells.contains(s);
	}
	public int getLevel()
	{
		return level;
	}
	public void setLevel(int level)
	{
		if(level < 0)throw new IllegalArgumentException("Level cannot be negative");
		this.level = level;
	}
	public void cleanup()
	{
		player = null;
		skills.clear();
		progress.clear();
		spells.clear();
	}
}
