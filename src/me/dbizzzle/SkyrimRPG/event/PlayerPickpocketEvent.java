package me.dbizzzle.SkyrimRPG.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerPickpocketEvent extends org.bukkit.event.Event implements org.bukkit.event.Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private Player victim;
	private Player player;
	private boolean success;
	private boolean cancelled;
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
	/**
	 * 
	 * @return If the event is cancelled.
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	/**
	 * If the event is cancelled and the pickpocket was successful,
	 * the inventory will not open and no experience will be given.
	 * @param cancel - If the event is cancelled or not.
	 */
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
