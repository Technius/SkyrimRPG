package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.entity.Player;

public class MagickaTimer implements Runnable
{
	public void run() 
	{
		for(Player p:SpellManager.magicka.keySet())
		{
			int clevel = SkillManager.getSkillLevel("Conjuration", p);
			int dlevel = SkillManager.getSkillLevel("Destruction", p);
			int cl = clevel/10;
			int dl = dlevel/10;
			int newv = 5 + cl + dl + SpellManager.magicka.get(p);
			if(newv > 100) continue;
			SpellManager.magicka.put(p, newv);
		}
	}

}
