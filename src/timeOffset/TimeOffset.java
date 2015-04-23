package timeOffset;

//Classe di test delle performance
public class TimeOffset 
{
	private long startTime;
	
	
	public TimeOffset()
	{
		this.startTime=System.currentTimeMillis();
	}
	
	public long getOffsetMillis()
	{
		return System.currentTimeMillis()-startTime;
	}
	
	public double getOffsetSeconds()
	{
		return (System.currentTimeMillis()-startTime)/1000.d;
	}
	
	public void restart()
	{
		this.startTime=System.currentTimeMillis();
	}

}
