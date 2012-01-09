package com.Technius.SkySpout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.inventory.ItemStack;
import org.getspout.spout.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.MaterialData;

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
		ItemStack spellbook = new SpoutItemStack(new Spellbook(p, null, null), 8);
		SpoutShapedRecipe chainRecipe = new SpoutShapedRecipe(spellbook);
		chainRecipe.shape(" B ", "B B", " B ");
		chainRecipe.setIngredient('B', MaterialData.ironIngot);
		SpoutManager.getMaterialManager().registerSpoutRecipe(chainRecipe);
	}
	public File getSpellbookTexture()
	{
		File tex = null;
		try
		{
			InputStream is = SkyRecipieManager.class.getResourceAsStream("textures" + File.separator + "spellbook.png");
			is.read();
		}
		catch(IOException ioe)
		{
			
		}
		return tex;
	}
}
