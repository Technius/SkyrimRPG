package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.PerkManager;
import me.dbizzzle.SkyrimRPG.Skill.Skill;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerkCmd implements CommandExecutor
{
	private SkyrimRPG plugin;
	public PerkCmd(SkyrimRPG plugin)
	{
		this.plugin = plugin;
	}
	public void menu(Player player)
	{
		if(player != null)
		{
			player.sendMessage(ChatColor.GOLD + "Perk Menu");
			player.sendMessage(ChatColor.GOLD + "Available Perk Points: " + ChatColor.BLUE + plugin.getPerkManager().getPoints(player));
			player.sendMessage(ChatColor.YELLOW + "<Required>" + ChatColor.GOLD + "[Optional]");
			player.sendMessage(ChatColor.GREEN + "/perk unlock <perk> [level] " + ChatColor.RED + "- Unlocks a perk, if all requirements have been met");
			player.sendMessage(ChatColor.GREEN + "/perk list [page number] " + ChatColor.RED + "- Lists your perks");
			player.sendMessage(ChatColor.GREEN + "/perk all [skill] " + ChatColor.RED + "- Lists all the requirements about the perks of that skill");
			if(player.hasPermission("skyrimrpg.addperk"))player.sendMessage(ChatColor.GREEN + "/addperk [player] <perk> <level> " + ChatColor.RED + "- Adds a perk to specified player");
			if(player.hasPermission("skyrimrpg.removeperk"))player.sendMessage(ChatColor.GREEN + "/removeperk <player> <perk> " + ChatColor.RED + "- Removes a perk from specified player");
		}
		else
		{
			plugin.log.info("[SkyrimRPG]/addperk <player> <perk> <level> - Adds a perk to specified player");
			plugin.log.info("[SkyrimRPG]/removeperk <player> <perk> - Removes a perk from specified player");
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		Player player = null;
		if(sender instanceof Player)player = (Player)sender;
		PerkManager pm = plugin.getPerkManager();
		if(command.getName().equalsIgnoreCase("perk"))
		{
			if(player == null)
			{
				menu(player);
				return true;
			}
			switch(args.length)
			{
			case 1:
				if(args[0].equalsIgnoreCase("list"))
				{
					int ct = 0;
					Perk[] ps = pm.getPerks(player);
					int pg = ps.length/10;
					if(pg < (double)ps.length/10)pg++;
					player.sendMessage(ChatColor.GOLD + "Perks list " + ChatColor.RED + "1" + ChatColor.GOLD + " of " + ChatColor.GREEN + pg);
					for(Perk p:ps)
					{
						if(ct == 9)break;
						ChatColor c;
						switch(p.getSkill())
						{
						case ARCHERY: c = ChatColor.DARK_RED; break;
						case SWORDSMANSHIP: c = ChatColor.DARK_RED; break;
						case BLOCKING: c = ChatColor.DARK_RED; break;
						case SNEAK: c = ChatColor.GRAY; break;
						default: c = ChatColor.WHITE; break;
						}
						int l = pm.getPerkLevel(player, p);
						if(p.getMaxLevel() == 1)player.sendMessage(c + p.getName());
						else player.sendMessage(c + p.getName() + " " + ChatColor.GOLD + "(" + l + "/" + p.getMaxLevel() + ")");
						ct++;
					}
					return true;
				}
				else if(args[0].equalsIgnoreCase("unlock"))
				{
					player.sendMessage(ChatColor.RED + "Usage: /perk unlock <perk> [level]");
					return true;
				}
				else
				{
					menu(player);
					return true;
				}
			case 2:
				if(args[0].equalsIgnoreCase("unlock"))
				{
					Perk p;
					try
					{
						p = Perk.valueOf(args[1].toUpperCase());
					}
					catch(IllegalArgumentException iae)
					{
						player.sendMessage(ChatColor.RED + "No such perk: " + args[1]);
						return true;
					}
					if(pm.hasPerk(player, p))
					{
						player.sendMessage(ChatColor.RED + "Use /perk unlock <perk> <level>!");
						return true;
					}
					else if(pm.hasEnough(player))
					{
						if(pm.canUnlock(player, p, 1))
						{
							pm.unlock(player, p, 1);
							player.sendMessage(ChatColor.GREEN + "You have unlocked " + ChatColor.RED + p.getName() + ChatColor.GREEN + "!");
						}
						else player.sendMessage(ChatColor.RED + "You have not met the requirements to unlock this perk.");
					}
					else player.sendMessage(ChatColor.RED + "You don't have enough perk points!");
					return true;
				}
				else if(args[0].equalsIgnoreCase("all"))
				{
					Skill s;
					try{s = Skill.valueOf(args[1].toUpperCase());}catch(IllegalArgumentException iae){player.sendMessage(ChatColor.RED + "No such skill: " + args[1]);return true;}
					player.sendMessage(ChatColor.RED + s.getName() + " Perks" + ChatColor.GOLD +" - Required Skill Level - Max Level");
					for(Perk p:pm.getPerksBySkill(s))
					{
						String rsl;
						if(pm.getPerkLevel(player, p) >= p.getRequiredSkillLevels().length)rsl = "MAX";
						else rsl = "" + p.getRequiredSkillLevels()[pm.getPerkLevel(player, p)];
						if(pm.hasPerk(player, p))player.sendMessage(ChatColor.RED + p.getName() + ChatColor.GOLD +" - " + rsl + " - " + p.getMaxLevel());
						else player.sendMessage(ChatColor.RED + p.getName() + ChatColor.GOLD +" - " + p.getRequiredSkillLevels()[0] + " - " + p.getMaxLevel());
					}
					return true;
				}
				else
				{
					menu(player);
					return true;
				}
			case 3:
				if(args[0].equalsIgnoreCase("unlock"))
				{
					Perk p;
					try
					{
						p = Perk.valueOf(args[1].toUpperCase());
					}
					catch(IllegalArgumentException iae)
					{
						player.sendMessage(ChatColor.RED + "No such perk: " + args[1]);
						return true;
					}
					int level;
					try{ level = Integer.parseInt(args[2]);}catch(NumberFormatException nfe){player.sendMessage("Not a number: " + args[2]);return true;}
					if(!pm.hasPerk(player, p))
					{
						player.sendMessage(ChatColor.RED + "Use /perk unlock <perk>!");
						return true;
					}
					else if(pm.getPerkLevel(player, p) >= level)
					{
						player.sendMessage(ChatColor.RED + "You already unlocked level " + level + "!");
						return true;
					}
					else if(level - pm.getPerkLevel(player, p) != 1)
					{
						player.sendMessage(ChatColor.RED + "You must first unlock level " + (pm.getPerkLevel(player, p) + 1));
						return true;
					}
					else if(pm.hasEnough(player))
					{
						if(pm.canUnlock(player, p, level))
						{
							pm.unlock(player, p, level);
							player.sendMessage(ChatColor.GREEN + "You have unlocked " + ChatColor.RED + p.getName() + ChatColor.GREEN + "!");
						}
						else player.sendMessage(ChatColor.RED + "You have not met the requirements to unlock this perk.");
					}
					else player.sendMessage(ChatColor.RED + "You don't have enough perk points!");
					return true;
				}
				else
				{
					menu(player);
					return true;
				}
			default:
				menu(player);
				return true;
			}
		}
		else if(command.getName().equalsIgnoreCase("addperk"))
		{
			if(sender.hasPermission("skyrimrpg.addperk"))
			{
				Player t = plugin.getServer().getPlayer(args[0]);
				if(t == null)
				{
					player.sendMessage(ChatColor.RED + "No such player!");
					return true;
				}
				Perk p;
				try
				{
					p = Perk.valueOf(args[1].toUpperCase());
				}
				catch(IllegalArgumentException iae)
				{
					player.sendMessage(ChatColor.RED + "No such perk!");
					return true;
				}
				int l;
				try
				{
					l = Integer.parseInt(args[2]);
					if(l>p.getMaxLevel())
					{
						player.sendMessage(ChatColor.RED + p.getName() + " has a max level of " + p.getMaxLevel() +", you entered " + l + "!");
						return true;
					}
				}
				catch(NumberFormatException nfe)
				{
					player.sendMessage(ChatColor.RED + "That is not a valid number.");
					return true;
				}
				plugin.getPerkManager().addPerk(t, p, l);
				player.sendMessage(ChatColor.GREEN + p.getName() + " successfully added to " + t.getName() + " level " + l);
				t.sendMessage(ChatColor.GREEN + "You were given the perk " + p.getName() + " at level " + l);
			}
			else sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}
		else if(command.getName().equalsIgnoreCase("removeperk"))
		{
			if(sender.hasPermission("skyrimrpg.removeperk"))
			{
				if(args.length != 2)
				{
					sender.sendMessage(ChatColor.RED + "Usage: /removeperk <player> <perk>");
					return true;
				}
				Perk p;
				try
				{
					p = Perk.valueOf(args[1].toUpperCase());
				}catch(IllegalArgumentException iae){sender.sendMessage(ChatColor.RED + "No such perk!");return true;}
				Player t = plugin.getServer().getPlayer(args[0]);
				if(t == null)
				{
					sender.sendMessage(ChatColor.RED + "No such player: \"" + args[0] + "\"");
					return true;
				}
				if(!pm.hasPerk(t, p))
				{
					sender.sendMessage(ChatColor.RED + args[0] + " doesn't have the perk " + p.getName());
					return true;
				}
				pm.removePerk(t, p);
				sender.sendMessage(ChatColor.RED + p.getName() + " removed from " + args[0]);
				t.sendMessage(ChatColor.GREEN + "The perk " + p.getName() + " has been taken away from you.");
			}
			else sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
		}
		return true;
	}

}
