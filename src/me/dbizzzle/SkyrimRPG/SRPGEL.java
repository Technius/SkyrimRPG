package me.dbizzzle.SkyrimRPG;

import java.util.List;
import java.util.Random;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
import net.minecraft.server.EntityPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class SRPGEL extends EntityListener
{
	public void onEntityTarget(EntityTargetEvent event)
	{
		if(event.getEntity() instanceof Zombie)
		{
			Zombie z = (Zombie)event.getEntity();
			if(!SpellManager.czombie.containsValue(z))return;
			if(!(event.getTarget() instanceof Player))return;
			Player player = (Player)event.getTarget();
			if(!SpellManager.czombie.containsKey(player))return;
			if(SpellManager.czombie.get(player) != z)return;
			event.setCancelled(true);
		}
		else if(event.getEntity() instanceof Blaze)
		{
			Blaze z = (Blaze)event.getEntity();
			if(!SpellManager.conjured.containsValue(z))return;
			if(!(event.getTarget() instanceof Player))return;
			Player player = (Player)event.getTarget();
			if(!SpellManager.conjured.containsKey(player))return;
			if(SpellManager.conjured.get(player) != z)return;
			event.setCancelled(true);
		}
	}
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(event.getEntity() instanceof Zombie)
		{
			Zombie z = (Zombie)event.getEntity();
			if(!SpellManager.czombie.containsValue(z))return;
			SpellManager.czombie.remove(z);
		}
		else if(event.getEntity() instanceof Blaze)
		{
			Blaze z = (Blaze)event.getEntity();
			if(!SpellManager.conjured.containsValue(z))return;
			SpellManager.conjured.remove(z);
			event.getDrops().clear();
		}
	}
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			if((e.getDamager() instanceof Arrow))
			{
				Arrow a = (Arrow)e.getDamager();
				if(a.getShooter() instanceof Player)
				{
					SkillManager sm = new SkillManager();
					Player shooter = (Player)a.getShooter();
					int alevel = SkillManager.getSkillLevel("Archery", shooter);
					e.setDamage(e.getDamage() + (alevel/10));
					if(sm.processExperience(shooter, "Archery"))
					{
						sm.incrementLevel("Archery", shooter);
						SkillManager.progress.get(shooter).put("Archery", 0);
						SkillManager.calculateLevel(shooter);
					}
					else SkillManager.progress.get(shooter).put("Archery", SkillManager.progress.get(shooter).get("Archery") + 1);
					//debug message:  shooter.sendMessage("Progress:" + pro + "/" + t);
				}
				else return;
			}
			else if(e.getDamager() instanceof Player)
			{
				SkillManager sm = new SkillManager();
				Player player = (Player)e.getDamager();
				if(SpellManager.czombie.containsKey(player))
				{
					Zombie z = SpellManager.czombie.get(player);
					if(!z.isDead())
					{
						if(e.getEntity() instanceof LivingEntity)z.setTarget((LivingEntity)e.getEntity());
					}
				}
				if(SpellManager.conjured.containsKey(player))
				{
					Blaze z = (Blaze)SpellManager.conjured.get(player);
					if(!z.isDead())
					{
						if(e.getEntity() instanceof LivingEntity)z.setTarget((LivingEntity)e.getEntity());
						if(e.getEntity().getEntityId() == z.getEntityId())z.setTarget(player);
					}
				}
				String t = ToolComparer.getType(player.getItemInHand());
				if(t == null) return;
				if(t.equalsIgnoreCase("Sword"))
				{
					int alevel = SkillManager.getSkillLevel("Swordsmanship", player);
					e.setDamage(e.getDamage() + (alevel/10));
					if(sm.processExperience(player, "Swordsmanship"))
					{
						sm.incrementLevel("Swordsmanship", player);
						SkillManager.progress.get(player).put("Swordsmanship", 0);
						SkillManager.calculateLevel(player);
					}
					else SkillManager.progress.get(player).put("Swordsmanship", SkillManager.progress.get(player).get("Swordsmanship") + 1);
				}
				else if(t.equalsIgnoreCase("Axe"))
				{
					int alevel = SkillManager.getSkillLevel("Axecraft", player);
					int crit = new Random().nextInt(99);
					e.setDamage(e.getDamage() + (alevel/10) * crit<=alevel ? 2 : 1);
					if(sm.processExperience(player, "Axecraft"))
					{
						sm.incrementLevel("Axecraft", player);
						SkillManager.progress.get(player).put("Axecraft", 0);
						SkillManager.calculateLevel(player);
					}
					else SkillManager.progress.get(player).put("Axecraft", SkillManager.progress.get(player).get("Axecraft") + 1);
				}
			}
			if(e.getEntity() instanceof Player)
			{
				Player player = (Player)e.getEntity();
				EntityPlayer ep = ((CraftPlayer)player).getHandle();
				boolean ib = ep.K();
				if(ib)
				{
					SkillManager sm = new SkillManager();
					int alevel = SkillManager.getSkillLevel("Blocking", player);
					e.setDamage(e.getDamage() - (alevel/10));
					if(sm.processExperience(player, "Blocking"))
					{
						sm.incrementLevel("Blocking", player);
						SkillManager.progress.get(player).put("Blocking", 0);
						SkillManager.calculateLevel(player);
					}
					else SkillManager.progress.get(player).put("Blocking", SkillManager.progress.get(player).get("Blocking") + 1);
				}
			}
		}
		else
		{
			
		}
	}
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}
	public void onExplosionPrime(ExplosionPrimeEvent event)
	{
		if(!(event.getEntity() instanceof Fireball))return;
		Fireball f = (Fireball)event.getEntity();
		if(!SpellManager.ftracker.contains(f))return;
		if(!(f.getShooter() instanceof Player))return;
		Player p = (Player)f.getShooter();
		List<Entity> tod = f.getNearbyEntities(f.getYield(), f.getYield(), f.getYield());
		event.setCancelled(true);
		f.setYield(0);
		int sp = 0;
		int alevel = SkillManager.getSkillLevel("Destruction", p);
		for(Entity x:tod)
		{
			if(!(x instanceof LivingEntity))continue;
			LivingEntity l = (LivingEntity) x;
			l.damage(7 + (alevel/10));
			l.getWorld().createExplosion(f.getLocation(), 0);
			l.setFireTicks(60);
			sp = sp+1;
		}
		SkillManager sm = new SkillManager();
		if(sm.processExperience(p, "Destruction"))
		{
			sm.incrementLevel("Destruction", p);
			SkillManager.progress.get(p).put("Destruction", 0);
		}
		else SkillManager.progress.get(p).put("Destruction", SkillManager.progress.get(p).get("Destruction") + sp);
		SpellManager.ftracker.remove(f);
	}
}
