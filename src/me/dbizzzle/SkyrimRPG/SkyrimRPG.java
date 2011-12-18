package me.dbizzzle.SkyrimRPG;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class SkyrimRPG extends JavaPlugin 
{
	public Logger log = Logger.getLogger("Minecraft");
	
	public void onEnable() 
	{
		log.info("[SkyrimRPG] Plugin enabled.");
	}
	
	public void onDisable() 
	{
		log.info("[SkyrimRPG] Plugin disabled.");
	}
}
