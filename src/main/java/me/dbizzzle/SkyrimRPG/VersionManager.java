package me.dbizzzle.SkyrimRPG;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class VersionManager 
{
	public String getLatestVersion() throws MalformedURLException
	{
		String version = null;
		try
		{
			URL filerss = new URL("http://dev.bukkit.org/server-mods/skyrimrpg/files.rss");
			InputStream in = filerss.openStream();
			XMLEventReader reader = 
					XMLInputFactory.newInstance().createXMLEventReader(in);
			boolean i = false;
			while(reader.hasNext())
			{
				XMLEvent event = reader.nextEvent();
				if(!event.isStartElement())continue;
				String l = event.asStartElement().getName().getLocalPart();
				if(l.equals("item"))i = true;
				if(!i)continue;
				if(l.equals("title"))version = reader.nextEvent().asCharacters().getData();
				if(version != null)break;
			}
			in.close();
		}
		catch(XMLStreamException xse)
		{
			
		}
		catch(IOException ioe)
		{
			
		}
		return version;
	}
	public boolean compareVersion(String newver, String oldver)
	{
		if(oldver.equals(newver))return false;
		return true;
	}
}
