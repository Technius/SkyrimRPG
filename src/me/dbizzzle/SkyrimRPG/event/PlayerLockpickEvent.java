package me.dbizzzle.SkyrimRPG.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerLockpickEvent extends PlayerEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private Block block;
	private boolean success;
	private boolean c = false;
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
	public void setSuccessful(boolean s)
	{
		success = s;
	}
	public HandlerList getHandlers() 
	{
		return handlers;
	}
	@Override
	public boolean isCancelled() {
		return c;
	}
	@Override
	public void setCancelled(boolean cancel) {
		c = cancel;
	}

}
