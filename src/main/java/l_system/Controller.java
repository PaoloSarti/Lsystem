package l_system;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;


import l_system.processing.LSystemProcessing;
import l_system.processing.StreamLsystem;
import l_system.processing.StreamLsystemold;
import timeOffset.TimeOffset;
//import timeOffset.TimeOffset;
import l_system.gui.IOPanel;
import l_system.gui.TurtlePanel;
import l_system.persistence.L_System;
import l_system.persistence.L_SystemFileJsonPersister;
import l_system.persistence.L_SystemPersister;
import l_system.persistence.WindowRestore;
import l_system.processing.StringProcessingListener;

public class Controller implements WindowListener, StringProcessingListener
{
	private L_SystemDrawer drawer;
	private JFrame frame;
	private TurtlePanel turtle;
	private IOPanel ioPanel;
	private List<L_System> l_systems;
	//private StringProcessingThread process;
	private LSystemProcessing process;
	private boolean restoring;
	private TimeOffset to;
	private long millisecondsLastCalculations=0;
	private String saveFolder=".";
	private L_SystemPersister persister;

	public Controller() 
	{
		this.frame=new JFrame("L-System");
		this.turtle=new TurtlePanel();
		this.drawer=new L_SystemDrawer(turtle);
		this.l_systems= new LinkedList<>();
		this.ioPanel=new IOPanel(this, turtle);
		this.persister= new L_SystemFileJsonPersister(saveFolder);
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.restoring=true;
		this.process = new LSystemProcessing(new StreamLsystem());
	}
	
	public void start()
	{
		WindowRestore restore=null;
		try
		{
			this.l_systems=persister.loadL_Systems();
			restore=persister.restoreWindow();
		}
		catch (IOException e1) 
		{
			this.l_systems= new ArrayList<>();
			e1.printStackTrace();
		}
		finally
		{
			if(restore!=null)
			{
				frame.setBounds(restore.getX(), restore.getY(), restore.getWidth(), restore.getHeight());

				this.restoreL_System(restore.getRestore());
			}
			else
			{
				frame.setBounds(400, 150, 900, 800);
				this.restoring=false;
			}
			frame.getContentPane().add(turtle, BorderLayout.CENTER);
			JScrollPane scrollPane = new JScrollPane(ioPanel);
			frame.getContentPane().add(scrollPane, BorderLayout.EAST);
			frame.setVisible(true);
		}

	}

	public void startDrawing(String axiom, List<String> rules, int nIterations, double angle, double probabilityToMiss, long seed)
	{
		if(!this.validArguments(axiom, rules, nIterations, angle, probabilityToMiss))
		{
			JOptionPane.showMessageDialog(null, "Check the arguments!");
			return;
		}
		if(process !=null)
			process.stop();
		ioPanel.showProgressBar();
		process.startProcessing(axiom, rules, nIterations, probabilityToMiss, seed, this);
		//process = new StringProcessingThread(axiom, rules, nIterations,probabilityToMiss,this);
		to=new TimeOffset();
		//process.start();
	}
	
	private void startDrawing(L_System l_system)
	{
		if(!l_system.isValid())
		{
			return;
		}
		if(process !=null)
			process.stop();
		ioPanel.showProgressBar();
		process.startProcessing(l_system.getAxiom(), l_system.getRules(), l_system.getnIterations(), l_system.getProbabilityToMiss(), l_system.getSeed(), this);
		to=new TimeOffset();
	}
	
	public void finishedStringProcessing(String command)
	{
		this.millisecondsLastCalculations=to.getOffsetMillis();
		//System.out.println("Milliseconds to process String: "+this.millisecondsLastCalculations);
		//System.out.println("Computed string: "+command);
		this.ioPanel.setMilliseconds(millisecondsLastCalculations);
		drawer.draw(command, 0.05, ioPanel.getAngle(), turtle.getStartingPoint(),
				turtle.getZoom(),ioPanel.getInvisibleChar(), !restoring||ioPanel.getProbabilityToMiss()>0);
		restoring=false;
		ioPanel.HideProgressBar();
		System.gc();
	}

	public void saveL_System(L_System l_system)
	{
		try 
		{
			boolean overwrite=false;
			boolean sameName=false;
			for(int i=0; i<l_systems.size()&&!sameName; i++)
			{
				if(l_systems.get(i).getName().equals(l_system.getName()))
				{
					sameName=true;
					int n = JOptionPane.showConfirmDialog(
						    frame,
						    "There's another L-System with that name, overwrite?",
						    "Warning",
						    JOptionPane.YES_NO_OPTION);
					if(overwrite=(n==JOptionPane.YES_OPTION))
					{
						l_systems.remove(i);
						l_systems.add(l_system);
					}
				}
			}

			
			if(!sameName)
			{
				this.l_systems.add(l_system);
			}
			
			if(overwrite||!sameName)
			{
				persister.persist(l_system);
			}
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "The file couldn't be saved");
			e.printStackTrace();
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) 
	{
		this.stopStringProcess();
		frame.setExtendedState(JFrame.NORMAL);
		L_System lastState = new L_System(ioPanel.getAxiom(), ioPanel.getRules(), ioPanel.getnIterations(), ioPanel.getAngle(),
				turtle.getStartingPoint(), turtle.getZoom(), "lastState", ioPanel.getProbabilityToMiss(), ioPanel.getInvisibleChar(), ioPanel.getSeed());
		
		int x=(int) frame.getLocationOnScreen().getX();
		int y=(int) frame.getLocationOnScreen().getY();
		int width=frame.getWidth();
		int height=frame.getHeight();
		WindowRestore windowRestore= new WindowRestore(x, y, width, height, lastState);
		
		try 
		{
			persister.persist(windowRestore);
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "The file couldn't be saved");
			e.printStackTrace();
		}
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	

	public void restoreL_System(L_System restore)
	{
		ioPanel.setAxiom(restore.getAxiom());
		ioPanel.setRules(restore.getRules());
		ioPanel.setnIterations(restore.getnIterations());
		ioPanel.setAngle(restore.getAngle());
		ioPanel.setProbabilityToMiss(restore.getProbabilityToMiss());
		ioPanel.setInvisibleChar(restore.getInvisibleChar());
		ioPanel.setSeed(restore.getSeed());
		turtle.setZoom(restore.getZoom());
		turtle.setStartingPoint(restore.getStartingPoint());
		this.restoring=true;
		if(restore.isValid())
			this.startDrawing(restore);
	}
	
	private boolean validArguments(String axiom, List<String> rules, int nIterations, double angle, double probabilityToMiss)
	{
		return axiom!=null
				&&!axiom.equals("")
				&&rules!=null
				&&this.validRules(rules)
				&&nIterations>=0
				&&probabilityToMiss>=0
				&&probabilityToMiss<=1;
	}
	
	private boolean validRules(List<String> rules)
	{
		boolean valid = true;
		for(int i=0; i<rules.size()&&valid; i++)
		{
			if(rules.get(i).split("=").length!=2)
			{
				valid=false;
			}
		}
		
		return valid;
	}
	
	public void openSaves()
	{
		if(this.l_systems.size()==0)
		{
			JOptionPane.showMessageDialog(frame, "No saves lo load");
			return;
		}
		String[] l_systemsNames= new String[this.l_systems.size()];
		for(int i=0; i<this.l_systems.size(); i++)
		{
			l_systemsNames[i]=l_systems.get(i).getName();
		}
		JFrame j= new JFrame("Open");
	    String name = (String) JOptionPane.showInputDialog(j, 
	        "Choose the L-System",
	        "Open",
	        JOptionPane.PLAIN_MESSAGE, 
	        null, 
	        l_systemsNames, 
	        l_systemsNames[0]);
		//System.out.println(name);
	    if(name!=null)
	    {
	    	for(L_System l_system : l_systems)
	    	{
	    		if(name.equals(l_system.getName()))
	    		{
	    			this.restoreL_System(l_system);
	    			return;
	    		}
	    	}
	    }
	}
	
	public void saveImagePng(BufferedImage image, File file)
	{
		 if(!file.getAbsolutePath().toLowerCase().endsWith(".png"))
         {
         	file=new File(file.getAbsolutePath()+".png");
         }
         try 
			{
				ImageIO.write(image, "PNG", file);
			} 
			catch (IOException ex) 
			{
				JOptionPane.showMessageDialog(turtle, "Couldn't save the image");
				return;
			}
         JOptionPane.showMessageDialog(turtle, "Image saved at "+file.getAbsolutePath());
	}
	
	public void stopStringProcess()
	{
		if(this.process !=null/*&&this.process.isAlive()*/)
			this.process.stop();//.interrupt();
	}
	
	//All these WindowListener methods are not useful for the program's purposes
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public JFrame getFrame() 
	{
		return this.frame;
	}

}
