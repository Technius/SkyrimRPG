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
		Player player = null;
		if(sender instanceof Player)player = (Player)sender;
		
		if (label.equalsIgnoreCase("fireball")) {
			if (player == null) 
			{
				sender.sendMessage(ChatColor.RED + "Console can not use magic.");
			} 
			else if (player.hasPermission("skyrimrpg.fireball")) 
			{
				if (hasSpell(se, "fireball")) 
				{
					final Vector direction = s.getEyeLocation().getDirection().multiply(2);
					Fireball fireball = s.getWorld().spawn(s.getEyeLocation().add(direction.getX(), direction.getY(), 
						direction.getZ()), Fireball.class);
					fireball.setShooter(s);
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
							s.sendMessage(ChatColor.RED + "Usage: <player> <spell>");
							break;
							
						case 1:
							s.sendMessage(ChatColor.RED + "Usage: <player> <spell>");
							break;
							
						default:
							String spel = spell.getName();
							temp.add(args[1]);
							spells.put(spel, temp);
							s.sendMessage(ChatColor.GREEN + "You have given the spell " + args[1] + " to " + spel + ".");
							spell.sendMessage(ChatColor.GREEN + "You have been given the spell " + args[1] + ".");
							break;
					}
				} 
			}
			else 
			{
				s.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
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
			else if (s.hasPermission("SkyrimRPG.removespell")) 
			{
				Player spell = getServer().getPlayer(args[0]);
				if (spell != null) 
				{
					switch (args.length) 
					{
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
				} 
				else 
				{
					s.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
				}
			} 
			else 
			{
				s.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
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
					s.sendMessage(ChatColor.GREEN + "fireball");
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
								s.sendMessage(ChatColor.GREEN + "This player has no spells.");
							} 
							else 
							{
								s.sendMessage(ChatColor.GREEN + temp.toString());
							}
						} 
						else 
						{
							s.sendMessage(ChatColor.RED + "This player has no spells.");
						}
					} 
					else 
					{
						s.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
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
}
