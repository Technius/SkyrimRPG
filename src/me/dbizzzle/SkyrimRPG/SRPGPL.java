package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
		if(event.getPlayer().getItemInHand().getType() != Material.BLAZE_ROD)return;
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
				if (sm.processExperience(se, "Archery")) {
					sm.incrementLevel("Archery", se);
					SkillManager.progress.get(se).put("PickPocket", 0);
				} else {
					SkillManager.progress.get(se).put("PickPocket", SkillManager.progress.get(se).get("PickPocket") + 1);
				}
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				    @SuppressWarnings("deprecation")
					public void run() {
				    	Player picked = plugin.getServer().getPlayer(ents);
				        picked.sendMessage(pickpocketed);
				        picked.updateInventory();
				    }
				}, delay);
			}
		}
	}
	
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
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
}
