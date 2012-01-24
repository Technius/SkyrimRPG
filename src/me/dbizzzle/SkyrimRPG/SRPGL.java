package me.dbizzzle.SkyrimRPG;

import java.util.List;
import java.util.Random;

import me.dbizzzle.SkyrimRPG.DiseaseManager.Disease;
import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.PerkManager;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
import net.minecraft.server.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;

public class SRPGL implements Listener
{
	public SkyrimRPG plugin;

	public SRPGL(SkyrimRPG p) {
		plugin = p;
	}
	
	int secondsDelay = 20; //This will be configurable, I just set 20 for now
	long delay = secondsDelay*20;

	String pickpocketed = ChatColor.RED + "Somebody has pickpocketed you!"; //Configurable
	@EventHandler(event = PlayerInteractEvent.class, priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		boolean disable = false;
		/**
		if(event.getPlayer().getItemInHand().getType() == Material.BLAZE_ROD)
		{
			if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				plugin.st.chargeFireball(event.getPlayer());
				event.getPlayer().sendMessage("Charging fireball...");
			}	
			else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				int m = plugin.st.unchargeFireball(event.getPlayer());
				if(m == -1) return;
				plugin.sm.shootFireball(event.getPlayer(), m);
				event.getPlayer().sendMessage("Fireball shot!");
			}
		}
		**/
		if (event.getPlayer().getItemInHand().getType() == Material.REDSTONE_TORCH_ON && disable)
		{
			//disabling lockpicking for alpha release
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if (event.getClickedBlock().getType() == Material.IRON_DOOR)
				{
					Player s = event.getPlayer();
					Inventory inv = s.getInventory();
					if (inv.contains(Material.IRON_INGOT))
					{
						if (pickLockSuccess(s, event.getClickedBlock().getLocation()))
						{
							SkillManager sm = new SkillManager();
							if (sm.processExperience(s, "Lockpicking"))
							{
								sm.incrementLevel("Lockpicking", s);
								SkillManager.progress.get(s).put("Lockpicking", 0);
							}
							else SkillManager.progress.get(s).put("Lockpicking", SkillManager.progress.get(s).get("Lockpicking") + 1);
							s.sendMessage(ChatColor.GREEN + "You picked the lock successfully!");
							Door d = (Door) event.getClickedBlock().getState();
							d.setOpen(true);
						}
						else
						{
							int alevel = SkillManager.getSkillLevel("Lockpicking", s);
							Random r = new Random();
							int calc = 20 - (alevel/10);
							if(r.nextInt(calc + 1) < alevel/10)
							{
								s.sendMessage(ChatColor.RED + "You failed at picking the lock, and one of your lockpicks broke!");
								for(ItemStack i:inv.getContents())
								{
									if(i.getType() != Material.IRON_INGOT)continue;
									i.setAmount(i.getAmount() - 1);
									break;
								}
							}
							else s.sendMessage(ChatColor.RED + "You failed at picking the lock!");
						}
					}
				}
			}
		}
		else if(event.getPlayer().getItemInHand().getType() == Material.AIR)
		{
			if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				if(SpellManager.boundleft.containsKey(event.getPlayer()))
				{
					plugin.sm.castSpell(SpellManager.boundleft.get(event.getPlayer()), event.getPlayer());
				}
			}
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(SpellManager.boundright.containsKey(event.getPlayer()))
				{
				plugin.sm.castSpell(SpellManager.boundright.get(event.getPlayer()), event.getPlayer());
				}
			}
		}
		else if(event.getPlayer().getItemInHand().getType() == Material.BOOK)
		{
			if(!ConfigManager.useSpellbooks)return;
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				ItemStack b = event.getPlayer().getItemInHand();
				if(b.getDurability() != 0)
				{
					plugin.sm.useBook(event.getPlayer(), b.getDurability());
				}
			}
		}
}

	@EventHandler(event = PlayerInteractEntityEvent.class, priority = EventPriority.LOW)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player se = event.getPlayer();
		final EntityPlayer s = ((CraftPlayer) event.getPlayer()).getHandle();
		if (s.isSneaking()) {
			Entity ent = event.getRightClicked();
			if (ent instanceof Player) {
				final String ents = ((HumanEntity) ent).getName();
				EntityPlayer pick = ((CraftPlayer) plugin.getServer().getPlayer(ents)).getHandle();
				s.a(pick.inventory);
				se.sendMessage(ChatColor.GREEN + "You have succesfully pickpocketed " + ents + "!");

				SkillManager sm = new SkillManager();
				if (sm.processExperience(se, "PickPocket")) {
					sm.incrementLevel("PickPocket", se);
					SkillManager.progress.get(se).put("PickPocket", 0);
					SkillManager.calculateLevel(se);
				} else {
					SkillManager.progress.get(se).put("PickPocket", SkillManager.progress.get(se).get("PickPocket") + 1);
				}

				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						Player picked = plugin.getServer().getPlayer(ents);
						if(!picked.isOnline())return;
						picked.sendMessage(pickpocketed);
						picked.updateInventory();
						s.closeInventory();
					}
				}, delay);
			}
		}
	}
	@EventHandler(event = PlayerJoinEvent.class, priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		SkillManager sm = new SkillManager(plugin);
		sm.loadData(event.getPlayer());
	}
	@EventHandler(event = PlayerQuitEvent.class, priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		SkillManager sm = new SkillManager();
		sm.setPlugin(plugin);
		sm.saveData(event.getPlayer());
	}

	public boolean pickLockSuccess(Player pla, Location loc)
	{
		if (loc.getBlock().getType() == Material.IRON_DOOR || loc.getBlock().getType() == Material.CHEST)
		{
			int alevel = SkillManager.getSkillLevel("LockPick", pla);
			Random r = new Random();
			int calc = 10 - (alevel/10);
			if (r.nextInt(calc + 1) < alevel/10)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return false;
	}	
	@EventHandler(event = EntityTargetEvent.class, priority = EventPriority.HIGH)
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
	@EventHandler(event = EntityDeathEvent.class, priority = EventPriority.HIGH)
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
	@EventHandler(event = EntityDamageEvent.class, priority = EventPriority.HIGH)
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
					double perkm = 1;
					if(PerkManager.perks.get(shooter).containsKey(Perk.OVERDRAW))
					{
						perkm = 1 + (0.2*(PerkManager.perks.get(shooter).get(Perk.OVERDRAW)));
					}
					e.setDamage((int)((e.getDamage() + (alevel/10))*(1 + perkm)));
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
					double perkm = 1;
					if(PerkManager.perks.get(player).containsKey(Perk.SWORDSMAN))
					{
						perkm = 1 + (0.2*(PerkManager.perks.get(player).get(Perk.SWORDSMAN)));
					}
					if(PerkManager.perks.get(player).containsKey(Perk.BLADESMAN))
					{
						int a = new Random().nextInt(100);
						if(a < 5 + (5*PerkManager.perks.get(player).get(Perk.BLADESMAN)))perkm = perkm + 0.25 + (PerkManager.perks.get(player).get(Perk.BLADESMAN)*0.83);
					}
					e.setDamage((int)(e.getDamage() + (alevel/10)*perkm));
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
					e.setDamage(e.getDamage() + (alevel/10) * (crit<=alevel ? 2 : 1));
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
				boolean ib = ep.L();
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
			else if(e.getDamager() instanceof Zombie)
			{
				if(e.getEntity() instanceof Player)
				{
					double a;
					a = Math.random();
					if(a > 80)
					{
						Player p = (Player) e.getEntity();
						DiseaseManager.addDisease(p, Disease.BRAIN_ROT);
						p.sendMessage(ChatColor.GRAY + "The zombie has given you brain rot!");
					}
				}
			}
		}
	}
	@EventHandler(event = FoodLevelChangeEvent.class, priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}
	@EventHandler(event = ExplosionPrimeEvent.class, priority = EventPriority.HIGHEST)
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
