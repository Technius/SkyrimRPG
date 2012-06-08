package me.dbizzzle.SkyrimRPG;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DiseaseManager 
{
	public HashMap<Player, List<Disease>> diseasePlayer = new HashMap<Player, List<Disease>>();
	
	public enum Disease
	{
		BRAIN_ROT(1);
		private int id;
		private Disease(int id)
		{
			this.id = id;
		}
		
		public int getId(Disease d)
		{
			return id;
		}
		public static Disease getById(int id)
		{
			for(Disease d : Disease.values())
			{
				if(d.getId(d) == id)
					return d;
			}
			return null;
		}
	}
	
	public boolean hasDisease(Player p, Disease d)
	{
		return diseasePlayer.get(p).contains(d);
	}
	
	public void addDisease(Player p, Disease d)
	{
		if(!diseasePlayer.get(p).contains(d))
			diseasePlayer.get(p).add(d);
	}
	
	public void brainRot(Player p)
	{
		//fix later
		//SpellManager.magicka.put(p, SpellManager.magicka.get(p) - 25);
	}
	
	public void cureDisease(Player p, Disease d)
	{
		if(diseasePlayer.get(p).contains(d))
			diseasePlayer.get(p).remove(d);
		else
			p.sendMessage(ChatColor.GRAY + "That player does not have that disease.");
	}
}
