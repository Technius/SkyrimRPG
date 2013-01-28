package me.dbizzzle.SkyrimRPG.spell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;
import me.dbizzzle.SkyrimRPG.spell.Spell;

import org.bukkit.entity.Player;

public class SpellTimer 
{
	static final int MAX_FIREBALL = 250;
	private List<Player> fireballcharge = new ArrayList<Player>();
	private List<Player> fireballcharged = new ArrayList<Player>();
	static HashMap<Player, Integer> fballstart = new HashMap<Player,Integer>();
	private SkyrimRPG p;
	public SpellTimer(SkyrimRPG p)
	{
		this.p = p;
	}
	public List<Player> chargingFireballs()
	{
		return fireballcharge;
	}
	public List<Player> chargedFireballs()
	{
		return fireballcharged;
	}
	public void chargeFireball(Player player)
	{
		player.sendMessage("Charging Fireball...");
		p.getPlayerManager().getData(player.getName()).subtractMagicka(100);
		if(fireballcharge.contains(player))return;
		fireballcharge.add(player);
		p.getServer().getScheduler().scheduleSyncDelayedTask(p, new SpellRunnable(p.getSpellTimer(),Spell.FIREBALL, player), 50);
		fballstart.put(player, Integer.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND) + (Calendar.getInstance().get(Calendar.SECOND)*1000)));
	}
	public int unchargeFireball(Player player)
	{
		if(fireballcharge.contains(player))
		{
			int m = ((Calendar.getInstance().get(Calendar.MILLISECOND) + (Calendar.getInstance().get(Calendar.SECOND)*1000)) - fballstart.get(player).intValue())/10;
			fireballcharge.remove(player);
			return m;
		}
		if(fireballcharged.contains(player)) 
		{
			fireballcharged.remove(player);
			return 250;
		}
		return -1;
	}
}
