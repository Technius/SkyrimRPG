package me.dbizzzle.SkyrimRPG;

public class DiseaseManager 
{
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
}
