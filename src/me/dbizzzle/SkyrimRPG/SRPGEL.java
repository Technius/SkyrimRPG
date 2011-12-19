package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class SRPGEL extends EntityListener
{
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			if(!(e.getDamager() instanceof Arrow))return;
			Arrow a = (Arrow)e.getDamager();
			if(a.getShooter() instanceof Player)
			{
				SkillManager sm = new SkillManager();
				Player shooter = (Player)a.getServer();
				int alevel = SkillManager.getSkillLevel("Archery", shooter);
				e.setDamage(e.getDamage() + (alevel/10));
				sm.incrementLevel("Archery", shooter);
			}
			else return;
		}
		else
		{
			
		}
	}
}
