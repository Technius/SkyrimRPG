package me.dbizzzle.SkyrimRPG;

import org.bukkit.entity.Player;

public class SpellRunnable implements Runnable
{
	int type = 0;
	Player p;
	public SpellRunnable(String spell, Player player)
	{
		p = player;
		if(spell.equalsIgnoreCase("fireball"))
		{
			type = 1;
		}
	}
	public void run()
	{
		switch(type)
		{
		case 1:
			if(SpellTimer.fireballcharge.contains(p)) SpellTimer.fireballcharged.add(p);
			SpellTimer.fireballcharge.remove(p);
			break;
		}
	}
}