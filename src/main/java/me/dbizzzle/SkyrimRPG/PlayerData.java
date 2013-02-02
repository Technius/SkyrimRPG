package me.dbizzzle.SkyrimRPG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.spell.Spell;

public class PlayerData 
{
	private String player;
	private HashMap<Skill, Integer> skills = new HashMap<Skill, Integer>();
	private HashMap<Skill, Integer> progress = new HashMap<Skill, Integer>();
	private ArrayList<Spell> spells = new ArrayList<Spell>();
	private HashMap<Perk, Integer>perks = new HashMap<Perk, Integer>();
	private int level = 1;
	private int perkpoints = 0;
	private int magicka = 0;
	private int maxmagicka = 100;
	private int levelprogress = 0;
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
	public void setLevelProgress(int val)
	{
		levelprogress = val;
	}
	public int getLevelProgress()
	{
		return levelprogress;
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
		for(Map.Entry<Perk, Integer>e:perks.entrySet())
		{
			data.setInt("perk-" + e.getKey().name(), e.getValue());
		}
		data.setInt("level", level);
		data.setInt("levelprogress", levelprogress);
		data.setInt("magicka", magicka);
		data.setInt("perkpoints", perkpoints);
		data.setInt("maxmagicka", maxmagicka);
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
		for(Perk perk: Perk.values())
		{
			if(data.hasKey("perk-" + perk.name()))
			{
				int i = data.getInt("perk-" + perk.name(), 0);
				if(i != 0)
				{
					try{setPerkLevel(perk, i);}catch(IllegalArgumentException iae){}
				}
			}
		}
		level = data.getInt("level", 1);
		levelprogress = data.getInt("levelprogress", 0);
		magicka = data.getInt("magicka", 0);
		maxmagicka = data.getInt("maxmagicka", 100);
		perkpoints = data.getInt("perkpoints", 0);
	}
	public void loadOld(String[] data)
	{
		for(String s:data)
		{
			String[] t = s.split(":");
			if(t.length != 2)continue;
			Skill sk = Skill.getSkill(t[0]);
			if(sk == null)
			{
				if(t[0].equalsIgnoreCase("level"))
				{
					try{level = Integer.parseInt(t[1].replaceAll(" ", ""));}
					catch(Exception e){level = 1;}
				}
				else if(t[0].equalsIgnoreCase("magicka"))
				{
					try{magicka = Integer.parseInt(t[1].replaceAll(" ", ""));}
					catch(Exception e){magicka = 0;}
				}
				else if(t[0].equalsIgnoreCase("perk points"))
				{
					try
					{
						perkpoints = level - Integer.parseInt(t[1].replaceAll(" ", ""));
						if(perkpoints < 0)perkpoints = 0;
					}
					catch(Exception e){perkpoints = 0;}
				}
				continue;
			}
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
	public Spell[] getSpells()
	{
		return spells.toArray(new Spell[spells.size()]);
	}
	public void addPerk(Perk p)
	{
		if(p == null)throw new IllegalArgumentException("Perk cannot be null");
		if(perks.containsKey(p))return;
		perks.put(p, 1);
	}
	public void setPerkLevel(Perk p, int level)
	{
		if(p == null)throw new IllegalArgumentException("Perk cannot be null");
		int pmax = p.getMaxLevel();
		if(level < 1 || level > pmax)throw new IllegalArgumentException("Perk level must be from 1 to " + pmax);
		perks.put(p, 1);
	}
	public int getPerkLevel(Perk p)
	{
		if(p == null)throw new IllegalArgumentException("Perk cannot be null");
		if(!perks.containsKey(p))return 0;
		return perks.get(p);
	}
	public void removePerk(Perk p)
	{
		if(p == null)throw new IllegalArgumentException("Perk cannot be null");
		perks.remove(p);
	}
	public boolean hasPerk(Perk p)
	{
		if(p == null)throw new IllegalArgumentException("Perk cannot be null");
		return perks.containsKey(p);
	}
	public Perk[] getPerks()
	{
		Set<Perk>p = perks.keySet();
		return p.toArray(new Perk[p.size()]);
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
	public int getPerkPoints()
	{
		return perkpoints;
	}
	public void setPerkPoints(int val)
	{
		perkpoints = val;
	}
	public int getMagicka()
	{
		return magicka;
	}
	public void setMagicka(int val)
	{
		magicka = val;
	}
	public boolean canUnlockPerk(Perk p, int level)
	{
		if(p == null)throw new IllegalArgumentException("Perk cannot be null");
		if(level < 1)throw new IllegalArgumentException("Level cannot be less than 1");
		if(level > p.getMaxLevel())return false;
		if(!perks.containsKey(p))return level == 1;
		return perks.get(p) + 1 == level;
	}
	public boolean processSkillExperience(Skill s, int exp, SkyrimRPG plugin)
	{
		int alevel = getSkillLevel(s);
		int scap = plugin.getConfigManager().skillLevelCap;
		if(alevel >= scap && scap > 0)return false;
		int pro = getSkillProgress(s) + exp;
		int t = 5 + ((alevel - 1)*2);
		if(s == Skill.ARMOR)t *=4;
		if(pro >= t)
		{
			setSkillLevel(s, alevel + 1);
			setSkillProgress(s, pro - t);
			levelprogress = levelprogress + 1;
			return true;
		}
		else setSkillProgress(s, pro);
		return false;
	}
	public boolean processLevelExperience()
	{
		int comp = 8 + level*3/2;
		if(levelprogress > comp)
		{
			level = level + 1;
			levelprogress = levelprogress - comp;
			perkpoints = perkpoints + 1;
			return true;
		}
		return false;
	}
	public void cleanup()
	{
		player = null;
		skills.clear();
		progress.clear();
		spells.clear();
	}
	public void addMagicka(int val)
	{
		if(val + magicka > maxmagicka)magicka = maxmagicka;
		else magicka = val + magicka;
	}
	public void subtractMagicka(int val)
	{
		if(magicka - val < 0)magicka = 0;
		else magicka = magicka - val;
	}
	public boolean hasEnoughMagicka(int val)
	{
		return magicka >= val;
	}
	public void setMaxMagicka(int val)
	{
		if(val < 0)throw new IllegalArgumentException("Max magicka cannot be negative");
		maxmagicka = val;
	}
	public int getMaxMagicka()
	{
		return maxmagicka;
	}
}
