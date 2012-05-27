package me.dbizzzle.SkyrimRPG;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Logger;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
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
	public Logger log = Logger.getLogger("Minecraft");
	private SpellManager sm = new SpellManager(this);
	private SkillManager sk = new SkillManager(this);
	private SpellTimer st = new SpellTimer(this);
	ConfigManager cm = new ConfigManager(this);
	VersionManager vm = new VersionManager();
	String latestversion;
	public SkillManager getSkillManager()
	{
		return sk;
	}
	public SpellManager getSpellManager()
	{
		return sm;
	}
	public SpellTimer getSpellTimer()
	{
		return st;
	}
	public void onEnable() 
	{
		getCommand("addspell").setExecutor(new SkyrimCmd(sm, this, cm, sk));
		getCommand("bindspell").setExecutor(new SkyrimCmd(sm, this, cm, sk));
		getCommand("addperk").setExecutor(new PerkCmd(this));
		getCommand("perk").setExecutor(new PerkCmd(this));
		getCommand("removeperk").setExecutor(new PerkCmd(this));
		getCommand("removespell").setExecutor(new SkyrimCmd(sm, this, cm, sk));
		getCommand("listspells").setExecutor(new SkyrimCmd(sm, this, cm, sk));
		getCommand("skyrimrpg").setExecutor(new SkyrimCmd(sm, this, cm, sk));
		getCommand("skystats").setExecutor(new SkyrimCmd(sm, this, cm, sk));
		sk.setPlugin(this);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new SRPGL(this), this);
		if(!checkFiles())cm.refreshConfig();
		cm.loadConfig();
		for(Player p: this.getServer().getOnlinePlayers())sk.loadData(p);
		log.info("[SkyrimRPG]Version " + getDescription().getVersion() + " enabled.");
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new MagickaTimer(), 0, 20);
		VCThread check = new VCThread(this);
		check.start();
	}
	
	public void onDisable() 
	{
		for(Player p: this.getServer().getOnlinePlayers())sk.saveData(p);
		log.info("[SkyrimRPG] Plugin disabled.");
	}
	public boolean checkFiles()
	{
		File file = new File(this.getDataFolder().getPath());
		if(!file.exists())return false;
		File players = new File(file.getPath() + File.separator + "Players");
		if(!players.exists())return false;
		File config = new File(file.getPath() + File.separator + "config.txt");
		if(!config.exists())return false;
		File magic = new File(file.getPath() + File.separator + "Magic");
		if(!magic.exists())return false;
		File perks = new File(file.getPath() + File.separator + "Perks");
		if(!perks.exists())return false;
		return true;
	}
	private class VC implements Runnable
	{
		private VC(String message)
		{
			this.message = message;
		}
		private String message;
		public void run() {
			log.info("[SkyrimRPG]" + message);
		}
		
	}
	private class VCThread extends Thread
	{
		private SkyrimRPG s;
		private VCThread(SkyrimRPG i)
		{
			s = i;
		}
		@SuppressWarnings("deprecation")
		public void run()
		{
			try
			{
				latestversion = vm.getLatestVersion();
				if(latestversion == null)log.info("[SkyrimRPG]Could not find new version");
				else if(vm.compareVersion(latestversion, s.getDescription().getVersion()))
				{
					s.getServer().getScheduler().scheduleSyncDelayedTask(s, 
							new VC("New " + (latestversion.indexOf("DEV") > -1 ? "dev build" : "release") 
									+ " is available: " + latestversion), 0L);
				}
			}
			catch(MalformedURLException mue)
			{
				log.info("[SkyrimRPG]Could not find new version");
			}
			stop();
		}
	}
	public void debug(String message)
	{
		if(ConfigManager.debug)log.info("[SkyrimRPG] [DEBUG] " + message);
	}
}
