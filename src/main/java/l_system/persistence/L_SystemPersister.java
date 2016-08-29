package l_system.persistence;

import java.io.IOException;
import java.util.List;

public interface L_SystemPersister 
{
	public void persist(L_System lsystem) throws IOException;
	
	public void persist(WindowRestore wr) throws IOException;
	
	public WindowRestore restoreWindow() throws IOException;
	
	public List<L_System> loadL_Systems() throws IOException;
	
}
