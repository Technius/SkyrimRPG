package me.dbizzzle.SkyrimRPG;

import java.util.List;
import java.util.Random;

import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.PerkManager;
import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
import net.minecraft.server.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event.getPlayer().getItemInHand().getType() == Material.REDSTONE_TORCH_ON)
		{
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				Material btype = event.getClickedBlock().getType();
				if (btype == Material.IRON_DOOR_BLOCK)
				{
					Door d = (Door) btype.getNewData(event.getClickedBlock().getData());
					if(!d.isOpen())
					{
						SkillManager sm = new SkillManager();
						if(pickLockSuccess(event.getPlayer()))
						{
							d.setOpen(true);
							org.bukkit.block.Block above = event.getClickedBlock().getRelative(BlockFace.UP);
	                        org.bukkit.block.Block below = event.getClickedBlock().getRelative(BlockFace.DOWN);
							if(d.isTopHalf())
							{
								d.setOpen(true);
								event.getClickedBlock().setData(d.getData(),true);
								d.setOpen(true);
								d.setTopHalf(false);
								below.setData(d.getData(), true);
							}
							else
							{
								d.setOpen(true);
								event.getClickedBlock().setData(d.getData(),true);
								d.setOpen(true);
								d.setTopHalf(true);
								above.setData(d.getData(), true);
							}
							event.getPlayer().sendMessage(ChatColor.GREEN + "Lockpicking success!");
							if(event.isCancelled())event.setCancelled(false);
							event.getClickedBlock().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.DOOR_TOGGLE, 0);
						}
						else if(new Random().nextInt(100) + 1 > SkillManager.getSkillLevel(Skill.LOCKPICKING, event.getPlayer())/2 + 10)
						{
							int newa = event.getPlayer().getItemInHand().getAmount() - 1;
							event.getPlayer().setItemInHand(new org.bukkit.inventory.ItemStack(Material.REDSTONE_TORCH_ON, newa));
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed, and your lock pick broke.");
						}
						else
						{
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed!");
						}
						if (sm.processExperience(event.getPlayer(), Skill.LOCKPICKING)) {
							sm.incrementLevel(Skill.LOCKPICKING, event.getPlayer());
							SkillManager.progress.get(event.getPlayer()).put(Skill.LOCKPICKING, 0);
							SkillManager.calculateLevel(event.getPlayer());
						} else {
							SkillManager.progress.get(event.getPlayer()).put(Skill.LOCKPICKING, SkillManager.progress.get(event.getPlayer()).get(Skill.LOCKPICKING) + 1);
						}
					}
				}
				else if(btype == Material.CHEST)
				{
					if(event.isCancelled())
					{
						SkillManager sm = new SkillManager();
						if(pickLockSuccess(event.getPlayer()))
						{
							net.minecraft.server.TileEntityChest c = (net.minecraft.server.TileEntityChest)((org.bukkit.craftbukkit.CraftWorld)event.getPlayer().getWorld()).getTileEntityAt(event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());
							c.a((net.minecraft.server.EntityHuman)((CraftPlayer)event.getPlayer()).getHandle());
							event.getPlayer().sendMessage(ChatColor.GREEN + "Lockpicking success!");
							if(event.isCancelled())event.setCancelled(false);
						}
						else if(new Random().nextInt(100) + 1 > SkillManager.getSkillLevel(Skill.LOCKPICKING, event.getPlayer())/2 + 10)
						{
							event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed, and your lock pick broke.");
						}
						else
						{
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed!");
						}
						if (sm.processExperience(event.getPlayer(), Skill.LOCKPICKING)) {
							sm.incrementLevel(Skill.LOCKPICKING, event.getPlayer());
							SkillManager.progress.get(event.getPlayer()).put(Skill.LOCKPICKING, 0);
							SkillManager.calculateLevel(event.getPlayer());
						} else {
							SkillManager.progress.get(event.getPlayer()).put(Skill.LOCKPICKING, SkillManager.progress.get(event.getPlayer()).get(Skill.LOCKPICKING) + 1);
						}
					}
				}
				event.getPlayer().updateInventory();
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
					if(plugin.sm.useBook(event.getPlayer(), b.getDurability()))
					{
						b.setAmount(b.getAmount() - 1);
						event.getPlayer().setItemInHand(b);
					}
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
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
				if (sm.processExperience(se, Skill.PICKPOCKETING)) {
					sm.incrementLevel(Skill.PICKPOCKETING, se);
					SkillManager.progress.get(se).put(Skill.PICKPOCKETING, 0);
					SkillManager.calculateLevel(se);
				} else {
					SkillManager.progress.get(se).put(Skill.PICKPOCKETING, SkillManager.progress.get(se).get(Skill.PICKPOCKETING) + 1);
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
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		SkillManager sm = new SkillManager(plugin);
		sm.loadData(event.getPlayer());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		SkillManager sm = new SkillManager();
		sm.setPlugin(plugin);
		sm.saveData(event.getPlayer());
	}

	public boolean pickLockSuccess(Player pla)
	{
		int alevel = SkillManager.getSkillLevel(Skill.PICKPOCKETING, pla);
		Random r = new Random();
		int calc = r.nextInt(100) + 1;
		if(calc < (alevel/2 + 10))return true;
		return false;
	}	
	@EventHandler(priority = EventPriority.HIGH)
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
	@EventHandler(priority = EventPriority.HIGH)
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
	@EventHandler(priority = EventPriority.HIGH)
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
					int alevel = SkillManager.getSkillLevel(Skill.ARCHERY, shooter);
					double perkm = 1;
					if(PerkManager.perks.get(shooter).containsKey(Perk.OVERDRAW))
					{
						perkm = 1 + (0.2*(PerkManager.perks.get(shooter).get(Perk.OVERDRAW)));
					}
					e.setDamage((int)((e.getDamage() + (alevel/10))*(1 + perkm)));
					if(sm.processExperience(shooter, Skill.ARCHERY))
					{
						sm.incrementLevel(Skill.ARCHERY, shooter);
						SkillManager.progress.get(shooter).put(Skill.ARCHERY, 0);
						SkillManager.calculateLevel(shooter);
					}
					else SkillManager.progress.get(shooter).put(Skill.ARCHERY, SkillManager.progress.get(shooter).get(Skill.ARCHERY) + 1);
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
					int alevel = SkillManager.getSkillLevel(Skill.SWORDSMANSHIP, player);
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
					if(sm.processExperience(player, Skill.SWORDSMANSHIP))
					{
						sm.incrementLevel(Skill.SWORDSMANSHIP, player);
						SkillManager.progress.get(player).put(Skill.SWORDSMANSHIP, 0);
						SkillManager.calculateLevel(player);
					}
					else SkillManager.progress.get(player).put(Skill.SWORDSMANSHIP, SkillManager.progress.get(player).get(Skill.SWORDSMANSHIP) + 1);
				}
				else if(t.equalsIgnoreCase("Axe"))
				{
					int alevel = SkillManager.getSkillLevel(Skill.AXECRAFT, player);
					int crit = new Random().nextInt(99);
					e.setDamage(e.getDamage() + (alevel/10) * (crit<=alevel ? 2 : 1));
					if(sm.processExperience(player, Skill.AXECRAFT))
					{
						sm.incrementLevel(Skill.AXECRAFT, player);
						SkillManager.progress.get(player).put(Skill.AXECRAFT, 0);
						SkillManager.calculateLevel(player);
					}
					else SkillManager.progress.get(player).put(Skill.AXECRAFT, SkillManager.progress.get(player).get(Skill.AXECRAFT) + 1);
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
					int alevel = SkillManager.getSkillLevel(Skill.BLOCKING, player);
					e.setDamage(e.getDamage() - (alevel/10));
					if(sm.processExperience(player, Skill.BLOCKING))
					{
						sm.incrementLevel(Skill.BLOCKING, player);
						SkillManager.progress.get(player).put(Skill.BLOCKING, 0);
						SkillManager.calculateLevel(player);
					}
					else SkillManager.progress.get(player).put(Skill.BLOCKING, SkillManager.progress.get(player).get(Skill.BLOCKING) + 1);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.HIGHEST)
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
		int alevel = SkillManager.getSkillLevel(Skill.DESTRUCTION, p);
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
		if(sm.processExperience(p, Skill.DESTRUCTION))
		{
			sm.incrementLevel(Skill.DESTRUCTION, p);
			SkillManager.progress.get(p).put(Skill.DESTRUCTION, 0);
		}
		else SkillManager.progress.get(p).put(Skill.DESTRUCTION, SkillManager.progress.get(p).get(Skill.DESTRUCTION) + sp);
		SpellManager.ftracker.remove(f);
	}
}
