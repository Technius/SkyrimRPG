package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.SkillManager;
import me.dbizzzle.SkyrimRPG.SpellManager.Spell;
import org.bukkit.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyrimCmd implements CommandExecutor
{
	public SpellManager sm;
	public SkyrimRPG plugin;
	public ConfigManager cm;
	public SkillManager sk;
	public SkyrimCmd(SpellManager sm, SkyrimRPG plugin, ConfigManager cm, SkillManager sk)
	{
		this.sm = sm;
		this.sk = sk;
		this.cm = cm;
		this.plugin = plugin;
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
					if(!sm.hasSpell(player, s))
					{
						player.sendMessage(ChatColor.RED + "You have not yet learned this spell.");
						return true;
					}
					else
					{
						if(s == Spell.FIREBALL)
						{
							SpellManager.boundleft.put(player, Spell.FIREBALL);
							SpellManager.boundright.put(player, Spell.UFIREBALL);
						}
						else
						{
							if(mode == 1 ||mode == 3)SpellManager.boundleft.put(player, s);
							if(mode == 2 ||mode == 3)SpellManager.boundright.put(player, s);
						}
						String sl = s.toString().toLowerCase().replaceAll("_", " ");
						String[] st = sl.split("[ ]");
						String m = Character.toUpperCase(st[0].charAt(0)) + st[0].substring(1);
						for(String x: st)
						{
							if(x.equalsIgnoreCase(st[0]))continue;
							m = m + " " + Character.toUpperCase(x.charAt(0)) + x.substring(1);
						}
						if(mode != 3)player.sendMessage(ChatColor.GREEN + m + " bound to " + (mode == 1 ? "left" : "right") + " hand");
						else player.sendMessage(ChatColor.GREEN + m + " bound to both hands");
					}
				}
			}
			else
			{
				player.sendMessage("You aren't allowed to bind spells.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("addspell")) 
		{
			if (player == null) 
			{
				Player spell = plugin.getServer().getPlayer(args[0]);
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
							sm.addSpell(spell, s);
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
				Player spell = player.getServer().getPlayer(args[0]);
				if (spell != null) 
				{
					if(args.length == 2)
					{
						Spell s = SpellManager.Spell.valueOf(args[1].toUpperCase());
						int a = s.getId(s);
						if(sm.spellExists(a) == true)
						{
							sender.sendMessage(ChatColor.GREEN + "You have given the spell " + args[1] + " to " + spell.getName() + ".");
							spell.sendMessage(ChatColor.GREEN + "You have been given the spell " + args[1] + ".");
							sm.addSpell(player, s);
						}
						else
						{
							player.sendMessage(ChatColor.RED + "That spell does not exist.");
						}
					}
					else 
					{
						player.sendMessage(ChatColor.RED + "Usage: /addspell <player> <spell>");
					}
				} 
				else 
				{
					player.sendMessage(ChatColor.RED + args[0] + " is currently not available or not online.");
				}
			}
			else
			{
				player.sendMessage(ChatColor.RED + "You do no have permission to use this command");
			}
		}
		
		else if (command.getName().equalsIgnoreCase("removespell")) 
		{
			if (player == null) 
			{
				Player spell = plugin.getServer().getPlayer(args[0]);
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
						Spell s = null;
						try
						{
							s = SpellManager.Spell.valueOf(args[1]);
						}
						catch(IllegalArgumentException iae){if(s == null)sender.sendMessage("No such spell!");return true;}
						sender.sendMessage(ChatColor.GREEN + "You have taken the spell " + args[1] + " from " + spel + ".");
						spell.sendMessage(ChatColor.GREEN + "The spell " + args[1] + " has been taken from you.");
						sm.removeSpell(player, s);
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
				Player spell = player.getServer().getPlayer(args[0]);
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
						Spell s = null;
						try
						{
							s = SpellManager.Spell.valueOf(args[1].toUpperCase());
						}
						catch(IllegalArgumentException iae){if(s == null)sender.sendMessage("No such spell!");return true;}
						sender.sendMessage(ChatColor.GREEN + "You have taken the spell " + args[1] + " from " + spel + ".");
						spell.sendMessage(ChatColor.GREEN + "The spell " + args[1] + " has been taken from you.");
						sm.removeSpell(player, s);
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
					Player spell = plugin.getServer().getPlayer(args[0]);
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
							case FLAMES:
								sender.sendMessage("Flames");
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
						case FLAMES:
							sender.sendMessage("Flames");
							break;
						}
					}
				} 
				else if (args.length > 0) 
				{
					Player spell = player.getServer().getPlayer(args[0]);
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
							case FLAMES:
								sender.sendMessage("Flames");
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
				System.out.println("[SkyrimRPG]SkyrimRPG version " + plugin.getDescription().getVersion());
				System.out.println("[SkyrimRPG]Made by dbizzle and Technius");
			}
			else
			{
				switch(args.length)
				{
				case 0:
					player.sendMessage(ChatColor.YELLOW + "SkyrimRPG version " + plugin.getDescription().getVersion());
					player.sendMessage(ChatColor.GREEN + "Made by dbizzle and Technius");
					player.sendMessage("========================");
					player.sendMessage(ChatColor.RED + "/skystats <page>" + ChatColor.YELLOW + " - displays your stats");
					if(player.hasPermission("skyrimrpg.setlevel"))player.sendMessage(ChatColor.RED + "/skyrimrpg setlevel <skill> <level>" + ChatColor.YELLOW + " - sets the level of the specified skill");
					if(player.hasPermission("skyrimrpg.reload"))player.sendMessage(ChatColor.RED + "/skyrimrpg reload" + ChatColor.YELLOW + " - reloads the configuration file");
					if(player.hasPermission("skyrimrpg.refresh"))player.sendMessage(ChatColor.RED + "/skyrimrpg refresh" + ChatColor.YELLOW + " - refreshes the configuration file by adding new values, useful when updating");
					break;
				case 3:
					if(args[0].equalsIgnoreCase("setlevel"))
					{
						if(!player.hasPermission("skyrimrpg.setlevel"))
						{
							player.sendMessage(ChatColor.RED + "You don't have permission to do this");
							return true;
						}
						String skill = args[1].toLowerCase();
						if(skill.length() == 1)
						{
							player.sendMessage(ChatColor.RED + "No such skill!");
							return true;
						}
						String s = Character.toUpperCase(skill.charAt(0)) + skill.substring(1);
						if(!SkillManager.skills.get(player).containsKey(s))
						{
							player.sendMessage(ChatColor.RED + "No such skill!");
							return true;
						}
						int l = SkillManager.getSkillLevel(s, player);
						try{ l = Integer.parseInt(args[2]);}catch(NumberFormatException nfe){sender.sendMessage(ChatColor.RED + "That is not a valid number."); return true;}
						sk.setLevel(s, player, l);
						player.sendMessage(ChatColor.GREEN + s + " set to level " + l);
						return true;
					}
					break;
				case 1:
					if(args[0].equalsIgnoreCase("reload"))
					{
						if(!player.hasPermission("skyrimrpg.reload"))
						{
							player.sendMessage(ChatColor.RED + "You don't have permission to do this");
							return true;
						}
						if(!plugin.checkFiles())
						plugin.createFiles();
						cm.loadConfig();
						player.sendMessage(ChatColor.GREEN + "Configuration file reloaded successfully.");
					}
					else if(args[0].equalsIgnoreCase("refresh"))
					{
						if(!player.hasPermission("skyrimrpg.refresh"))
						{
							player.sendMessage(ChatColor.RED + "You don't have permission to do this");
							return true;
						}
						if(!plugin.checkFiles())plugin.createFiles();
						cm.refreshConfig();
						player.sendMessage(ChatColor.GREEN + "Configuration file refreshed successfully.");
					}
					break;
				}
			}
		}
		else if(command.getName().equalsIgnoreCase("skystats"))
		{
			if(player == null)
			{
				System.out.println("[SkyrimRPG]You don't have stats!");
			}
			else
			{
				int page = 1;
				if(args.length != 0 && args.length != 1)
				{
					player.sendMessage(ChatColor.RED + "Usage: /skystats <page>");
					return true;
				}
				if(args.length == 1)
				{
					try{page = Integer.parseInt(args[0]);}catch(NumberFormatException nfe){page = 1;}
				}
				if(page <= 0)page = 1;
				switch(page)
				{
				case 1:
					player.sendMessage(ChatColor.GOLD + "Stats Page 1 of 2");
					player.sendMessage(ChatColor.RED + "Combat" + ChatColor.WHITE + "|" + ChatColor.BLUE + "Magic" + ChatColor.WHITE + "|" + ChatColor.GRAY + "Stealth");
					player.sendMessage(ChatColor.GREEN + "Level: " + SkillManager.level.get(player));
					player.sendMessage(ChatColor.BLUE + "Magicka: " + SpellManager.magicka.get(player));
					player.sendMessage(ChatColor.RED + "Archery: Level " + SkillManager.getSkillLevel("Archery", player));
					player.sendMessage(ChatColor.RED + "Swordsmanship: Level " + SkillManager.getSkillLevel("Swordsmanship", player));
					player.sendMessage(ChatColor.RED + "Axecraft: Level " + SkillManager.getSkillLevel("Axecraft", player));
					player.sendMessage(ChatColor.RED + "Blocking: Level " + SkillManager.getSkillLevel("Blocking", player));
					player.sendMessage(ChatColor.BLUE + "Destruction: Level " + SkillManager.getSkillLevel("Destruction", player));
					player.sendMessage(ChatColor.BLUE + "Conjuration: Level " + SkillManager.getSkillLevel("Conjuration", player));
					break;
				case 2:
					player.sendMessage(ChatColor.GOLD + "Stats Page 2 of 2");
					player.sendMessage(ChatColor.GRAY + "Pickpocketing: Level " + SkillManager.getSkillLevel("PickPocket", player));
					player.sendMessage(ChatColor.GRAY + "Lockpicking: Level " + SkillManager.getSkillLevel("Lockpicking", player));
					break;
				default:
					player.sendMessage(ChatColor.GOLD + "Stats Page " + page + " of 2");
					break;
				}
			}
		}
		return true;
	}
}
