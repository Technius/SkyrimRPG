package me.dbizzzle.SkyrimRPG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class VersionManager 
{
	public String getLatestVersion() throws MalformedURLException
	{
		String version = null;
		URL u;
		try {
			u = new URL("https://raw.github.com/Technius/SkyrimRPG/master/src/plugin.yml");
			URLConnection uc = u.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String l;
			boolean gotver = false;
			while((l=br.readLine()) != null && !gotver)
			{
				String[] tokens = l.split("[:]");
				if(tokens.length != 2)continue;
				if(!tokens[0].equalsIgnoreCase("version"))continue;
				version = tokens[1].trim();
				gotver = true;
			}
			br.close();
		} 
		catch (IOException e) 
		{
			return null;
		}
		return version;
	}
	public boolean compareVersion(String newver, String oldver)
	{
		if(newver.equals(oldver))return false;
		return true;
	}
}
