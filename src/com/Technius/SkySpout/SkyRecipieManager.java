package com.Technius.SkySpout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.Spout;
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
		ItemStack spellbook = new SpoutItemStack(new Spellbook(p, "DestructionSpellbook", null), 8);
		SpoutShapedRecipe spellbookr = new SpoutShapedRecipe(spellbook);
		spellbookr.shape(" B ", "BSB", " B ");
		spellbookr.setIngredient('B', MaterialData.blazePowder);
		spellbookr.setIngredient('S', MaterialData.book);
		SpoutManager.getMaterialManager().registerSpoutRecipe(spellbookr);
	}
	public File getDestructionSpellbookTexture()
	{
		File tex = null;
		try {
			tex = new File(DestructionSpellbook.class.getResource("textures/DestructionSpellbook.png").toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tex;
	}
}
