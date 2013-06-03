package me.dbizzzle.SkyrimRPG.spell;

import me.dbizzzle.SkyrimRPG.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpellUtil 
{
	private static boolean checkMagicka(Player player,PlayerData data, Spell spell)
	{
		if(data.getMagicka() < spell.getBaseCost())
		{
			player.sendMessage(ChatColor.RED + "You need at least " + spell.getBaseCost() + " magicka to cast " + spell.getDisplayName() + "; You have " + data.getMagicka() + "!");
			return false;
		}
		return true;
	}
	public static void castSpell(Player player, PlayerData data, Spell spell)
	{
		if(!checkMagicka(player, data, spell))return;
		if(spell != null && player != null)
		{
			data.subtractMagicka(spell.getBaseCost());
			spell.cast(player, player.getServer(), data);
		}
	}
	public static void magickaWarning(Player p, String s)
	{
		p.sendMessage(ChatColor.RED + "You do not have enough magicka to cast " + s + "!");
	}
	public static boolean useSpellBook(Player p, PlayerData data, Spell s)
	{
		if(data.hasSpell(s))
		{
			p.sendMessage(ChatColor.RED + "You already know how to cast " + s.toString());
			return false;
		}
		data.addSpell(s);
		p.sendMessage(ChatColor.GREEN + "You have learned how to cast " + s.toString());
		return true;
	}
}
