package me.dbizzzle.SkyrimRPG;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import me.dbizzzle.SkyrimRPG.spell.MagickaTimer;
import me.dbizzzle.SkyrimRPG.spell.SpellManager;
import me.dbizzzle.SkyrimRPG.spell.SpellTimer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
* SkyrimRPG
* Copyright (C) 2012 Technius <techniux@gmail.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

public class SkyrimRPG extends JavaPlugin 
{
	private Logger log;
	private SpellManager sm = new SpellManager(this);
	private SpellTimer st = new SpellTimer(this);
	private SRPGL listen = new SRPGL(this);
	private ConfigManager cm = new ConfigManager(this);
	private VersionManager vm = new VersionManager();
	private String latestversion;
	private String versionmessage = null;
	private PlayerManager pm;
	private static SkyrimRPG inst;
	public String getVersionMessage()
	{
		return versionmessage;
	}
	public PlayerManager getPlayerManager()
	{
		return pm;
	}
	public SpellManager getSpellManager()
	{
		return sm;
	}
	public SpellTimer getSpellTimer()
	{
		return st;
	}
	public VersionManager getVersionManager()
	{
		return vm;
	}
	public ConfigManager getConfigManager()
	{
		return cm;
	}
	public String getLatestVersion()
	{
		return latestversion;
	}
	public void onEnable() 
	{
		inst = this;
		log = getLogger();
		this.pm = new PlayerManager(this);
		SkyrimCmd cmd = new SkyrimCmd(this);
		PerkCmd pcmd = new PerkCmd(this);
		getCommand("addspell").setExecutor(cmd);
		getCommand("bindspell").setExecutor(cmd);
		getCommand("addperk").setExecutor(pcmd);
		getCommand("perk").setExecutor(pcmd);
		getCommand("removeperk").setExecutor(pcmd);
		getCommand("removespell").setExecutor(cmd);
		getCommand("listspells").setExecutor(cmd);
		getCommand("skyrimrpg").setExecutor(cmd);
		getCommand("skystats").setExecutor(cmd);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(listen, this);
		if(!new File(getDataFolder(), "config.txt").exists())cm.refreshConfig();
		cm.loadConfig();
		this.pm.load();
		for(Player pl:getServer().getOnlinePlayers())
		{
			if(this.pm.getData(pl.getName()) == null)
				this.pm.addData(new PlayerData(pl.getPlayer().getName()));
		}
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new MagickaTimer(this), 0, 20);
		VCThread check = new VCThread(this);
		check.start();
	}
	
	public void onDisable() 
	{
		pm.save();
		log.info("Plugin disabled.");
		sm.clearData();
		pm.clearData();
		listen.clearData();
		cm.clearData();
	}
	private class VC implements Runnable
	{
		private VC(String message)
		{
			this.message = message;
		}
		private String message;
		public void run() {
			getLogger().info(message);
		}
		
	}
	private class VCThread extends Thread
	{
		private SkyrimRPG s;
		private VCThread(SkyrimRPG i)
		{
			s = i;
		}
		public void run()
		{
			try
			{
				latestversion = vm.getLatestVersion();
				if(latestversion == null)log.info("Failed to find new version!");
				else if(vm.compareVersion(latestversion, s.getDescription().getVersion()))
				{
					versionmessage = "A new release is available: " + latestversion;
					s.getServer().getScheduler().scheduleSyncDelayedTask(s, 
							new VC(versionmessage), 0L);
				}
			}
			catch(MalformedURLException mue)
			{
				log.info("Could not find new version");
			}
		}
	}
	public void debug(String message)
	{
		if(cm.debug)getLogger().info("[DEBUG] " + message);
	}
	public static SkyrimRPG instance()
	{
		return inst;
	}
}
