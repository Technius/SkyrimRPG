package com.Technius.SkySpout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.inventory.ItemStack;
import org.getspout.spout.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.MaterialData;

import com.Technius.SkySpout.Items.DestructionSpellbook;
import com.Technius.SkySpout.Items.Spellbook;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;

public class SkyRecipieManager 
{
	private SkyrimRPG p;
	public SkyRecipieManager(Spout s, SkyrimRPG p)
	{
		this.p = p;
	}
	public void setupRecipies()
	{
		File tex = getDestructionSpellbookTexture();
		ItemStack spellbook = new SpoutItemStack(new Spellbook(p, "Destruction Spellbook", p.getDataFolder().getPath() + File.separator + "DestructionSpellbook.png"), 1);
		SpoutShapedRecipe spellbookr = new SpoutShapedRecipe(spellbook);
		spellbookr.shape(" B ", "BSB", " B ");
		spellbookr.setIngredient('B', MaterialData.blazePowder);
		spellbookr.setIngredient('S', MaterialData.book);
		SpoutManager.getMaterialManager().registerSpoutRecipe(spellbookr);
		SpoutManager.getFileManager().addToPreLoginCache(p, tex);
	}
	public File getDestructionSpellbookTexture()
	{
		File tex = null;
		try
		{
			InputStream is = DestructionSpellbook.class.getResourceAsStream("textures/DestructionSpellbook.png");
			if(!new File(p.getDataFolder().getPath() + File.separator + "DestructionSpellbook.png").exists())
			{
				FileOutputStream fos = new FileOutputStream(p.getDataFolder().getPath() + File.separator + "DestructionSpellbook.png");
				byte[] data = new byte[128];
				int x=0;
				while((x=is.read(data,0,128))>=0)
				{
					fos.write(data,0,x);
				}
				is.close();
				fos.flush();
				fos.close();
			}
			tex = new File(p.getDataFolder().getPath() + File.separator + "DestructionSpellbook.png");
		}
		catch(IOException ioe)
		{
			p.log.severe("[SkyrimRPG]COULD NOT LOAD SPOUT TEXTURES!");
		}
		return tex;
	}
	public enum SkyItems
	{
		Destruction_Spellbook, Fireball_Spellbook;
	}
}
