package me.dbizzzle.SkyrimRPG.util;

import me.dbizzzle.SkyrimRPG.SkyrimRPG;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

public class MetadataHandler 
{
	public static MetadataValue getMetadata(Metadatable m, SkyrimRPG srpg, String key)
	{
		for(MetadataValue v:m.getMetadata(key))
		{
			if(v.getOwningPlugin() == srpg)return v;
		}
		return null;
	}
}
