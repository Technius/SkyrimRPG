package me.dbizzzle.SkyrimRPG.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerPickpocketEvent extends org.bukkit.event.Event
{
	private static final HandlerList handlers = new HandlerList();
	private Player victim;
	private Player player;
	private boolean success;
	public PlayerPickpocketEvent(Player player, Player victim, boolean success)
	{
		this.victim = victim;
		this.player = player;
		this.success = success;
	}
	/**
	 * 
	 * @return If the pickpocket was successful
	 */
	public boolean isSuccessful()
	{
		return success;
	}
	/**
	 * 
	 * @return The player who is being pickpocketed
	 */
	public Player getVictim()
	{
		return victim;
	}
	/**
	 * 
	 * @return The player who is pickpocketing
	 */
	public Player getPlayer()
	{
		return player;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
