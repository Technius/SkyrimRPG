package me.dbizzzle.SkyrimRPG.spell;

import me.dbizzzle.SkyrimRPG.PlayerData;
import me.dbizzzle.SkyrimRPG.SkyrimRPG;
import me.dbizzzle.SkyrimRPG.skill.Skill;

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
		for(Player p:plugin.getServer().getOnlinePlayers())
		{
			PlayerData pd = plugin.getPlayerManager().getData(p.getName());
			int clevel = pd.getSkillLevel(Skill.CONJURATION);
			int dlevel = pd.getSkillLevel(Skill.DESTRUCTION);
			int cl = clevel/10;
			int dl = dlevel/10;
			int newv = 5 + cl + dl;
			pd.addMagicka(newv);
		}
	}

}
