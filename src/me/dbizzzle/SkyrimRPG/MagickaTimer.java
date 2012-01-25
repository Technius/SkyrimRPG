package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.entity.Player;

public class MagickaTimer implements Runnable
{
	public void run() 
	{
		for(Player p:SpellManager.magicka.keySet())
		{
			int clevel = SkillManager.getSkillLevel(Skill.CONJURATION, p);
			int dlevel = SkillManager.getSkillLevel(Skill.DESTRUCTION, p);
			int cl = clevel/10;
			int dl = dlevel/10;
			int newv = 5 + cl + dl + SpellManager.magicka.get(p);
			if(newv > 100) SpellManager.magicka.put(p, 100);
			else SpellManager.magicka.put(p, newv);
		}
	}

}
