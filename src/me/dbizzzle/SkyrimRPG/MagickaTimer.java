package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.Skill;

import org.bukkit.entity.Player;

public class MagickaTimer implements Runnable
{
	private SkyrimRPG plugin;
	public MagickaTimer(SkyrimRPG srpg)
	{
		plugin = srpg;
	}
	public void run() 
	{
		for(Player p:SpellManager.magicka.keySet())
		{
			int clevel = plugin.getSkillManager().getSkillLevel(Skill.CONJURATION, p);
			int dlevel = plugin.getSkillManager().getSkillLevel(Skill.DESTRUCTION, p);
			int cl = clevel/10;
			int dl = dlevel/10;
			int newv = 5 + cl + dl + SpellManager.magicka.get(p);
			if(newv > 100) SpellManager.magicka.put(p, 100);
			else SpellManager.magicka.put(p, newv);
		}
	}

}
