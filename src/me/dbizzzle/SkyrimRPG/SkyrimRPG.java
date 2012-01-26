package me.dbizzzle.SkyrimRPG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyrimRPG extends JavaPlugin 
{
	public Logger log = Logger.getLogger("Minecraft");
	SpellManager sm = new SpellManager(this);
	SkillManager sk = new SkillManager();
	SpellTimer st = new SpellTimer(this);
	ConfigManager cm = new ConfigManager(this);
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
		if(!checkFiles())createFiles();
		cm.loadConfig();
		for(Player p: this.getServer().getOnlinePlayers())sk.loadData(p);
		log.info("[SkyrimRPG]Version " + getDescription().getVersion() + " enabled.");
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new MagickaTimer(), 0, 20);
	}
	
	public void onDisable() 
	{
		for(Player p: this.getServer().getOnlinePlayers())sk.saveData(p);
		log.info("[SkyrimRPG] Plugin disabled.");
	}
	public boolean createFiles()
	{
		File file = new File(this.getDataFolder().getPath());
		if(!file.exists())file.mkdir();
		File players = new File(file.getPath() + File.separator + "Players");
		if(!players.exists())players.mkdir();
		File config = new File(file.getPath() + File.separator + "config.txt");
		File locks = new File(file.getPath() + File.separator + "Locks");
		if(!locks.exists())locks.mkdir();
		File magic = new File(file.getPath() + File.separator + "Magic");
		if(!magic.exists())magic.mkdir();
		File perks = new File(file.getPath() + File.separator + "Perks");
		if(!perks.exists())perks.mkdir();
		try
		{
			if(!config.exists())
			{
				FileOutputStream fos = new FileOutputStream(config);
				fos.flush();
				fos.close();
				BufferedWriter bw = new BufferedWriter(new FileWriter(config));
				bw.write("#SkyrimRPG configuration file");
				bw.newLine();
				bw.write("#Enable this if you want to use spellbooks");
				bw.newLine();
				bw.write("useSpellbooks: true");
				bw.newLine();
				bw.write("#Disable these if you don't like thievery");
				bw.newLine();
				bw.write("enableLockpicking: true");
				bw.flush();
				bw.write("enablePickpocketing: true");
				bw.newLine();
				bw.flush();
				bw.close();
			}
			return true;
		}
		catch(IOException ioe)
		{
			return false;
		}
	}
	public boolean checkFiles()
	{
		File file = new File(this.getDataFolder().getPath());
		if(!file.exists())return false;
		File players = new File(file.getPath() + File.separator + "Players");
		if(!players.exists())return false;
		File config = new File(file.getPath() + File.separator + "config.txt");
		if(!config.exists())return false;
		File locks = new File(file.getPath() + File.separator + "Locks");
		if(!locks.exists())return false;
		File magic = new File(file.getPath() + File.separator + "Magic");
		if(!magic.exists())return false;
		File perks = new File(file.getPath() + File.separator + "Perks");
		if(!perks.exists())return false;
		return true;
	}
}
