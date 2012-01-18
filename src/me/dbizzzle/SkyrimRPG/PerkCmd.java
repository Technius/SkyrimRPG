package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.PerkManager;

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
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		Player player = null;
		if(sender instanceof Player)player = (Player)sender;
		if(command.getName().equalsIgnoreCase("perk"))
		{
			if(player == null)
			{
				plugin.log.info("You don't have perks!");
				return true;
			}
			player.sendMessage(ChatColor.GOLD + "Perk Menu");
			if(player.hasPermission("skyrimrpg.addperk"))player.sendMessage(ChatColor.GREEN + "/addperk [player] <perk> <level> " + ChatColor.RED + "- Adds a perk to specified player");
		}
		else if(command.getName().equalsIgnoreCase("addperk"))
		{
			switch(args.length)
			{
			case 2:
				if(player != null)
				{
					if(!player.hasPermission("skyrimrpg.addperk"))player.sendMessage(ChatColor.RED + "You don't haver permission to do this.");
					else
					{
						Perk p;
						try
						{
							p = Perk.valueOf(args[0].toUpperCase());
						}
						catch(IllegalArgumentException iae)
						{
							player.sendMessage("No such perk!");
							return true;
						}
						int l;
						try
						{
							l = Integer.parseInt(args[1]);
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
						PerkManager.perks.get(player).put(p, l);
						player.sendMessage(ChatColor.GREEN + p.getName() + " successfully added at level " + l);
					}
				}
				else
				{
					plugin.log.info("Usage: /addperk <player> <perk> <level>");
				}
				return true;
			case 3:
				if(player == null)
				{
					Player t = plugin.getServer().getPlayer(args[0]);
					if(t == null)
					{
						plugin.log.info("No such player!");
						return true;
					}
					Perk p;
					try
					{
						p = Perk.valueOf(args[1].toUpperCase());
					}
					catch(IllegalArgumentException iae)
					{
						sender.sendMessage("No such perk!");
						return true;
					}
					int l;
					try
					{
						l = Integer.parseInt(args[2]);
						if(l>p.getMaxLevel())
						{
							sender.sendMessage(p.getName() + " has a max level of " + p.getMaxLevel() +", you entered " + l + "!");
							return true;
						}
					}
					catch(NumberFormatException nfe)
					{
						sender.sendMessage("That is not a valid number.");
						return true;
					}
					PerkManager.perks.get(player).put(p, l);
					sender.sendMessage(p.getName() + " successfully added at level " + l);
				}
				else if(player.hasPermission("skyrimrpg.addperk"))
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
					PerkManager.perks.get(player).put(p, l);
					player.sendMessage(ChatColor.RED + p.getName() + " successfully added at level " + l);
				}
				return true;
			default:
				if(player == null)plugin.log.info("Usage: /addperk <player> <perk> <level>");
				else if(player.hasPermission("skyrimrpg.addperk"))
				{
					player.sendMessage(ChatColor.RED + "Usage: /addperk <player> <perk> <level>");
				}
				else player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
				return true;
			}
		}
		return true;
	}

}
