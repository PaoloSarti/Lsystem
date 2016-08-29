package l_system.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class L_SystemFileJsonPersister implements L_SystemPersister {

	private final String lastState = "lastState.restore";
	private final String samples="samples";
	private String sep;
	private String directory;
	private String subDir;
	private Gson gson;
	
	public L_SystemFileJsonPersister(String directory) {
		super();
		this.directory = directory;
		this.sep=System.getProperty("file.separator");
		this.subDir=directory+sep+samples;
		setGson(new Gson());
	}

	@Override
	public void persist(L_System lsystem) throws IOException {
		FileOutputStream fout;
		fout = new FileOutputStream(subDir+sep+lsystem.getName().trim()+".lsy");
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
		
		try
		{
			loaded = gson.fromJson(new InputStreamReader(fin), WindowRestore.class);
		}
		catch(Exception e)
		{
			fin.close();
			
			//try the old Object Way
			fin=new FileInputStream(lastState);
			ObjectInputStream ois = new ObjectInputStream(fin);
			try {
				loaded= (WindowRestore) ois.readObject();
			} catch (ClassNotFoundException e1) {
				throw new IOException("Class not found");
			}
			finally
			{
				ois.close();
			}
		}
		return loaded;
	}

	@Override
	public List<L_System> loadL_Systems() throws IOException {
		List<L_System> l = new ArrayList<L_System>();
		
		File dir = new File(directory);
		String[] fileNames = dir.list();

		for(String fileName : fileNames)
		{
			if(fileName.equals(samples))
			{
				File samplesDir=new File(subDir);
				
				String[] otherFileNames=samplesDir.list();
				
				if(otherFileNames!=null)
				{
					for(String otherFile : otherFileNames)
					{
						if(otherFile.endsWith(".lsy"))
						{
							l.add(this.loadL_System(subDir+sep+otherFile));
						}
					}
				}
			}
			
			if(fileName.endsWith(".lsy"))
			{
				l.add(this.loadL_System(fileName));
			}
		}
		
		return l;
	}

	private L_System loadL_System(String fileName) throws IOException
	{
		L_System loaded=null;
		FileInputStream fin;
		fin = new FileInputStream(fileName);
		try
		{
			loaded = gson.fromJson(new InputStreamReader(fin), L_System.class);
		}
		catch(Exception e)	//if it is not JSON
		{
			fin.close();
			
			//try the old Object Way
			fin=new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fin);
			try {
				loaded= (L_System) ois.readObject();
			} catch (ClassNotFoundException e1) {
				throw new IOException("Class not found");
			}
			finally
			{
				ois.close();
			}
		}
		
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
