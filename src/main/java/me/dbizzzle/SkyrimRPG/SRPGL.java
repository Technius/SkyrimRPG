package me.dbizzzle.SkyrimRPG;

import static me.dbizzzle.SkyrimRPG.Skill.SkillUtil.processExperience;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.event.PlayerLockpickEvent;
import me.dbizzzle.SkyrimRPG.event.PlayerPickpocketEvent;
import me.dbizzzle.SkyrimRPG.spell.Spell;
import me.dbizzzle.SkyrimRPG.spell.SpellUtil;
import me.dbizzzle.SkyrimRPG.util.MetadataHandler;
import me.dbizzzle.SkyrimRPG.util.ToolComparer;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.material.TrapDoor;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SRPGL implements Listener
{
	public SkyrimRPG plugin;

	public SRPGL(SkyrimRPG p) {
		plugin = p;
	}
	
	int secondsDelay = 20; //This will be configurable, I just set 20 for now
	long delay = secondsDelay*20;

	String pickpocketed = ChatColor.RED + "Somebody has pickpocketed you!"; //Configurable
	ArrayList<Player> sneak = new ArrayList<Player>();
	CopyOnWriteArrayList<Player> ppcd = new CopyOnWriteArrayList<Player>();
	CopyOnWriteArrayList<Player> lpcd = new CopyOnWriteArrayList<Player>();
	public void clearData()
	{
		ppcd.clear();
		lpcd.clear();
		sneak.clear();
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(plugin.getConfigManager().disabledWorlds.contains(event.getPlayer().getWorld()))return;
		PlayerData pd = plugin.getPlayerManager().getData(event.getPlayer().getName());
		if(event.getPlayer().getItemInHand().getType() == Material.REDSTONE_TORCH_ON && plugin.getConfigManager().enableLockpicking)
		{
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				Material btype = event.getClickedBlock().getType();
				if (btype == Material.IRON_DOOR_BLOCK || (btype == Material.WOOD_DOOR && event.isCancelled()))
				{
					if(lpcd.contains(event.getPlayer()) && plugin.getConfigManager().enableLockpickingCooldown)
					{
						event.getPlayer().sendMessage(ChatColor.RED + "You don't feel comfortable attempting to pick locks right now");
						return;
					}
					Door d = (Door) btype.getNewData(event.getClickedBlock().getData());
					if(!d.isOpen())
					{
						boolean initial = pickLockSuccess(event.getPlayer());
						PlayerLockpickEvent cevent = new PlayerLockpickEvent(event.getPlayer(), event.getClickedBlock(), initial);
						event.getPlayer().getServer().getPluginManager().callEvent(cevent);
						if(cevent.isCancelled())return;
						if(cevent.isSuccessful())
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
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=success, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						else if(new Random().nextInt(100) + 1 > pd.getSkillLevel(Skill.LOCKPICKING)/2 + 10)
						{
							int newa = event.getPlayer().getItemInHand().getAmount() - 1;
							event.getPlayer().setItemInHand(new org.bukkit.inventory.ItemStack(Material.REDSTONE_TORCH_ON, newa));
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed, and your lock pick broke.");
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=fail+break, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						else
						{
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed!");
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=fail, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						processExperience(pd, event.getPlayer(), Skill.LOCKPICKING, 1, plugin);
					}
					event.getPlayer().getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(Skill.LOCKPICKING, event.getPlayer(), false), plugin.getConfigManager().LockpickingCooldown);
				}
				else if(btype == Material.TRAP_DOOR)
				{
					TrapDoor d = (TrapDoor)btype.getNewData(event.getClickedBlock().getData());
					if(!d.isOpen())
					{
						if(pickLockSuccess(event.getPlayer()))
						{
							d.setOpen(true);
							event.getClickedBlock().setData(d.getData(), true);
							event.getPlayer().sendMessage(ChatColor.GREEN + "Lockpicking success!");
							if(event.isCancelled())event.setCancelled(false);
							event.getClickedBlock().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.DOOR_TOGGLE, 0);
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=success, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						else if(new Random().nextInt(100) + 1 > pd.getSkillLevel(Skill.LOCKPICKING)/2 + 10)
						{
							int newa = event.getPlayer().getItemInHand().getAmount() - 1;
							event.getPlayer().setItemInHand(new org.bukkit.inventory.ItemStack(Material.REDSTONE_TORCH_ON, newa));
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed, and your lock pick broke.");
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=fail+break, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						else
						{
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed!");
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=fail, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						processExperience(pd, event.getPlayer(), Skill.LOCKPICKING, 1, plugin);
					}
				}
				else if(btype == Material.CHEST)
				{
					if(event.isCancelled())
					{
						if(lpcd.contains(event.getPlayer()) && plugin.getConfigManager().enableLockpickingCooldown)
						{
							event.getPlayer().sendMessage(ChatColor.RED + "You don't feel comfortable attempting to pick locks right now");
							return;
						}
						if(pickLockSuccess(event.getPlayer()))
						{
							event.getPlayer().openInventory(((Chest)event.getClickedBlock().getState()).getBlockInventory());
							event.getPlayer().sendMessage(ChatColor.GREEN + "Lockpicking success!");
							if(event.isCancelled())event.setCancelled(false);
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=success, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						else if(new Random().nextInt(100) + 1 > pd.getSkillLevel(Skill.LOCKPICKING)/2 + 10)
						{
							event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed, and your lock pick broke.");
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=fail+break, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						else
						{
							event.getPlayer().sendMessage(ChatColor.RED + "Lockpicking failed!");
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=success, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						processExperience(pd, event.getPlayer(), Skill.LOCKPICKING, 1, plugin);
						event.getPlayer().getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(Skill.LOCKPICKING, event.getPlayer(), false));
					}
				}
				return;
			}
		}
		else if(event.getPlayer().getItemInHand().getType().getId() == plugin.getConfigManager().wand)
		{
			if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				Spell s = plugin.getSpellManager().getBoundLeft(event.getPlayer());
				if(s != null)
				{
					event.setCancelled(true);
					if(plugin.getConfigManager().useSpellPermissions && !event.getPlayer().hasPermission("skyrimrpg.spells.*"))
					{
						if(!event.getPlayer().hasPermission("skyrimrpg.spells." + s.getDisplayName()))
						{
							event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to use this spell!");return;
						}
					}
					SpellUtil.castSpell(event.getPlayer(), pd, s);
				}
			}
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				Spell s = plugin.getSpellManager().getBoundRight(event.getPlayer());
				if(s != null)
				{
					event.setCancelled(true);
					if(plugin.getConfigManager().useSpellPermissions && !event.getPlayer().hasPermission("skyrimrpg.spells.*"))
					{
						if(!event.getPlayer().hasPermission("skyrimrpg.spells." + s.getDisplayName()))
						{
							event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to use this spell!");return;
						}
					}
					SpellUtil.castSpell(event.getPlayer(), pd, s);
				}
			}
		}
		else if(event.getPlayer().getItemInHand().getType() == Material.BOOK)
		{
			if(plugin.getConfigManager().useSpellBooks)return;
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				ItemStack b = event.getPlayer().getItemInHand();
				if(b.getDurability() != 0)
				{
					if(SpellUtil.useSpellBook(event.getPlayer(), pd, b.getDurability()))
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
		if(plugin.getConfigManager().disabledWorlds.contains(event.getPlayer().getWorld()))return;
		final Player s = event.getPlayer();
		if (s.isSneaking() && plugin.getConfigManager().enablePickpocketing) {
			Entity ent = event.getRightClicked();
			if (ent instanceof Player) {
				final Player victim = (Player) ent;
				if(ppcd.contains(s) && plugin.getConfigManager().enablePickpocketingCooldown)
				{
					s.sendMessage(ChatColor.RED + "You are too afraid to pickpocket someone right now");
					plugin.debug("Pickpocketing: result=cooldown, player=" + s.getName() + ", " + "target= " + ((Player)ent).getName());
					return;
				}
				if(((Player)ent).hasPermission("skyrimrpg.nopickpocket"))
				{
					s.sendMessage(ChatColor.RED + "You probably don't want to pickpocket this person.");
					plugin.debug("Pickpocketing: result=denied, player=" + s.getName() + ", " + "target= " + victim.getName());
					return;
				}
				Random r = new Random();
				double c = r.nextInt(100) + 1;
				double mul = 1.0;
				PlayerData pds = plugin.getPlayerManager().getData(s.getName());
				if(pds.hasPerk(Perk.LIGHT_FINGERS))mul = mul + (0.2*pds.getPerkLevel(Perk.LIGHT_FINGERS));
				mul = mul - (0.25*pds.getSkillLevel(Skill.PICKPOCKETING));
				c = c * mul;
				if(c > pds.getSkillLevel(Skill.PICKPOCKETING) && plugin.getConfigManager().enablePickpocketingChance)
				{
					s.sendMessage(ChatColor.RED + "You have unsucessfully pickpocketed " + victim.getName() + "!");
					victim.sendMessage(ChatColor.RED + s.getName() + " tried to pickpocket you!");
					processExperience(pds, event.getPlayer(), Skill.PICKPOCKETING, 1, plugin);
					plugin.debug("Pickpocketing: result=fail, player=" + s.getName() + ", " + "target= " + ((Player)ent).getName());
					s.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(Skill.PICKPOCKETING, s, false), plugin.getConfigManager().PickpocketingCooldown);
					s.getServer().getPluginManager().callEvent(new PlayerPickpocketEvent(event.getPlayer(), victim, false));
					return;
				}
				PlayerPickpocketEvent e = new PlayerPickpocketEvent(event.getPlayer(), victim, true);
				s.getServer().getPluginManager().callEvent(e);
				if(e.isCancelled())return;
				s.openInventory(victim.getInventory());
				s.sendMessage(ChatColor.GREEN + "You have succesfully pickpocketed " + victim.getName() + "!");
				plugin.debug("Pickpocketing: result=success, player=" + s.getName() + ", " + "target= " + ((Player)ent).getName());
				processExperience(pds, event.getPlayer(), Skill.PICKPOCKETING, 1, plugin);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						if(!victim.isOnline())return;
						victim.sendMessage(pickpocketed);
						victim.updateInventory();
						s.closeInventory();
					}
				}, delay);
				s.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(Skill.PICKPOCKETING, s, false), plugin.getConfigManager().PickpocketingCooldown);
				return;
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(plugin.getPlayerManager().getData(event.getPlayer().getName()) == null)
			plugin.getPlayerManager().addData(new PlayerData(event.getPlayer().getName()));
		if(event.getPlayer().hasPermission("skyrimrpg.newversion") && plugin.getVersionMessage() != null)event.getPlayer().sendMessage(ChatColor.GOLD + "[SkyrimRPG]" + plugin.getVersionMessage());
	}
	public boolean pickLockSuccess(Player pla)
	{
		int alevel = plugin.getPlayerManager().getData(pla.getName()).getSkillLevel(Skill.PICKPOCKETING);
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
			if(!plugin.getSpellManager().czombie.containsValue(z))return;
			if(!(event.getTarget() instanceof Player))return;
			Player player = (Player)event.getTarget();
			if(!plugin.getSpellManager().czombie.containsKey(player))return;
			if(plugin.getSpellManager().czombie.get(player) != z)return;
			event.setCancelled(true);
		}
		else if(event.getEntity() instanceof Blaze)
		{
			Blaze z = (Blaze)event.getEntity();
			if(!plugin.getSpellManager().conjured.containsValue(z))return;
			if(!(event.getTarget() instanceof Player))return;
			Player player = (Player)event.getTarget();
			if(!plugin.getSpellManager().conjured.containsKey(player))return;
			if(plugin.getSpellManager().conjured.get(player) != z)return;
			event.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(event.getEntity() instanceof Zombie)
		{
			Zombie z = (Zombie)event.getEntity();
			if(!plugin.getSpellManager().czombie.containsValue(z))return;
			plugin.getSpellManager().czombie.remove(z);
		}
		else if(event.getEntity() instanceof Blaze)
		{
			Blaze z = (Blaze)event.getEntity();
			if(!plugin.getSpellManager().conjured.containsValue(z))return;
			plugin.getSpellManager().conjured.remove(z);
			event.getDrops().clear();
		}
	}
	public void sneakDetect(PlayerMoveEvent event)
	{
		
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void sneakSkill(PlayerToggleSneakEvent event)
	{
		if(event.isCancelled())return;
		if(plugin.getConfigManager().disabledWorlds.contains(event.getPlayer().getWorld()))return;
		Player player = event.getPlayer();
		PlayerData pd = plugin.getPlayerManager().getData(player.getName());
		boolean detect = false;
		if(event.isSneaking() && !sneak.contains(player))
		{
			List<Entity> a = player.getNearbyEntities(60, 10, 60);
			for(Entity e:a)
			{
				if(e instanceof Player)
				{
					Player t = (Player)e;
					int alevel = pd.getSkillLevel(Skill.SNEAK);
					double dmul = 1.0;
					if(pd.hasPerk(Perk.STEALTH))
					{
						dmul = 1.15 + 0.05*pd.getPerkLevel(Perk.STEALTH);
					}
					double d = e.getLocation().distance(player.getLocation());
					if(d + dmul*(10 + alevel/5) >= 70)
					{
						processExperience(pd, event.getPlayer(), Skill.SNEAK, 1, plugin);
						t.hidePlayer(player);
						plugin.debug("Sneaking: result=hidden, player=" + player.getName());
					}
					else
					{
						detect = true;
						plugin.debug("Sneaking: result=visible, player=" + player.getName());
					}
					sneak.add(player);
				}
			}
			if(!detect && plugin.getConfigManager().enableSneakMessage)player.sendMessage(ChatColor.YELLOW + "Hidden");
			else if(detect && a.size() != 0 && plugin.getConfigManager().enableSneakMessage)player.sendMessage(ChatColor.YELLOW + "Detected");
			else if(plugin.getConfigManager().enableSneakMessage)player.sendMessage(ChatColor.YELLOW + "Hidden");
		}
		else if(!event.isSneaking() && sneak.contains(player))
		{
			if(plugin.getConfigManager().enableSneakMessage)player.sendMessage(ChatColor.YELLOW + "Now visible");
			for(Player p:player.getServer().getOnlinePlayers())
			{
				if(!p.canSee(player))
				{
					p.showPlayer(player);
					plugin.debug("Sneaking: result=reveal, player=" + player.getName());
				}
			}
			sneak.remove(player);
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(event.isCancelled())return;
		if(plugin.getConfigManager().disabledWorlds.contains(event.getEntity().getWorld()))return;
		if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			if(e.getDamager() instanceof Player && e.getEntity() instanceof Player && !event.getEntity().getWorld().getPVP())return;
			if((e.getDamager() instanceof Arrow) && !plugin.getConfigManager().disabledSkills.contains(Skill.ARCHERY))
			{
				Arrow a = (Arrow)e.getDamager();
				if(a.getShooter() instanceof Player)
				{
					Player shooter = (Player)a.getShooter();
					double perkm = 1;
					PlayerData pd = plugin.getPlayerManager().getData(shooter.getName());
					int alevel = pd.getSkillLevel(Skill.ARCHERY);
					if(pd.hasPerk(Perk.OVERDRAW))
					{
						perkm = 1 + (0.2*pd.getPerkLevel(Perk.OVERDRAW));
					}
					e.setDamage((int)((e.getDamage() + (alevel/10))*(1 + perkm)));
					processExperience(pd, shooter, Skill.ARCHERY, 1, plugin);
					//debug message:  shooter.sendMessage("Progress:" + pro + "/" + t);
				}
				else return;
			}
			else if(e.getDamager() instanceof Player)
			{
				Player player = (Player)e.getDamager();
				PlayerData pd = plugin.getPlayerManager().getData(player.getName());
				if(plugin.getSpellManager().czombie.containsKey(player))
				{
					Zombie z = plugin.getSpellManager().czombie.get(player);
					if(!z.isDead())
					{
						if(e.getEntity() instanceof LivingEntity)z.setTarget((LivingEntity)e.getEntity());
					}
				}
				if(plugin.getSpellManager().conjured.containsKey(player))
				{
					Blaze z = (Blaze)plugin.getSpellManager().conjured.get(player);
					if(!z.isDead())
					{
						if(e.getEntity() instanceof LivingEntity)z.setTarget((LivingEntity)e.getEntity());
						if(e.getEntity().getEntityId() == z.getEntityId())z.setTarget(player);
					}
				}
				String t = ToolComparer.getType(player.getItemInHand());
				if(t == null) return;
				if(t.equalsIgnoreCase("Sword") && !plugin.getConfigManager().disabledSkills.contains(Skill.SWORDSMANSHIP))
				{
					int alevel = pd.getSkillLevel(Skill.SWORDSMANSHIP);
					double perkm = 1;
					if(pd.hasPerk(Perk.SWORDSMAN))
					{
						perkm = 1 + (0.2*pd.getPerkLevel(Perk.SWORDSMAN));
					}
					if(pd.hasPerk(Perk.BLADESMAN))
					{
						int a = new Random().nextInt(100);
						if(a < 5 + (5*pd.getPerkLevel( Perk.BLADESMAN)))perkm = perkm + 0.25 + pd.getPerkLevel(Perk.BLADESMAN);
					}
					e.setDamage((int)(e.getDamage() + (alevel/10)*perkm));
					processExperience(pd, player, Skill.SWORDSMANSHIP, 1, plugin);
				}
				else if(t.equalsIgnoreCase("Axe") && !plugin.getConfigManager().disabledSkills.contains(Skill.AXECRAFT))
				{
					int alevel = pd.getSkillLevel(Skill.AXECRAFT);
					int crit = new Random().nextInt(99);
					e.setDamage(e.getDamage() + (alevel/10) * (crit<=alevel ? 2 : 1));
					processExperience(pd, player, Skill.AXECRAFT, 1, plugin);
				}
				if(sneak.contains(player) && event.getEntity() instanceof Player)
				{
					if(!((Player)event.getEntity()).canSee(player))
					{
						event.setDamage(event.getDamage()* 2);
						player.sendMessage(ChatColor.GREEN + "Sneak attack for 2x damage!");
						processExperience(pd, player, Skill.SNEAK, 5, plugin);
					}
				}
			}
			else if(e.getDamager() instanceof SmallFireball)
			{
				SmallFireball sf = (SmallFireball)e.getDamager();
				MetadataValue mv = MetadataHandler.getMetadata(sf, plugin, "flames");
				if(sf.getShooter() instanceof Player && mv != null)
				{
					if(mv.asBoolean())
					{
						Player player = (Player)sf.getShooter();
						PlayerData pd = plugin.getPlayerManager().getData(player.getName());
						int alevel = pd.getSkillLevel(Skill.DESTRUCTION)/20;
						int damage = 1 + alevel;
						e.setDamage(damage);
						processExperience(pd, player, Skill.DESTRUCTION, 1, plugin);
					}
				}
			}
			else if(e.getDamager() instanceof Snowball)
			{
				Snowball sf = (Snowball)e.getDamager();
				MetadataValue mv = MetadataHandler.getMetadata(sf, plugin, "frostbite");
				if(sf.getShooter() instanceof Player && mv != null)
				{
					if(mv.asBoolean())
					{
						Player player = (Player)sf.getShooter();
						PlayerData pd = plugin.getPlayerManager().getData(player.getName());
						int alevel = pd.getSkillLevel(Skill.DESTRUCTION)/30;
						int plevel = pd.getSkillLevel(Skill.DESTRUCTION)/5;
						int damage = 1 + alevel;
						e.setDamage(damage);
						if(event.getEntity() instanceof LivingEntity)
						{
							((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, plevel + 20, 1));
						}
						processExperience(pd, player, Skill.DESTRUCTION, 1, plugin);
					}
				}
			}
			if(e.getEntity() instanceof Player)
			{
				Player player = (Player)e.getEntity();
				PlayerData pd = plugin.getPlayerManager().getData(player.getName());
				if(player.isBlocking() && ToolComparer.getType(player.getItemInHand()).equalsIgnoreCase("Sword") && !plugin.getConfigManager().disabledSkills.contains(Skill.BLOCKING))
				{
					int alevel = pd.getSkillLevel(Skill.BLOCKING);
					double perkm = 1.0;
					if(pd.hasPerk(Perk.SWORD_WALL))
					{
						perkm = 1 + (0.1*pd.getPerkLevel(Perk.SWORD_WALL));
					}
					if(pd.hasPerk(Perk.DEFLECT_ARROWS) && e.getDamager() instanceof Arrow)
					{
						e.setDamage(0);
						plugin.debug("Blocking: player=" + player.getName() + ", damage=" + event.getDamage() + ", blocked=DEFLECT_ARROWS PERK");
					}
					else plugin.debug("Blocking: player=" + player.getName() + ", damage=" + event.getDamage() + ", blocked=" + ((int)(perkm*(alevel/10))));
					e.setDamage(e.getDamage() - ((int)(perkm*(alevel/10))));
					processExperience(pd, player, Skill.BLOCKING, 1, plugin);
				}
				double red;
				if((red = getDamageReduced(player)) > 0 && !plugin.getConfigManager().disabledSkills.contains(Skill.ARMOR))
				{
					int a = armorCount(player);
					double alevel = pd.getSkillLevel(Skill.ARMOR);
					if(alevel <= 0)alevel = 0.01;
					else alevel = alevel/100;
					processExperience(pd, player, Skill.ARMOR, a, plugin);
					double d = red*alevel*1.25;
					//0.5 full diamond at level 84: 0.75
					//0.5 full diamond at level 50: 1.25
					if(d > 0.5)d = 0.5;
					plugin.debug("Armor: player=" + player.getName() + ", damage=" + e.getDamage() + ", blocked%=" + (d*100));
					e.setDamage((int) (event.getDamage() - event.getDamage()*d));
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplosionPrime(ExplosionPrimeEvent event)
	{
		if(!(event.getEntity() instanceof Fireball))return;
		Fireball f = (Fireball)event.getEntity();
		MetadataValue mv = MetadataHandler.getMetadata(f, plugin, "fireball");
		if(mv == null)return;
		if(!mv.asBoolean())return;
		if(!(f.getShooter() instanceof Player))return;
		Player p = (Player)f.getShooter();
		float yield = f.getYield();
		List<Entity> tod = f.getNearbyEntities(yield, yield, yield);
		f.setYield(0);
		event.setCancelled(true);
		int sp = 0;
		PlayerData pd = plugin.getPlayerManager().getData(p.getName());
		int alevel = pd.getSkillLevel(Skill.DESTRUCTION);
		event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0F);
		for(Entity x:tod)
		{
			if(!(x instanceof LivingEntity))continue;
			LivingEntity l = (LivingEntity) x;
			if(!l.hasLineOfSight(f))continue;
			double dist = event.getEntity().getLocation().distance(x.getLocation());
			if(dist > 5)continue;
			double scale = (5 - dist)/5;
			int damage = (int)(scale*(7 + (alevel/10)));
			if(damage == 0)continue;
			l.damage(damage, p);
			l.setFireTicks(60);
			sp = sp+1;
		}
		processExperience(pd, p, Skill.DESTRUCTION, sp, plugin);
	}
	private class Cooldown implements Runnable
	{
		private Skill skill;
		private Player p;
		private boolean r;
		private Cooldown(Skill s, Player player, boolean remove)
		{
			skill = s;
			p = player;
			r = remove;
		}
		public void run() 
		{
			if(!r)
			{
				int cd = 0;
				if(skill == Skill.PICKPOCKETING)
				{
					ppcd.add(p);
					cd = plugin.getConfigManager().PickpocketingCooldown;
				}
				else if(skill == Skill.LOCKPICKING)
				{
					lpcd.add(p);
					cd = plugin.getConfigManager().LockpickingCooldown;
				}
				p.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(skill, p, true), cd);
			}
			else
			{
				if(skill == Skill.PICKPOCKETING)ppcd.remove(p);
				else if(skill == Skill.LOCKPICKING)lpcd.remove(p);
			}
		}
	}
	public double getDamageReduced(Player player)
	{
		org.bukkit.inventory.PlayerInventory inv = player.getInventory();
	    ItemStack boots = inv.getBoots();
	    ItemStack helmet = inv.getHelmet();
	    ItemStack chest = inv.getChestplate();
	    ItemStack pants = inv.getLeggings();
	    double red = 0.0;
	    if(helmet != null)
	    {
		    if(helmet.getType() == Material.LEATHER_HELMET)red = red + 0.04;
		    else if(helmet.getType() == Material.GOLD_HELMET)red = red + 0.08;
		    else if(helmet.getType() == Material.CHAINMAIL_HELMET)red = red + 0.08;
		    else if(helmet.getType() == Material.IRON_HELMET)red = red + 0.08;
		    else if(helmet.getType() == Material.DIAMOND_HELMET)red = red + 0.12;
	    }
	    if(boots != null)
	    {
		    if(boots.getType() == Material.LEATHER_BOOTS)red = red + 0.04;
		    else if(boots.getType() == Material.GOLD_BOOTS)red = red + 0.04;
		    else if(boots.getType() == Material.CHAINMAIL_BOOTS)red = red + 0.04;
		    else if(boots.getType() == Material.IRON_BOOTS)red = red + 0.08;
		    else if(boots.getType() == Material.DIAMOND_BOOTS)red = red + 0.12;
	    }
	    if(pants != null)
	    {
		    if(pants.getType() == Material.LEATHER_LEGGINGS)red = red + 0.08;
		    else if(pants.getType() == Material.GOLD_LEGGINGS)red = red + 0.12;
		    else if(pants.getType() == Material.CHAINMAIL_LEGGINGS)red = red + 0.16;
		    else if(pants.getType() == Material.IRON_LEGGINGS)red = red + 0.20;
		    else if(pants.getType() == Material.DIAMOND_LEGGINGS)red = red + 0.24;
	    }
	    if(chest != null)
	    {
		    if(chest.getType() == Material.LEATHER_CHESTPLATE)red = red + 0.12;
		    else if(chest.getType() == Material.GOLD_CHESTPLATE)red = red + 0.20;
		    else if(chest.getType() == Material.CHAINMAIL_CHESTPLATE)red = red + 0.20;
		    else if(chest.getType() == Material.IRON_CHESTPLATE)red = red + 0.24;
		    else if(chest.getType() == Material.DIAMOND_CHESTPLATE)red = red + 0.32;
	    }
	    return red;
	}
	public int armorCount(Player player)
	{
		org.bukkit.inventory.PlayerInventory inv = player.getInventory();
	    ItemStack boots = inv.getBoots();
	    ItemStack helmet = inv.getHelmet();
	    ItemStack chest = inv.getChestplate();
	    ItemStack pants = inv.getLeggings();
	    int i = 0;
	    if(helmet != null)i++;
	    if(boots != null)i++;
	    if(chest != null)i++;
	    if(pants != null)i++;
	    return i;
	}
}
