package me.dbizzzle.SkyrimRPG.api;

import me.dbizzzle.SkyrimRPG.ConfigManager;
import me.dbizzzle.SkyrimRPG.SkyrimRPG;
import me.dbizzzle.SkyrimRPG.Skill.Perk;
import me.dbizzzle.SkyrimRPG.Skill.PerkManager;
import me.dbizzzle.SkyrimRPG.Skill.Skill;
import me.dbizzzle.SkyrimRPG.Skill.SkillManager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Main API class of SkyrimRPG
 *
 */
public class SkyUtility 
{
	private SkyrimRPG p;
	private ConfigManager cm;
	private SkillManager sm;
	/**
	 * Main API class of SkyrimRPG
	 * @param SkyrimRPG - The plugin that represents SkyrimRPG
	 * @throws ClassCastException If the plugin provided is not SkyrimRPG
	 */
	public SkyUtility(Plugin SkyrimRPG)
	{
		if(p instanceof SkyrimRPG)
		{
			this.p = (SkyrimRPG) SkyrimRPG;
			cm = new ConfigManager(p);
			sm = new SkillManager(p);
		}
		else throw new ClassCastException("This plugin: \"" + SkyrimRPG.getDescription().getName() + "\"is not SkyrimRPG!");
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
	/**Checks if files and folders exists, and creates them if not
	 * @return True if all files and folders exist, otherwise false
	 * 
	 */
	public boolean checkFiles()
	{
		return p.checkFiles();
	}
	public int getLevel(Player player)
	{
		return SkillManager.level.get(player);
	}
	public int getSkillLevel(Player player, Skill skill)
	{
		return SkillManager.getSkillLevel(skill, player);
	}
}
