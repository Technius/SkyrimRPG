package me.dbizzzle.SkyrimRPG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import me.dbizzzle.SkyrimRPG.skill.Perk;
import me.dbizzzle.SkyrimRPG.spell.Spell;

public class PlayerManager 
{
	private File f;
	private File data;
	private File players;
	private File oldplayers;
	private File oldperks;
	private File oldmagic;
	private SkyrimRPG plugin;
	private ArrayList<PlayerData>pdata = new ArrayList<PlayerData>();
	public PlayerManager(SkyrimRPG plugin)
	{
		this.plugin = plugin;
		f = plugin.getDataFolder();
		data = new File(f, "Data");
		players = new File(data, "Players");
		oldplayers = new File(f, "Players");
		oldperks = new File(f, "Perks");
		oldmagic = new File(f, "Magic");
	}
	public void save()
	{
		if(!f.exists())f.mkdir();
		if(!data.exists())data.mkdir();
		if(!players.exists())players.mkdir();
		for(PlayerData data : pdata)
		{
			StringConfig conf = data.save();
			conf.setFile(new File(players, data.getPlayer() + ".txt"));
			try {
				conf.write();
				conf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void load()
	{
		if(!data.exists() && oldplayers.exists() && oldperks.exists() && oldmagic.exists())
		{
			//Use old data
			importOld();
		}
		else if(data.exists())
		{
			//Use existing data
			if(players.exists())
			{
				for(File f:players.listFiles())
				{
					if(!f.getName().endsWith(".txt"))return;
					String name = f.getName().substring(0, f.getName().length() - 4);
					StringConfig conf = new StringConfig(f);
					try {
						conf.load();
						PlayerData data = new PlayerData(name);
						data.load(conf);
						addData(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	public void importOld()
	{
		plugin.log.info("Importing old data...");
		final int curdata = pdata.size();
		if(oldplayers.exists())
		{
			for(File f:oldplayers.listFiles())
			{
				if(!f.getName().endsWith(".txt"))continue;
				String name = f.getName().substring(0, f.getName().length() - 4);
				try {
					ArrayList<String>lines = new ArrayList<String>();
					BufferedReader br = new BufferedReader(new FileReader(f));
					String l;
					while((l = br.readLine()) != null)lines.add(l);
					PlayerData data = new PlayerData(name);
					data.loadOld(lines.toArray(new String[lines.size()]));
					addData(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(oldmagic.exists())
		{
			for(File f:oldmagic.listFiles())
			{
				if(!f.getName().endsWith(".txt"))continue;
				String name = f.getName().substring(0, f.getName().length() - 4);
				PlayerData pdata = getData(name);
				if(pdata == null)
				{
					pdata = new PlayerData(name);
					addData(pdata);
				}
				try {
					BufferedReader br = new BufferedReader(new FileReader(f));
					String s;
					while((s=br.readLine()) != null)
					{
						if(s.startsWith("#"))continue;
						Spell sp = Spell.get(s);
						if(sp != null)pdata.addSpell(sp);
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(oldperks.exists())
		{
			for(File f:oldperks.listFiles())
			{
				try
				{
					if(!f.getName().endsWith(".txt"))continue;
					String name = f.getName().substring(0, f.getName().length() - 4);
					PlayerData pdata = getData(name);
					if(pdata == null)
					{
						pdata = new PlayerData(name);
						addData(pdata);
					}
					BufferedReader br = new BufferedReader(new FileReader(f));
					String l;
					while((l=br.readLine())!= null)
					{
						if(l.startsWith("#"))continue;
						String[] tokens = l.replaceAll(" ", "").split("[,]", 2);
						if(tokens.length != 2)continue;
						Perk p;
						int level;
						try
						{
							p = Perk.valueOf(tokens[0].toUpperCase());
						}
						catch(IllegalArgumentException iae)
						{
							continue;
						}
						try
						{
							level = Integer.parseInt(tokens[1]);
						}
						catch(NumberFormatException nfe)
						{
							continue;
						}
						try{pdata.setPerkLevel(p, level);}catch(IllegalArgumentException iae){}
					}
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		}
		final int nowdata = pdata.size();
		plugin.log.info("Impoted " + (nowdata - curdata) + " players.");
	}
	public void addData(PlayerData data)
	{
		if(getData(data.getPlayer()) != null)return;
		pdata.add(data);
	}
	public void removeData(String player)
	{
		PlayerData d = getData(player);
		if(d != null)pdata.remove(d);
	}
	public PlayerData getData(String player)
	{
		for(PlayerData d:pdata)
		{
			if(d.getPlayer().equals(player))return d;
		}
		return null;
	}
	public void clearData()
	{
		pdata.clear();
	}
}
