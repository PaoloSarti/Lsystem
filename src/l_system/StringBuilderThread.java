package l_system;

import java.util.List;

public class StringBuilderThread extends Thread 
{
	private List<CharReplace> elaboratedAxiomPortion;
	private String rebuiltString;
	private boolean stop;
	
	
	public StringBuilderThread(List<CharReplace> elaboratedAxiomPortion)
	{
		super();
		this.elaboratedAxiomPortion = elaboratedAxiomPortion;
		this.stop=false;
	}

	public void run()
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<elaboratedAxiomPortion.size()&&!stop; i++)
		{
			sb.append(elaboratedAxiomPortion.get(i).getC());
		}
		if(!stop)
			rebuiltString = sb.toString();
		else
			return;
	}
	

	public String getRebuiltString()
	{
		return rebuiltString;
	}
	
	public void requestStop()
	{
		this.stop=true;
	}
}
