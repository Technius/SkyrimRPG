package me.dbizzzle.SkyrimRPG.api;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;
import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.PerkManager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Main API class of SkyrimRPG
 *
 */
public class SkyUtility 
{
	private SkyrimRPG p;
	/**
	 * Main API class of SkyrimRPG
	 * @param SkyrimRPG - The plugin that represents SkyrimRPG
	 * @throws ClassCastException If the plugin provided is not SkyrimRPG
	 */
	public SkyUtility(Plugin SkyrimRPG)
	{
		if(p instanceof SkyrimRPG)this.p = (SkyrimRPG) SkyrimRPG;
		else throw new ClassCastException("This \"" + SkyrimRPG.getDescription().getName() + "\"is not SkyrimRPG!");
	}
	/**
	 * Checks if the player has the specified perk
	 * @param player - The player
	 * @param perk - The perk
	 * @return True if the player has the perk, otherwise false
	 */
	public boolean hasPerk(Player player, Perk perk)
	{
		return PerkManager.perks.get(player).containsKey(perk);
	}
	/**
	 * Returns the level of the specified perk the player has
	 * @param player - The player
	 * @param perk - The perk
	 * @return -1 if the player doesn't have the perk, otherwise the level of the perk
	 */
	public int getPerkLevel(Player player, Perk perk)
	{
		if(!hasPerk(player, perk))return -1;
		return PerkManager.perks.get(player).get(perk);
	}
}
