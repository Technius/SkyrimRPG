package me.dbizzzle.SkyrimRPG;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ToolComparer 
{
	public static String getType(ItemStack tool)
	{
		Material tt = tool.getType();
		if(tt==Material.IRON_SWORD||tt==Material.WOOD_SWORD||tt==Material.STONE_SWORD||tt==Material.GOLD_SWORD||tt==Material.DIAMOND_SWORD)return "Sword";
		else return null;
	}
}
