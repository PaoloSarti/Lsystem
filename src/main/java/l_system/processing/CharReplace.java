package l_system.processing;

public class CharReplace 
{
	private char c;
	private boolean replaced;
	
	public CharReplace(char c, boolean replaced)
	{
		this.setC(c);
		this.setReplaced(replaced);
	}

	public char getC() 
	{
		return c;
	}

	public void setC(char c) 
	{
		this.c = c;
	}

	public boolean isReplaced() 
	{
		return replaced;
	}

	public void setReplaced(boolean replaced) 
	{
		this.replaced = replaced;
	}
	
}
