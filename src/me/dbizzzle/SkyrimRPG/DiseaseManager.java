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
	}
}
