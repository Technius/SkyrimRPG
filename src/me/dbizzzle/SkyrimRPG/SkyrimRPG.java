package me.dbizzzle.SkyrimRPG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import me.dbizzzle.SkyrimRPG.SpellManager.Spell;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyrimRPG extends JavaPlugin 
{
	public Logger log = Logger.getLogger("Minecraft");
	SpellManager sm = new SpellManager(this);
	SkillManager sk = new SkillManager();
	SpellTimer st = new SpellTimer(this);
	SRPGPL pl = new SRPGPL(this);
	SRPGEL el = new SRPGEL();
	public void onEnable() 
	{
		sk.setPlugin(this);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, pl, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, pl, Event.Priority.Low, this);
		pm.registerEvent(Event.Type.FOOD_LEVEL_CHANGE, el, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, pl, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, pl, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, el, Event.Priority.High, this);
		pm.registerEvent(Event.Type.ENTITY_TARGET, el, Event.Priority.High, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, el, Event.Priority.High, this);
		pm.registerEvent(Event.Type.EXPLOSION_PRIME, el, Event.Priority.Highest, this);
		if(!checkFiles())createFiles();
		for(Player p: this.getServer().getOnlinePlayers())sk.loadSkills(p);
		log.info("[SkyrimRPG] Plugin enabled.");
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new MagickaTimer(), 0, 20);
	}
	
	public void onDisable() 
	{
		for(Player p: this.getServer().getOnlinePlayers())sk.saveSkills(p);
		log.info("[SkyrimRPG] Plugin disabled.");
	}
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player player = null;
		if(sender instanceof Player)player = (Player)sender;
		if (command.getName().equalsIgnoreCase("bindspell")) {
			if (player == null) 
			{
				sender.sendMessage(ChatColor.RED + "Console can not use magic.");
			} 
			else if (player.hasPermission("skyrimrpg.bindspell")) 
			{
				if(args.length != 2)
				{
					player.sendMessage(ChatColor.RED + "Usage: /bindspell <left/right> <spell>");
				}
				else
				{
					int mode = 1;
					if(args[0].equalsIgnoreCase("left"))mode = 1;
					else if(args[0].equalsIgnoreCase("right"))mode = 2;
					else if(args[0].equalsIgnoreCase("both"))mode = 3;
					if(args[1].equalsIgnoreCase("none"))
					{
						if(mode == 1 ||mode == 3)SpellManager.boundleft.remove(player);
						if(mode == 2 ||mode == 3)SpellManager.boundright.remove(player);
						if(mode == 3)player.sendMessage(ChatColor.GREEN + "Spell bindings removed from both hands");
						else player.sendMessage(ChatColor.GREEN + "Spell bindings removed from " + (mode == 1 ? "left" : "right") + " hand");
						return true;
					}
					Spell s = null;
					try{s = SpellManager.Spell.valueOf(args[1].toUpperCase());}
					catch(IllegalArgumentException iae){if(s == null)sender.sendMessage("No such spell!");return true;}
					switch(s)
					{
					case FIREBALL:
						SpellManager.boundleft.put(player, Spell.FIREBALL);
						SpellManager.boundright.put(player, Spell.UFIREBALL);
						player.sendMessage(ChatColor.GREEN + "Fireball bound to both hands");
						break;
					case RAISE_ZOMBIE:
						if(mode == 1 ||mode == 3)SpellManager.boundleft.put(player, Spell.RAISE_ZOMBIE);
						if(mode == 2 ||mode == 3)SpellManager.boundright.put(player, Spell.RAISE_ZOMBIE);
						if(mode == 3)player.sendMessage(ChatColor.GREEN + "Raise Zombie bound to both hands");
						else player.sendMessage(ChatColor.GREEN + "Raise Zombie bound to " + (mode == 1 ? "left" : "right") + " hand");
					}
				}
			}
			else
			{
				player.sendMessage("You don't have magical powers");
			}
		}
		
		if (command.getName().equalsIgnoreCase("addspell")) 
		{
			if (player == null) 
			{
				Player spell = getServer().getPlayer(args[0]);
				if (spell != null) 
				{
					switch (args.length) 
					{
					case 0:
						sender.sendMessage(ChatColor.RED + "Usage: <player> <spell>");
						break;
						
					case 1:
						sender.sendMessage(ChatColor.RED + "Usage: <player> <spell>");
						break;
						
					default:
						String spel = spell.getName();
						Spell s = SpellManager.Spell.valueOf(args[1]);
						try
						{
							s = SpellManager.Spell.valueOf(args[1].toUpperCase());
							sender.sendMessage(ChatColor.GREEN + "You have given the spell " + args[1] + " to " + spel + ".");
							spell.sendMessage(ChatColor.GREEN + "You have been given the spell " + args[1] + ".");
						}
						catch(IllegalArgumentException iae){if(s == null)sender.sendMessage("No such spell!");return true;}
						break;
					}
				} 
				else 
				{
					sender.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
				}
			} 
			else if (player.hasPermission("skyrimrpg.addspell"))
			{
				Player spell = getServer().getPlayer(args[0]);
				if (spell != null) 
				{
					switch (args.length) 
					{
						case 0:
							player.sendMessage(ChatColor.RED + "Usage: <player> <spell>");
							break;
							
						case 1:
							player.sendMessage(ChatColor.RED + "Usage: <player> <spell>");
							break;
							
						default:
							String spel = spell.getName();
							Spell s = SpellManager.Spell.valueOf(args[1]);
							try
							{
								s = SpellManager.Spell.valueOf(args[1].toUpperCase());
								sender.sendMessage(ChatColor.GREEN + "You have given the spell " + args[1] + " to " + spel + ".");
								spell.sendMessage(ChatColor.GREEN + "You have been given the spell " + args[1] + ".");
							}
							catch(IllegalArgumentException iae){if(s == null)sender.sendMessage("No such spell!");return true;}
							break;
					}
				} 
			}
			else 
			{
				player.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
			}
		}
		
		else if (command.getName().equalsIgnoreCase("removespell")) 
		{
			if (player == null) 
			{
				Player spell = getServer().getPlayer(args[0]);
				if (spell != null) 
				{
					switch (args.length) 
					{
					case 0 :
						sender.sendMessage(ChatColor.RED + "Usage: /removespell <player> <spell>");
						break;
					
					case 1:
						sender.sendMessage(ChatColor.RED + "Usage: /removespell <player> <spell>");
						break;
					
					default:
						String spel = spell.getName();
						Spell s = SpellManager.Spell.valueOf(args[1]);
						if(s == null)sender.sendMessage("No such spell!");
						else
						{
							sender.sendMessage(ChatColor.GREEN + "You have taken the spell " + args[1] + " from " + spel + ".");
							spell.sendMessage(ChatColor.GREEN + "The spell " + args[1] + " has been taken from you.");
						}
						break;
					}
				} 
				else 
				{
					sender.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
				}
			} 
			else if (player.hasPermission("SkyrimRPG.removespell")) 
			{
				Player spell = getServer().getPlayer(args[0]);
				if (spell != null) 
				{
					switch (args.length) 
					{
					case 0 :
						player.sendMessage(ChatColor.RED + "Proper syntax: /removespell <player> <spell>");
						break;
						
					case 1:
						player.sendMessage(ChatColor.RED + "Proper syntax: /removespell <player> <spell>");
						break;
						
					default:
						String spel = spell.getName();
						Spell s = SpellManager.Spell.valueOf(args[1]);
						if(s == null)sender.sendMessage("No such spell!");
						else
						{
							sender.sendMessage(ChatColor.GREEN + "You have taken the spell " + args[1] + " from " + spel + ".");
							spell.sendMessage(ChatColor.GREEN + "The spell " + args[1] + " has been taken from you.");
						}
						break;
					}
				} 
				else 
				{
					player.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
				}
			} 
			else 
			{
				player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
			}
		}
		
		else if (command.getName().equalsIgnoreCase("listspells")) 
		{
			if (player == null) 
			{
				if (args.length == 0) 
				{
					sender.sendMessage("You don't have any spells");
				} 
				else if (args.length > 0) 
				{
					Player spell = getServer().getPlayer(args[0]);
					if (spell != null) 
					{
						for(Spell s:SpellManager.spells.get(spell))
						{
							switch(s)
							{
							case FIREBALL:
								sender.sendMessage("Fireball");
								break;
							case RAISE_ZOMBIE:
								sender.sendMessage("Raise Zombie");
								break;
							case HEALING:
								sender.sendMessage("Healing");
								break;
							}
						}
					} 
					else 
					{
						sender.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
					}
				}
			} 
			else if(player.hasPermission("skyrimrpg.listspells"))
			{
				if (args.length == 0) 
				{
					for(Spell s:SpellManager.spells.get(player))
					{
						switch(s)
						{
						case FIREBALL:
							sender.sendMessage("Fireball");
							break;
						case RAISE_ZOMBIE:
							sender.sendMessage("Raise Zombie");
							break;
						case HEALING:
							sender.sendMessage("Healing");
							break;
						}
					}
				} 
				else if (args.length > 0) 
				{
					Player spell = getServer().getPlayer(args[0]);
					if (spell != null) 
					{
						for(Spell s:SpellManager.spells.get(player))
						{
							switch(s)
							{
							case FIREBALL:
								sender.sendMessage("Fireball");
								break;
							case RAISE_ZOMBIE:
								sender.sendMessage("Raise Zombie");
								break;
							case HEALING:
								sender.sendMessage("Healing");
								break;
							}
						}
					} 
					else 
					{
						player.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
					}
				}
			} 
			else
			{
				player.sendMessage(ChatColor.RED + "You don't have permissions to use this command.");
			}
		}
		else if(command.getName().equalsIgnoreCase("skyrimrpg") || label.equalsIgnoreCase("srpg"))
		{
			if(player == null)
			{
				log.info("[SkyrimRPG]SkyrimRPG version " + this.getDescription().getVersion());
				log.info("[SkyrimRPG]Made by dbizzle and Technius");
			}
			else
			{
				player.sendMessage(ChatColor.YELLOW + "SkyrimRPG version " + this.getDescription().getVersion());
				player.sendMessage(ChatColor.GREEN + "Made by dbizzle and Technius");
				player.sendMessage("========================");
				player.sendMessage(ChatColor.RED + "/skystats <page>" + ChatColor.YELLOW + " - displays your stats");
			}
		}
		else if(command.getName().equalsIgnoreCase("skystats"))
		{
			if(player == null)
			{
				log.info("[SkyrimRPG]You don't have stats!");
			}
			else
			{
				player.sendMessage(ChatColor.GOLD + "Stats");
				player.sendMessage(ChatColor.GREEN + "Level: " + SkillManager.level.get(player));
				player.sendMessage(ChatColor.BLUE + "Magicka: " + SpellManager.magicka.get(player));
				player.sendMessage("Archery: Level " + SkillManager.getSkillLevel("Archery", player));
				player.sendMessage("Swordsmanship: Level " + SkillManager.getSkillLevel("Swordsmanship", player));
				player.sendMessage("Axecraft: Level " + SkillManager.getSkillLevel("Axecraft", player));
				player.sendMessage("Destruction: Level " + SkillManager.getSkillLevel("Destruction", player));
				player.sendMessage("Conjuration: Level " + SkillManager.getSkillLevel("Conjuration", player));
				player.sendMessage("Pickpocketing: Level " + SkillManager.getSkillLevel("PickPocket", player));
				player.sendMessage("Lockpicking: Level " + SkillManager.getSkillLevel("Lockpicking", player));
			}
		}
		return true;
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
		return true;
	}
}
