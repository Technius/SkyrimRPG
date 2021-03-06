package me.dbizzzle.SkyrimRPG;

import me.dbizzzle.SkyrimRPG.skill.Perk;
import me.dbizzzle.SkyrimRPG.skill.Skill;

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
	public void menu(CommandSender sender)
	{
		if(sender instanceof Player)
		{
			sender.sendMessage(ChatColor.GOLD + "Perk Menu");
			sender.sendMessage(ChatColor.GOLD + "Available Perk Points: " + ChatColor.BLUE + plugin.getPlayerManager().getData(sender.getName()).getPerkPoints());
			sender.sendMessage(ChatColor.YELLOW + "<Required>" + ChatColor.GOLD + "[Optional]");
			sender.sendMessage(ChatColor.GREEN + "/perk unlock <perk> [level] " + ChatColor.RED + "- Unlocks a perk, if all requirements have been met");
			sender.sendMessage(ChatColor.GREEN + "/perk list [page number] " + ChatColor.RED + "- Lists your perks");
			sender.sendMessage(ChatColor.GREEN + "/perk all [skill] " + ChatColor.RED + "- Lists all the requirements about the perks of that skill");
			if(sender.hasPermission("skyrimrpg.addperk"))sender.sendMessage(ChatColor.GREEN + "/addperk [player] <perk> <level> " + ChatColor.RED + "- Adds a perk to specified player");
			if(sender.hasPermission("skyrimrpg.removeperk"))sender.sendMessage(ChatColor.GREEN + "/removeperk <player> <perk> " + ChatColor.RED + "- Removes a perk from specified player");
		}
		else
		{
			sender.sendMessage("/addperk <player> <perk> <level> - Adds a perk to specified player");
			sender.sendMessage("/removeperk <player> <perk> - Removes a perk from specified player");
		}
	}
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		Player player = null;
		if(sender instanceof Player)player = (Player)sender;
		if(command.getName().equalsIgnoreCase("perk"))
		{
			if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("list"))
				{
					if(args.length != 1 && args.length != 2)return usage(sender, "perk list [player]");
					Player t = null;
					if(args.length == 1)
					{
						if(player == null)return usage(sender, "perk list <player>");
						else t = player;
					}
					else
					{
						t = sender.getServer().getPlayer(args[1]);
						if(t == null)return msg(sender, ChatColor.RED + "No such player: " + args[1]);
					}
					int ct = 0;
					PlayerData pd = plugin.getPlayerManager().getData(t.getName());
					Perk[] ps = pd.getPerks();
					int pg = ps.length/10;
					if(pg < (double)ps.length/10)pg++;
					sender.sendMessage(ChatColor.GOLD + (t == player ? "Your"  : t.getName() + "'s") + " perks (" + ChatColor.RED + "1" + ChatColor.GOLD + "/" + ChatColor.GREEN + pg + ")");
					for(Perk p:ps)
					{
						if(ct == 9)break;
						ChatColor c;
						if(p.getSkill().isCombat())c = ChatColor.DARK_RED;
						else if(p.getSkill().isMagic())c = ChatColor.BLUE;
						else if(p.getSkill().isThief())c = ChatColor.GRAY;
						else c = ChatColor.WHITE;
						int l = pd.getPerkLevel(p);
						if(p.getMaxLevel() == 1)player.sendMessage(c + p.getName());
						else player.sendMessage(c + p.getName() + " " + ChatColor.GOLD + "(" + l + "/" + p.getMaxLevel() + ")");
						ct++;
					}
				}
				else if(args[0].equalsIgnoreCase("unlock"))
				{
					if(player == null)return noConsole(sender);
					if(args.length != 2 && args.length != 3)return usage(sender, "perk unlock <perk> [level]");
					Perk p = Perk.getPerk(args[1]);
					if(p == null)return msg(sender, ChatColor.RED + "No such perk: " + args[1]);
					PlayerData pd = plugin.getPlayerManager().getData(player.getName());
					int level = 1;
					if(args.length == 3)
					{
						try{level = Integer.parseInt(args[2]);}catch(NumberFormatException nfe){player.sendMessage(ChatColor.RED + "Not a number: " + args[2]);return true;}
					}
					if(pd.getPerkPoints() <= 0)return msg(sender, ChatColor.RED + "You don't have enough perk points!");
					if(level < 1)return msg(sender, ChatColor.RED + "The level must be greater than 0!");
					if(level > p.getMaxLevel())return msg(sender, ChatColor.RED + "The maximum level of " + p.getName() + " is " + p.getMaxLevel() + "!");
					if(level == 1)
					{
						if(pd.hasPerk(p))
							return msg(sender, ChatColor.RED + "You already have the perk " + p.getName() + "!");
						if(!pd.canUnlockPerk(p, level))
							return msg(sender, ChatColor.RED + "You don't meet the requirements to unlock this perk.");
						pd.addPerk(p);
						pd.setPerkPoints(pd.getPerkPoints() - 1);
						player.sendMessage(ChatColor.GREEN + "You have unlocked " + ChatColor.RED + p.getName() + ChatColor.GREEN + "!");
					}
					else
					{
						if(!pd.hasPerk(p))
							return msg(sender, ChatColor.RED + "You don't have the perk " + p.getName() + "!");
						if(!pd.canUnlockPerk(p, level))
							return msg(sender, ChatColor.RED + "You don't meet the requirements to unlock this perk.");
						pd.setPerkPoints(pd.getPerkPoints() - 1);
						pd.setPerkLevel(p, level);
						player.sendMessage(ChatColor.GREEN + "Perk " + ChatColor.RED + p.getName() + ChatColor.GREEN + " increased to level " + level + "!");
					}
					
				}
				else if(args[0].equalsIgnoreCase("all"))
				{
					if(player == null)return noConsole(sender);
					Skill s;
					s = Skill.getSkill(args[1]);
					if(s == null)return msg(player, ChatColor.RED + "No such skill: " + args[1]);
					PlayerData pd = plugin.getPlayerManager().getData(player.getName());
					player.sendMessage(ChatColor.RED + s.getName() + " Perks" + ChatColor.GOLD +" - Required Skill Level - Max Level");
					for(Perk p:Perk.getPerksBySkill(s))
					{
						String rsl;
						if(pd.getPerkLevel(p) >= p.getRequiredSkillLevels().length)rsl = "MAX";
						else rsl = "" + p.getRequiredSkillLevels()[pd.getPerkLevel(p)];
						if(pd.hasPerk(p))player.sendMessage(ChatColor.RED + p.getName() + ChatColor.GOLD +" - " + rsl + " - " + p.getMaxLevel());
						else player.sendMessage(ChatColor.RED + p.getName() + ChatColor.GOLD +" - " + p.getRequiredSkillLevels()[0] + " - " + p.getMaxLevel());
					}
				}
				else sender.sendMessage(ChatColor.RED + "Unrecognized command: " + args[0]);
			}
			else menu(sender);
		}
		else if(command.getName().equalsIgnoreCase("addperk"))
		{
			if(sender.hasPermission("skyrimrpg.addperk"))
			{
				if(args.length != 2)return usage(sender, "addperk <player> <perk>");
				Player t = plugin.getServer().getPlayer(args[0]);
				if(t == null)
				{
					sender.sendMessage(ChatColor.RED + "No such player!");
					return true;
				}
				Perk p;
				try
				{
					p = Perk.valueOf(args[1].toUpperCase());
				}
				catch(IllegalArgumentException iae)
				{
					sender.sendMessage(ChatColor.RED + "No such perk: " + args[1]);
					return true;
				}
				int l;
				try
				{
					l = Integer.parseInt(args[2]);
					if(l < 1)return msg(sender, ChatColor.RED + "Perk level has to be greater than 0");
					if(l>p.getMaxLevel())
						return msg(sender, ChatColor.RED + p.getName() + " has a max level of " + p.getMaxLevel() + "!");
				}
				catch(NumberFormatException nfe)
				{
					player.sendMessage(ChatColor.RED + "That is not a valid number.");
					return true;
				}
				plugin.getPlayerManager().getData(t.getName()).setPerkLevel(p, l);
				sender.sendMessage(ChatColor.GREEN + p.getName() + " successfully added to " + t.getName() + " at level " + l);
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
					return usage(sender, "/removeperk <player> <perk>");
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
				PlayerData pd = plugin.getPlayerManager().getData(t.getName());
				if(!pd.hasPerk(p))
				{
					sender.sendMessage(ChatColor.RED + args[0] + " doesn't have the perk " + p.getName());
					return true;
				}
				pd.removePerk(p);
				sender.sendMessage(ChatColor.RED + p.getName() + " removed from " + args[0]);
				t.sendMessage(ChatColor.GREEN + "The perk " + p.getName() + " has been taken away from you.");
			}
			else sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
		}
		return true;
	}
	private boolean msg(CommandSender sender, String msg)
	{
		sender.sendMessage(msg);
		return true;
	}
	private boolean noConsole(CommandSender sender)
	{
		sender.sendMessage(ChatColor.RED + "Console cannot use this command!");
		return true;
	}
	private boolean usage(CommandSender sender, String command)
	{
		sender.sendMessage(ChatColor.RED + "Usage: " + (sender instanceof Player ? "/" : "") + command);
		return true;
	}
}
