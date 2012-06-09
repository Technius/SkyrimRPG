package me.dbizzzle.SkyrimRPG.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerLockpickEvent extends org.bukkit.event.Event
{
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private Block block;
	private boolean success;
	public PlayerLockpickEvent(Player player, Block block, boolean success)
	{
		this.player = player;
		this.block = block;
		this.success = success;
	}
	/**
	 * 
	 * @return The player who is doing the lockpicking
	 */
	public Player getPlayer()
	{
		return player;
	}
	/**
	 * 
	 * @return The block being lockpicked
	 */
	public Block getBlock()
	{
		return block;
	}
	/**
	 * 
	 * @return If the lockpick is successful
	 */
	public boolean isSuccessful()
	{
		return success;
	}
	public HandlerList getHandlers() 
	{
		return handlers;
	}

}
