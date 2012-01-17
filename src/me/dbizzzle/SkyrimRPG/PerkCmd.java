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
						player.sendMessage(ChatColor.GREEN + "Overdraw successfully added at level " + l);
					}
				}
				else
				{
					sender.sendMessage("Usage: /addperk <player> <perk> <level>");
				}
				return true;
			case 3:
				return true;
			default:
				if(player == null)sender.sendMessage("Usage: /addperk <player> <perk> <level>");
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
