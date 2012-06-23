package me.dbizzzle.SkyrimRPG;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
import me.dbizzzle.SkyrimRPG.event.PlayerPickpocketEvent;
import net.minecraft.server.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.material.TrapDoor;
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
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(ConfigManager.disabledWorlds.contains(event.getPlayer().getWorld()))return;
		if(event.getPlayer().getItemInHand().getType() == Material.REDSTONE_TORCH_ON && ConfigManager.enableLockpicking)
		{
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				Material btype = event.getClickedBlock().getType();
				if (btype == Material.IRON_DOOR_BLOCK || (btype == Material.WOOD_DOOR && event.isCancelled()))
				{
					if(lpcd.contains(event.getPlayer()) && ConfigManager.enableLpCd)
					{
						event.getPlayer().sendMessage(ChatColor.RED + "You don't feel comfortable attempting to pick locks right now");
						return;
					}
					Door d = (Door) btype.getNewData(event.getClickedBlock().getData());
					if(!d.isOpen())
					{
						SkillManager sm = plugin.getSkillManager();
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
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=success, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						else if(new Random().nextInt(100) + 1 > plugin.getSkillManager().getSkillLevel(Skill.LOCKPICKING, event.getPlayer())/2 + 10)
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
						sm.calculateLevel(event.getPlayer(), Skill.LOCKPICKING);
					}
					event.getPlayer().getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(Skill.LOCKPICKING, event.getPlayer(), false), ConfigManager.LockpickingCooldown);
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
						else if(new Random().nextInt(100) + 1 > plugin.getSkillManager().getSkillLevel(Skill.LOCKPICKING, event.getPlayer())/2 + 10)
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
						SkillManager sm = plugin.getSkillManager();
						sm.calculateLevel(event.getPlayer(), Skill.LOCKPICKING);
					}
				}
				else if(btype == Material.CHEST)
				{
					if(event.isCancelled())
					{
						if(lpcd.contains(event.getPlayer()) && ConfigManager.enableLpCd)
						{
							event.getPlayer().sendMessage(ChatColor.RED + "You don't feel comfortable attempting to pick locks right now");
							return;
						}
						SkillManager sm = plugin.getSkillManager();
						if(pickLockSuccess(event.getPlayer()))
						{
							net.minecraft.server.TileEntityChest c = (net.minecraft.server.TileEntityChest)((org.bukkit.craftbukkit.CraftWorld)event.getPlayer().getWorld()).getTileEntityAt(event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ());
							((net.minecraft.server.EntityHuman)((CraftPlayer)event.getPlayer()).getHandle()).openContainer(c);
							event.getPlayer().sendMessage(ChatColor.GREEN + "Lockpicking success!");
							if(event.isCancelled())event.setCancelled(false);
							Location l = event.getClickedBlock().getLocation();
							String bl = l.getX() + "," + l.getY() + "," + l.getZ();
							plugin.debug("Lockpicking: result=success, player=" + event.getPlayer() + ", block=" + bl + " , world=" + l.getWorld());
						}
						else if(new Random().nextInt(100) + 1 > plugin.getSkillManager().getSkillLevel(Skill.LOCKPICKING, event.getPlayer())/2 + 10)
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
						sm.calculateLevel(event.getPlayer(), Skill.LOCKPICKING);
						event.getPlayer().getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(Skill.LOCKPICKING, event.getPlayer(), false));
					}
				}
				event.getPlayer().updateInventory();
				return;
			}
		}
		else if(event.getPlayer().getItemInHand().getType().getId() == ConfigManager.wand)
		{
			if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				Spell s = plugin.getSpellManager().getBoundLeft(event.getPlayer());
				if(s != null)
				{
					plugin.getSpellManager().castSpell(s, event.getPlayer());
				}
			}
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				Spell s = plugin.getSpellManager().getBoundRight(event.getPlayer());
				if(s != null)
				{
					plugin.getSpellManager().castSpell(s, event.getPlayer());
				}
			}
		}
		else if(event.getPlayer().getItemInHand().getType() == Material.BOOK)
		{
			if(!(Boolean)plugin.getConfigManager().getValue("useSpellBooks"))return;
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				ItemStack b = event.getPlayer().getItemInHand();
				if(b.getDurability() != 0)
				{
					if(plugin.getSpellManager().useBook(event.getPlayer(), b.getDurability()))
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
		if(ConfigManager.disabledWorlds.contains(event.getPlayer().getWorld()))return;
		Player se = event.getPlayer();
		final EntityPlayer s = ((CraftPlayer)se).getHandle();
		if (s.isSneaking() && ConfigManager.enablePickpocketing) {
			Entity ent = event.getRightClicked();
			if (ent instanceof Player) {
				final Player victim = (Player) ent;
				if(ppcd.contains(se) && ConfigManager.enablePpCd)
				{
					se.sendMessage(ChatColor.RED + "You are too afraid to pickpocket someone right now");
					plugin.debug("Pickpocketing: result=cooldown, player=" + se.getName() + ", " + "target= " + ((Player)ent).getName());
					return;
				}
				if(((Player)ent).hasPermission("skyrimrpg.nopickpocket"))
				{
					se.sendMessage(ChatColor.RED + "You probably don't want to pickpocket this person.");
					plugin.debug("Pickpocketing: result=denied, player=" + se.getName() + ", " + "target= " + victim.getName());
					return;
				}
				EntityPlayer pick = ((CraftPlayer) victim).getHandle();
				Random r = new Random();
				double c = r.nextInt(100) + 1;
				double mul = 1.0;
				if(plugin.getPerkManager().hasPerk(se, Perk.LIGHT_FINGERS))mul = mul + (0.2*plugin.getPerkManager().getPerkLevel(se, Perk.LIGHT_FINGERS));
				mul = mul - (0.25*plugin.getSkillManager().getSkillLevel(Skill.PICKPOCKETING, victim));
				c = c * mul;
				if(c > plugin.getSkillManager().getSkillLevel(Skill.PICKPOCKETING, se)&& ConfigManager.enablePickpocketingChance)
				{
					se.sendMessage(ChatColor.RED + "You have unsucessfully pickpocketed " + victim.getName() + "!");
					victim.sendMessage(ChatColor.RED + se.getName() + " tried to pickpocket you!");
					SkillManager sm = plugin.getSkillManager();
					sm.calculateLevel(event.getPlayer(), Skill.PICKPOCKETING);
					plugin.debug("Pickpocketing: result=fail, player=" + se.getName() + ", " + "target= " + ((Player)ent).getName());
					se.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(Skill.PICKPOCKETING, se, false), ConfigManager.PickpocketingCooldown);
					se.getServer().getPluginManager().callEvent(new PlayerPickpocketEvent(event.getPlayer(), victim, false));
					return;
				}
				PlayerPickpocketEvent e = new PlayerPickpocketEvent(event.getPlayer(), victim, true);
				se.getServer().getPluginManager().callEvent(e);
				if(e.isCancelled())return;
				s.openContainer(pick.inventory);
				se.sendMessage(ChatColor.GREEN + "You have succesfully pickpocketed " + victim.getName() + "!");
				plugin.debug("Pickpocketing: result=success, player=" + se.getName() + ", " + "target= " + ((Player)ent).getName());
				SkillManager sm = plugin.getSkillManager();
				sm.calculateLevel(event.getPlayer(), Skill.PICKPOCKETING);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						if(!victim.isOnline())return;
						victim.sendMessage(pickpocketed);
						victim.updateInventory();
						s.closeInventory();
					}
				}, delay);
				se.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Cooldown(Skill.PICKPOCKETING, se, false), ConfigManager.PickpocketingCooldown);
				return;
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		plugin.getSkillManager().loadData(event.getPlayer());
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.getSkillManager().saveData(event.getPlayer());
	}

	public boolean pickLockSuccess(Player pla)
	{
		int alevel = plugin.getSkillManager().getSkillLevel(Skill.PICKPOCKETING, pla);
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
	@EventHandler(priority = EventPriority.MONITOR)
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
	public void sneakDetect(PlayerMoveEvent event)
	{
		
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void sneakSkill(PlayerToggleSneakEvent event)
	{
		if(event.isCancelled())return;
		if(ConfigManager.disabledWorlds.contains(event.getPlayer().getWorld()))return;
		Player player = event.getPlayer();
		boolean detect = false;
		if(event.isSneaking() && !sneak.contains(player))
		{
			List<Entity> a = player.getNearbyEntities(60, 10, 60);
			for(Entity e:a)
			{
				if(e instanceof Player)
				{
					Player t = (Player)e;
					int alevel = plugin.getSkillManager().getSkillLevel(Skill.SNEAK, t);
					double dmul = 1.0;
					if(plugin.getPerkManager().hasPerk(player, Perk.STEALTH))
					{
						dmul = 1.15 + 0.05*plugin.getPerkManager().getPerkLevel(player, Perk.STEALTH);
					}
					double d = e.getLocation().distance(player.getLocation());
					if(d + dmul*(10 + alevel/5) >= 70)
					{
						plugin.getSkillManager().calculateLevel(player, Skill.SNEAK);
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
			if(!detect && ConfigManager.enableSneakMessage)player.sendMessage(ChatColor.YELLOW + "Hidden");
			else if(detect && a.size() != 0 && ConfigManager.enableSneakMessage)player.sendMessage(ChatColor.YELLOW + "Detected");
			else if(ConfigManager.enableSneakMessage)player.sendMessage(ChatColor.YELLOW + "Hidden");
		}
		else if(!event.isSneaking() && sneak.contains(player))
		{
			if(ConfigManager.enableSneakMessage)player.sendMessage(ChatColor.YELLOW + "Now visible");
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
		if(ConfigManager.disabledWorlds.contains(event.getEntity().getWorld()))return;
		if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
			if(e.getDamager() instanceof Player && e.getEntity() instanceof Player && !event.getEntity().getWorld().getPVP())return;
			if((e.getDamager() instanceof Arrow) && !ConfigManager.disabledSkills.contains(Skill.ARCHERY))
			{
				Arrow a = (Arrow)e.getDamager();
				if(a.getShooter() instanceof Player)
				{
					SkillManager sm = plugin.getSkillManager();
					Player shooter = (Player)a.getShooter();
					int alevel = plugin.getSkillManager().getSkillLevel(Skill.ARCHERY, shooter);
					double perkm = 1;
					if(plugin.getPerkManager().hasPerk(shooter, Perk.OVERDRAW))
					{
						perkm = 1 + (0.2*plugin.getPerkManager().getPerkLevel(shooter, Perk.OVERDRAW));
					}
					e.setDamage((int)((e.getDamage() + (alevel/10))*(1 + perkm)));
					sm.calculateLevel(shooter, Skill.ARCHERY);
					//debug message:  shooter.sendMessage("Progress:" + pro + "/" + t);
				}
				else return;
			}
			else if(e.getDamager() instanceof Player)
			{
				SkillManager sm = plugin.getSkillManager();
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
				if(t.equalsIgnoreCase("Sword") && !ConfigManager.disabledSkills.contains(Skill.SWORDSMANSHIP))
				{
					int alevel = plugin.getSkillManager().getSkillLevel(Skill.SWORDSMANSHIP, player);
					double perkm = 1;
					if(plugin.getPerkManager().hasPerk(player, Perk.SWORDSMAN))
					{
						perkm = 1 + (0.2*plugin.getPerkManager().getPerkLevel(player, Perk.SWORDSMAN));
					}
					if(plugin.getPerkManager().hasPerk(player, Perk.BLADESMAN))
					{
						int a = new Random().nextInt(100);
						if(a < 5 + (5*plugin.getPerkManager().getPerkLevel(player, Perk.BLADESMAN)))perkm = perkm + 0.25 + plugin.getPerkManager().getPerkLevel(player, Perk.BLADESMAN);
					}
					e.setDamage((int)(e.getDamage() + (alevel/10)*perkm));
					sm.calculateLevel(player, Skill.SWORDSMANSHIP);
				}
				else if(t.equalsIgnoreCase("Axe") && !ConfigManager.disabledSkills.contains(Skill.AXECRAFT))
				{
					int alevel = plugin.getSkillManager().getSkillLevel(Skill.AXECRAFT, player);
					int crit = new Random().nextInt(99);
					e.setDamage(e.getDamage() + (alevel/10) * (crit<=alevel ? 2 : 1));
					sm.calculateLevel(player, Skill.AXECRAFT);
				}
				if(sneak.contains(player) && event.getEntity() instanceof Player)
				{
					if(!((Player)event.getEntity()).canSee(player))
					{
						event.setDamage(event.getDamage()* 2);
						player.sendMessage(ChatColor.GREEN + "Sneak attack for 2x damage!");
					}
				}
			}
			else if(e.getDamager() instanceof SmallFireball)
			{
				SmallFireball sf = (SmallFireball)e.getDamager();
				if(sf.getShooter() instanceof Player)
				{
					if(SpellManager.flames.contains(sf))
					{
						Player player = (Player)sf.getShooter();
						int alevel = plugin.getSkillManager().getSkillLevel(Skill.DESTRUCTION, player)/20;
						int damage = 1 + alevel;
						e.setDamage(damage);
						SkillManager sm = plugin.getSkillManager();
						sm.calculateLevel(player, Skill.DESTRUCTION);
					}
				}
			}
			else if(e.getDamager() instanceof Snowball)
			{
				Snowball sf = (Snowball)e.getDamager();
				if(sf.getShooter() instanceof Player)
				{
					if(SpellManager.frostbite.contains(sf))
					{
						Player player = (Player)sf.getShooter();
						int alevel = plugin.getSkillManager().getSkillLevel(Skill.DESTRUCTION, player)/30;
						int plevel = plugin.getSkillManager().getSkillLevel(Skill.DESTRUCTION, player)/5;
						int damage = 1 + alevel;
						e.setDamage(damage);
						if(event.getEntity() instanceof LivingEntity)
						{
							((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, plevel + 20, 1));
						}
						SkillManager sm = plugin.getSkillManager();
						sm.calculateLevel(player, Skill.DESTRUCTION);
					}
				}
			}
			if(e.getEntity() instanceof Player)
			{
				Player player = (Player)e.getEntity();
				SkillManager sm = plugin.getSkillManager();
				if(player.isBlocking() && ToolComparer.getType(player.getItemInHand()).equalsIgnoreCase("Sword") && !ConfigManager.disabledSkills.contains(Skill.BLOCKING))
				{
					int alevel = plugin.getSkillManager().getSkillLevel(Skill.BLOCKING, player);
					double perkm = 1.0;
					if(plugin.getPerkManager().hasPerk(player, Perk.SWORD_WALL))
					{
						perkm = 1 + (0.1*plugin.getPerkManager().getPerkLevel(player, Perk.SWORD_WALL));
					}
					if(plugin.getPerkManager().hasPerk(player, Perk.DEFLECT_ARROWS) && e.getDamager() instanceof Arrow)
					{
						e.setDamage(0);
						plugin.debug("Blocking: player=" + player.getName() + ", damage=" + event.getDamage() + ", blocked=DEFLECT_ARROWS PERK");
					}
					else plugin.debug("Blocking: player=" + player.getName() + ", damage=" + event.getDamage() + ", blocked=" + ((int)(perkm*(alevel/10))));
					e.setDamage(e.getDamage() - ((int)(perkm*(alevel/10))));
					sm.calculateLevel(player, Skill.BLOCKING);
				}
				double red;
				if((red = getDamageReduced(player)) > 0 && !ConfigManager.disabledSkills.contains(Skill.ARMOR))
				{
					int a = armorCount(player);
					int i = 0;
					double alevel = plugin.getSkillManager().getSkillLevel(Skill.ARMOR, player);
					if(alevel <= 0)alevel = 0.01;
					else alevel = alevel/100;
					while(i < a)
					{
						sm.calculateLevel(player, Skill.ARMOR);
						i ++;
					}
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
	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(event.getEntity() instanceof SmallFireball)
		{
			if(SpellManager.flames.contains(event.getEntity()))
			{
				SpellManager.flames.remove(event.getEntity());
			}
		}
		else if(event.getEntity() instanceof Snowball)
		{
			if(SpellManager.frostbite.contains(event.getEntity()))
			{
				SpellManager.frostbite.remove(event.getEntity());
			}
		}
	}
	//@EventHandler(priority = EventPriority.HIGHEST)
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
		int alevel = plugin.getSkillManager().getSkillLevel(Skill.DESTRUCTION, p);
		for(Entity x:tod)
		{
			if(!(x instanceof LivingEntity))continue;
			LivingEntity l = (LivingEntity) x;
			l.damage(7 + (alevel/10), p);
			l.getWorld().createExplosion(f.getLocation(), 0);
			l.setFireTicks(60);
			sp = sp+1;
		}
		SkillManager sm = plugin.getSkillManager();
		sm.calculateLevel(p, Skill.DESTRUCTION);
		SpellManager.ftracker.remove(f);
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
					cd = ConfigManager.PickpocketingCooldown;
				}
				else if(skill == Skill.LOCKPICKING)
				{
					lpcd.add(p);
					cd = ConfigManager.LockpickingCooldown;
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
