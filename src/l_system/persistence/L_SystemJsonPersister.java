package l_system.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class L_SystemJsonPersister implements L_SystemPersister {

	private final String lastState = "lastState.restore";
	private String directory;
	private Gson gson;
	
	public L_SystemJsonPersister(String directory) {
		super();
		this.directory = directory;
		setGson(new Gson());
	}

	@Override
	public void persist(L_System lsystem) throws IOException {
		FileOutputStream fout;
		fout = new FileOutputStream(lsystem.getName().trim()+".lsy");
		PrintWriter pw = new PrintWriter(fout);   
		pw.write(gson.toJson(lsystem, L_System.class));
		pw.close();
	}

	@Override
	public void persist(WindowRestore wr) throws IOException {
		FileOutputStream fout;
		fout = new FileOutputStream(lastState);
		PrintWriter pw = new PrintWriter(fout);   
		pw.write(gson.toJson(wr, WindowRestore.class));
		pw.close();
	}

	@Override
	public WindowRestore restoreWindow() throws IOException {
		WindowRestore loaded;
		FileInputStream fin;
		fin = new FileInputStream(lastState);
		loaded = gson.fromJson(new InputStreamReader(fin), WindowRestore.class);
		return loaded;
	}

	@Override
	public List<L_System> loadL_Systems() throws IOException {
		List<L_System> l = new ArrayList<L_System>();
		
		File dir = new File(directory);
		String[] fileNames = dir.list();

		for(String fileName : fileNames)
		{
			if(fileName.endsWith(".lsy"))
			{
				l.add(this.loadL_System(fileName));
			}
		}
		return l;
	}

	private L_System loadL_System(String fileName) throws IOException
	{
		L_System loaded;
		FileInputStream fin;
		fin = new FileInputStream(fileName);
		loaded = gson.fromJson(new InputStreamReader(fin), L_System.class);
		return loaded;
		
	}
	
	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

}
