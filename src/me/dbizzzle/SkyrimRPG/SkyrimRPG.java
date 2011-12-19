package me.dbizzzle.SkyrimRPG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

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
	public HashMap<String, ArrayList<String>> spells = new HashMap<String, ArrayList<String>>();
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
		pm.registerEvent(Event.Type.FOOD_LEVEL_CHANGE, pl, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, pl, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, pl, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, el, Event.Priority.High, this);
		if(!checkFiles())createFiles();
		for(Player p: this.getServer().getOnlinePlayers())sk.loadSkills(p);
		log.info("[SkyrimRPG] Plugin enabled.");
	}
	
	public void onDisable() 
	{
		log.info("[SkyrimRPG] Plugin disabled.");
	}
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		Player player = null;
		if(sender instanceof Player)player = (Player)sender;
		
		if (label.equalsIgnoreCase("fireball")) {
			if (player == null) 
			{
				sender.sendMessage(ChatColor.RED + "Console can not use magic.");
			} 
			else if (player.hasPermission("skyrimrpg.fireball")) 
			{
				if (hasSpell(player.getName(), "fireball")) 
				{
					sm.shootFireball(player, 100);
				}
			}
		}
		
		if (label.equalsIgnoreCase("addspell")) 
		{
			ArrayList<String> temp = new ArrayList<String>();
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
						temp.add(args[1]);
						spells.put(spel, temp);
						sender.sendMessage(ChatColor.GREEN + "You have given the spell " + args[1] + " to " + spel + ".");
						spell.sendMessage(ChatColor.GREEN + "You have been given the spell " + args[1] + ".");
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
							temp.add(args[1]);
							spells.put(spel, temp);
							player.sendMessage(ChatColor.GREEN + "You have given the spell " + args[1] + " to " + spel + ".");
							spell.sendMessage(ChatColor.GREEN + "You have been given the spell " + args[1] + ".");
							break;
					}
				} 
			}
			else 
			{
				player.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
			}
		}
		
		if (label.equalsIgnoreCase("removespell")) 
		{
			ArrayList<String> temp = spells.get(args[0]);
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
						temp.remove(args[1]);
						sender.sendMessage(ChatColor.GREEN + "You have taken the spell " + args[1] + " from " + spel + ".");
						spell.sendMessage(ChatColor.GREEN + "The spell " + args[1] + " has been taken from you.");
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
						temp.remove(args[1]);
						player.sendMessage(ChatColor.GREEN + "You have taken the spell " + args[1] + " from " + spel + ".");
						spell.sendMessage(ChatColor.GREEN + "The spell " + args[1] + " has been taken from you.");
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
		
		if (label.equalsIgnoreCase("listspells")) 
		{
			ArrayList<String> temp = spells.get(args[0]);
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
						if (spells.containsKey(spell)) 
						{
							if (temp.isEmpty()) 
							{
								sender.sendMessage(ChatColor.GREEN + "This player has no spells.");
							} 
							else 
							{
								sender.sendMessage(ChatColor.GREEN + temp.toString());
							}
						} 
						else 
						{
							sender.sendMessage(ChatColor.RED + "This player has no spells.");
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
					player.sendMessage(ChatColor.GREEN + "fireball");
				} 
				else if (args.length > 0) 
				{
					Player spell = getServer().getPlayer(args[0]);
					if (spell != null) 
					{
						if (spells.containsKey(spell)) 
						{
							if (temp.isEmpty()) 
							{
								player.sendMessage(ChatColor.GREEN + "This player has no spells.");
							} 
							else 
							{
								player.sendMessage(ChatColor.GREEN + temp.toString());
							}
						} 
						else 
						{
							player.sendMessage(ChatColor.RED + "This player has no spells.");
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
		return true;
	}
	
	public boolean hasSpell(String player, String spell) 
	{
		if (spells.containsKey(player)) 
		{
			ArrayList<String> temp = spells.get(player);
			return temp.contains(spell);
		} 
		else 
		{
			return false;
		}
	}
	public boolean createFiles()
	{
		File file = new File(this.getDataFolder().getPath());
		if(!file.exists())file.mkdir();
		File players = new File(file.getPath() + File.separator + "Players");
		if(!players.exists())players.mkdir();
		File config = new File(file.getPath() + File.separator + "config.txt");
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
		return true;
	}
}
