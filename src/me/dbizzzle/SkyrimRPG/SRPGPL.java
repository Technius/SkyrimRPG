package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
import net.minecraft.server.EntityPlayer;

import org.bukkit.ChatColor;
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
		else if (event.getPlayer().getItemInHand().getType() == Material.REDSTONE_TORCH)
		{
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if (event.getBlock().getType() == Material.IRON_DOOR)
				{
					Player s = event.getPlayer();
					Inventory inv = s.getInventory();
					if (inv.contains(Material.IRON_INGOT))
					{
						if (pickLockSuccess(s, s.getEyeLocation()))
						{
							SkillManager sm = new SkillManager();
							if (sm.processExperience(se, "LockPick"))
							{
								sm.incrementLevel("LockPick", se);
								SkillManager.progress.get(se).put("LockPick", 0);
							}
							else
							{
								SkillManager.progress.get(se).put("LockPick", SkillManager.progress.get(se).get("LockPick") + 1);
							}
						}
					}
				}
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
		if (loc.getBlock().getType() == Material.IRON_DOOR || Material.CHEST)
		{
			int alevel = SkillManager.getSkillLevel("LockPick", pla);
			if (Random.nextInt(10) < alevel/10)
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
	}
}
