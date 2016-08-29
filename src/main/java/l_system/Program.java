package l_system;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;


public class Program 
{
	
	public static void main(String[] args) 
	{
		Program.setLookAndFeel();
		Controller controller = new Controller();
		controller.start();
	}

	private static void setLookAndFeel()
	{
		//Nimbus...
		try 
		{
			 for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
			 {
				 	if ("Nimbus".equals(info.getName())) 
					{
					     UIManager.setLookAndFeel(info.getClassName());
					     break;
					}
			 }
		}
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) 
		{
			e.printStackTrace();
		}
	}
	
}
