package me.dbizzzle.SkyrimRPG.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class SkillLevelChangeEvent extends PlayerEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private int levelto;
	private boolean cancelled = false;
	public SkillLevelChangeEvent(Player player, int levelto) 
	{
		super(player);
		this.levelto = levelto;
	}
	public int getLevelTo()
	{
		return levelto;
	}
	public void setLevelTo(int newlevelto)
	{
		levelto = newlevelto;
	}
	public HandlerList getHandlers() 
	{
		return handlers;
	}
	@Override
	public boolean isCancelled() 
	{
		return cancelled;
	}
	@Override
	public void setCancelled(boolean cancelled) 
	{
		this.cancelled = cancelled;
	}
	
}
