package me.dbizzzle.SkyrimRPG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SkyrimRPG extends JavaPlugin 
{
	public Logger log = Logger.getLogger("Minecraft");
	
	public HashMap<String, ArrayList<String>> spells = new HashMap<String, ArrayList<String>>();
	
	public void onEnable() 
	{
		log.info("[SkyrimRPG] Plugin enabled.");
	}
	
	public void onDisable() 
	{
		log.info("[SkyrimRPG] Plugin disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("fireball")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Console can not use magic.");
			} else {
				Player s = (Player) sender;
				String se = s.getName();
				if (s.hasPermission("SkyrimRPG.fireball")) {
					if (hasSpell(se, "fireball")) {
						final Vector direction = s.getEyeLocation().getDirection().multiply(2);
						Fireball fireball = s.getWorld().spawn(s.getEyeLocation().add(direction.getX(), direction.getY(), 
								direction.getZ()), Fireball.class);
						fireball.setShooter(s);
					}
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("addspell")) {
			ArrayList<String> temp = new ArrayList<String>();
			if (!(sender instanceof Player)) {
				Player spell = getServer().getPlayer(args[0]);
				if (spell != null) {
					switch (args.length) {
					case 0:
						sender.sendMessage(ChatColor.RED + "Proper syntax: <player> <spell>");
						break;
						
					case 1:
						sender.sendMessage(ChatColor.RED + "Proper syntax: <player> <spell>");
						break;
						
					default:
						String spel = spell.getName();
						temp.add(args[1]);
						spells.put(spel, temp);
						sender.sendMessage(ChatColor.GREEN + "You gave the spell " + args[1] + " to " + spel + ".");
						spell.sendMessage(ChatColor.GREEN + "You have been given the spell " + args[1] + ".");
						break;
					}
				} else {
					sender.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
				}
			} else {
				Player s = (Player) sender;
				if (s.hasPermission("SkyrimRPG.addspell")) {
					Player spell = getServer().getPlayer(args[0]);
					if (spell != null) {
						switch (args.length) {
						case 0:
							s.sendMessage(ChatColor.RED + "Proper syntax: <player> <spell>");
							break;
							
						case 1:
							s.sendMessage(ChatColor.RED + "Proper syntax: <player> <spell>");
							break;
							
						default:
							String spel = spell.getName();
							temp.add(args[1]);
							spells.put(spel, temp);
							s.sendMessage(ChatColor.GREEN + "You gave the spell " + args[1] + " to " + spel + ".");
							spell.sendMessage(ChatColor.GREEN + "You have been given the spell " + args[1] + ".");
							break;
						}
					} else {
						s.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
					}
				} else {
					s.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("removespell")) {
			ArrayList<String> temp = spells.get(args[0]);
			if (!(sender instanceof Player)) {
				Player spell = getServer().getPlayer(args[0]);
				if (spell != null) {
					switch (args.length) {
					case 0 :
						sender.sendMessage(ChatColor.RED + "Proper syntax: /removespell <player> <spell>");
						break;
					
					case 1:
						sender.sendMessage(ChatColor.RED + "Proper syntax: /removespell <player> <spell>");
						break;
					
					default:
						String spel = spell.getName();
						temp.remove(args[1]);
						sender.sendMessage(ChatColor.GREEN + "You have taken the spell " + args[1] + " from " + spel + ".");
						spell.sendMessage(ChatColor.GREEN + "The spell " + args[1] + " has been taken from you.");
						break;
					}
				} else {
					sender.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
				}
			} else {
				Player s = (Player) sender;
				if (s.hasPermission("SkyrimRPG.removespell")) {
					Player spell = getServer().getPlayer(args[0]);
					if (spell != null) {
						switch (args.length) {
						case 0 :
							s.sendMessage(ChatColor.RED + "Proper syntax: /removespell <player> <spell>");
							break;
						
						case 1:
							s.sendMessage(ChatColor.RED + "Proper syntax: /removespell <player> <spell>");
							break;
						
						default:
							String spel = spell.getName();
							temp.remove(args[1]);
							s.sendMessage(ChatColor.GREEN + "You have taken the spell " + args[1] + " from " + spel + ".");
							spell.sendMessage(ChatColor.GREEN + "The spell " + args[1] + " has been taken from you.");
							break;
						}
					} else {
						s.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
					}
				} else {
					s.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("listspells")) {
			ArrayList<String> temp = spells.get(args[0]);
			if (!(sender instanceof Player)) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.GREEN + "fireball");
				} else if (args.length > 0) {
					Player spell = getServer().getPlayer(args[0]);
					if (spell != null) {
						if (spells.containsKey(spell)) {
							if (temp.isEmpty()) {
								sender.sendMessage(ChatColor.GREEN + "This player has no spells.");
							} else {
								sender.sendMessage(ChatColor.GREEN + temp.toString());
							}
						} else {
							sender.sendMessage(ChatColor.RED + "This player has no spells.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
					}
				}
			} else {
				Player s = (Player) sender;
				if (s.hasPermission("SkyrimRPG.listspells")) {
					if (args.length == 0) {
						s.sendMessage(ChatColor.GREEN + "fireball");
					} else if (args.length > 0) {
						Player spell = getServer().getPlayer(args[0]);
						if (spell != null) {
							if (spells.containsKey(spell)) {
								if (temp.isEmpty()) {
									s.sendMessage(ChatColor.GREEN + "This player has no spells.");
								} else {
									s.sendMessage(ChatColor.GREEN + temp.toString());
								}
							} else {
								s.sendMessage(ChatColor.RED + "This player has no spells.");
							}
						} else {
							s.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
						}
					}
				} else {
					s.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				}
			}
		}
		return false;
	}
	
	public boolean hasSpell(String player, String spell) {
		if (spells.containsKey(player)) {
			ArrayList<String> temp = spells.get(player);
			if (!temp.contains(spell)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
