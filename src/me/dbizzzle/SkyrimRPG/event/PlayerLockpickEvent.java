package me.dbizzzle.SkyrimRPG.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerLockpickEvent extends PlayerEvent
{
	private static final HandlerList handlers = new HandlerList();
	private Block block;
	private boolean success;
	public PlayerLockpickEvent(Player player, Block block, boolean success)
	{
		super(player);
		this.block = block;
		this.success = success;
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
