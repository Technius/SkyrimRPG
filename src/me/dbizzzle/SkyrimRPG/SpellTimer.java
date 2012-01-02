package me.dbizzzle.SkyrimRPG;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

public class SpellTimer 
{
	static final int MAX_FIREBALL = 250;
	static List<Player> fireballcharge = new ArrayList<Player>();
	static List<Player> fireballcharged = new ArrayList<Player>();
	static HashMap<Player, Integer> fballstart = new HashMap<Player,Integer>();
	private SkyrimRPG p;
	public SpellTimer(SkyrimRPG p)
	{
		this.p = p;
	}
	public void chargeFireball(Player player)
	{
		if(!p.sm.hasEnough(player, 30))
		{
			p.sm.magickaWarning(player, "Fireball");
			return;
		}
		else
		{
			SpellManager.magicka.put(player, SpellManager.magicka.get(player) - 30);
			if(fireballcharge.contains(player))return;
			fireballcharge.add(player);
			p.getServer().getScheduler().scheduleSyncDelayedTask(p, new SpellRunnable("fireball", player), 50);
			fballstart.put(player, Integer.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND) + (Calendar.getInstance().get(Calendar.SECOND)*1000)));
		}
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
