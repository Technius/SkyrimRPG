package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.SpellManager.Spell;

import org.bukkit.entity.Player;

public class SpellRunnable implements Runnable
{
	private SpellTimer t;
	private Spell spell;
	Player p;
	public SpellRunnable(SpellTimer timer, Spell spell, Player player)
	{
		p = player;
		this.spell = spell;
		t = timer;
	}
	public void run()
	{
		switch(spell)
		{
		case FIREBALL:
			if(t.chargingFireballs().contains(p))
			{
				t.chargedFireballs().add(p);
				p.sendMessage("Fireball charged!");
			}
			t.chargingFireballs().remove(p);
			break;
		}
	}
}