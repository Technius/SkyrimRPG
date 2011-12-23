package me.dbizzzle.SkyrimRPG;

import java.util.Random;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
import net.minecraft.server.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;

public class SRPGPL extends PlayerListener {
	public SkyrimRPG plugin;

	public SRPGPL(SkyrimRPG p) {
		plugin = p;
	}

	int secondsDelay = 20; //This will be configurable, I just set 20 for now
	long delay = secondsDelay*20;

	String pickpocketed = ChatColor.RED + "Somebody has pickpocketed you!"; //Configurable

	public void onPlayerInteract(PlayerInteractEvent event)
	{
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
		else if (event.getPlayer().getItemInHand().getType() == Material.REDSTONE_TORCH_ON)
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
				if(!SpellManager.boundleft.containsKey(event.getPlayer()))return;
				plugin.sm.castSpell(SpellManager.boundleft.get(event.getPlayer()), event.getPlayer());
			}
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(!SpellManager.boundright.containsKey(event.getPlayer()))return;
				plugin.sm.castSpell(SpellManager.boundright.get(event.getPlayer()), event.getPlayer());
			}
		}
}

	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player se = event.getPlayer();
		EntityPlayer s = ((CraftPlayer) event.getPlayer()).getHandle();
		if (s.isSneaking()) {
			Entity ent = event.getRightClicked();
			final String ents = ((HumanEntity) ent).getName();
			if (ent instanceof Player) {
				EntityPlayer pick = ((CraftPlayer) plugin.getServer().getPlayer(ents)).getHandle();
				s.a(pick.inventory);
				se.sendMessage(ChatColor.GREEN + "You have succesfully pickpocketed " + ents + "!");

				SkillManager sm = new SkillManager();
				if (sm.processExperience(se, "PickPocket")) {
					sm.incrementLevel("PickPocket", se);
					SkillManager.progress.get(se).put("PickPocket", 0);
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
					}
				}, delay);
			}
		}
	}
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		SkillManager sm = new SkillManager();
		sm.setPlugin(plugin);
		sm.loadSkills(event.getPlayer());
	}
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		SkillManager sm = new SkillManager();
		sm.setPlugin(plugin);
		sm.saveSkills(event.getPlayer());
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
}

