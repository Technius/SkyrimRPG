package com.Technius.SkySpout;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class SkySpout 
{
	Plugin spoutraw = null;
	public boolean checkSpout(PluginManager pm)
	{
		spoutraw = pm.getPlugin("Spout");
		if(spoutraw!= null)return true;
		else return false;
	}
}
