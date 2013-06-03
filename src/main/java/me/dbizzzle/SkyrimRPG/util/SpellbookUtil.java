package me.dbizzzle.SkyrimRPG.util;

import me.dbizzzle.SkyrimRPG.spell.Spell;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class SpellbookUtil 
{
	private static final String author = ChatColor.GOLD + "SkyrimRPG";
	private static final String title = ChatColor.YELLOW + "Spell Tome: ";
	public static ItemStack createSpellbook(Spell spell)
	{
		ItemStack i = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta im = (BookMeta)i.getItemMeta();
		im.setTitle(ChatColor.YELLOW + "Spell Tome: " + spell.getDisplayName());
		im.setAuthor(ChatColor.GOLD + "SkyrimRPG");
		i.setItemMeta(im);
		return i;
	}
	public static Spell getSpellbookSpell(ItemStack item)
	{
		if(item.getType() != Material.WRITTEN_BOOK)return null;
		if(!item.hasItemMeta())return null;
		BookMeta im = (BookMeta)item.getItemMeta();
		if(!im.hasTitle() || !im.hasAuthor())return null;
		if(!im.getAuthor().equals(author))return null;
		if(!im.getTitle().startsWith(title))return null;
		String[] t = im.getTitle().split(" ", 3);
		if(t.length != 3)return null;
		String sname = t[2];
		Spell s = Spell.get(sname);
		return s;
	}
}
