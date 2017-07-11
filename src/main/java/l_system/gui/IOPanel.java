package l_system.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import timeOffset.TimeOffset;
import l_system.Controller;
import l_system.persistence.L_System;

public class IOPanel extends JPanel implements ActionListener 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int nColumnsTextFields=10;
	private JPanel upperPanel;
	private JTextField axiomText;
	private JTextArea rulesArea;
	private JTextField nText;
	private JButton drawButton;
	private JTextField angleText;
	private String axiom;
	private List<String> rules;
	private int nIterations;
	private double angle;
	private Controller controller;
	private JButton saveButton;
	private TurtlePanel turtle;
	private JButton openButton;
	private JButton advancedButton;
	private AdvancedOptionsFrame advancedOptionsFrame;
	private double probabilityToMiss;
	private long seed = 0L;
	private JButton saveImageButton;
	private JProgressBar progressBar;
	private JFileChooser fileChooser;
	private char invisibleChar;
	private JLabel millisecondsLabel;
	
	public IOPanel(Controller controller, TurtlePanel turtle)
	{
		super(new BorderLayout());
		
		this.controller=controller;
		this.turtle = turtle;
		this.probabilityToMiss=0;
		this.fileChooser=new JFileChooser();
		this.invisibleChar=' '; //di default tengo questo, che in real un carattere ignorato dal drawer
		
		upperPanel= new JPanel(new GridLayout(12, 1));
		this.add(upperPanel, BorderLayout.NORTH);
		
		JLabel axiomLabel = new JLabel(" Axiom:");
		upperPanel.add(axiomLabel);
		
		axiomText = new JTextField(nColumnsTextFields);
		upperPanel.add(axiomText);
		
		JLabel rulesLabel = new JLabel(" Rules:");
		upperPanel.add(rulesLabel);
		
		rulesArea= new JTextArea(2, nColumnsTextFields);
		JScrollPane scrollArea= new JScrollPane(rulesArea);
		upperPanel.add(scrollArea);
		
		JLabel nIterationsLabel = new JLabel(" n Iterations: ");
		upperPanel.add(nIterationsLabel);
		
		nText = new JTextField(nColumnsTextFields);
		upperPanel.add(nText);
		
		JLabel angleLabel= new JLabel(" Angle:");
		upperPanel.add(angleLabel);
		
		this.angleText=new JTextField(nColumnsTextFields);
		upperPanel.add(angleText);
		
		advancedButton = new JButton("Advanced Options");
		upperPanel.add(advancedButton);
		
		drawButton = new JButton("Draw!");
		upperPanel.add(drawButton);
		
		JPanel openSavePanel = new JPanel(new GridLayout(3,1));
		this.add(openSavePanel, BorderLayout.SOUTH);
		
		openButton = new JButton("Open");
		openSavePanel.add(openButton);
		saveButton = new JButton("Save");
		openSavePanel.add(saveButton);
		saveImageButton = new JButton("Save Image");
		openSavePanel.add(saveImageButton);
		
		this.millisecondsLabel= new JLabel("Elapsed millis: ");
		upperPanel.add(millisecondsLabel);
		
		this.progressBar=new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		upperPanel.add(progressBar);
		
		this.axiomText.addActionListener(this);
		this.angleText.addActionListener(this);
		this.nText.addActionListener(this);
		advancedButton.addActionListener(this);
		saveButton.addActionListener(this);
		drawButton.addActionListener(this);
		openButton.addActionListener(this);
		saveImageButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource()==this.drawButton||e.getSource()==this.axiomText||e.getSource()==this.angleText||e.getSource()==this.nText)
		{
			axiom=axiomText.getText();
			rules=this.readRules();
			try
			{
				this.nIterations=Integer.parseInt(this.nText.getText());
				angle=Math.PI*Double.parseDouble(this.angleText.getText())/180;
			}
			catch(NumberFormatException ex)
			{
				
			}
			Random r = new Random();
			this.seed = r.nextLong();
			controller.startDrawing(axiom, rules, nIterations, angle, probabilityToMiss, seed);
		}
		else if(e.getSource()==this.saveButton)
		{
			axiom=axiomText.getText();
			rules=this.readRules();
			this.nIterations=Integer.parseInt(this.nText.getText());
			angle=Math.PI*Double.parseDouble(this.angleText.getText())/180;
			JFrame j= new JFrame("Save");
			String name = JOptionPane.showInputDialog(j, "Insert name");
			if(name!=null)
			{
				L_System toSave = new L_System(axiom, rules, nIterations, angle, turtle.getStartingPoint(), turtle.getZoom(), name, probabilityToMiss, invisibleChar, seed);
				controller.saveL_System(toSave);
			}
		}
		else if(e.getSource()==this.openButton)
		{
			controller.openSaves();
		}
		else if(e.getSource()==this.advancedButton)
		{
			//apri il frame avanzato
			if(advancedOptionsFrame==null)
			{
				advancedOptionsFrame = new AdvancedOptionsFrame(probabilityToMiss, this, seed, controller);
			}
			advancedOptionsFrame.setProbabilityToMiss(probabilityToMiss);
			advancedOptionsFrame.setInvisibleChar(invisibleChar);
			advancedOptionsFrame.setSeed(seed);
			advancedOptionsFrame.setVisible(true);
		}
		else if(e.getSource()==this.saveImageButton)
		{
			int returnVal = fileChooser.showSaveDialog(turtle);
			if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
				//tengo come standard il png
	            File file = fileChooser.getSelectedFile();

	            BufferedImage image = turtle.getImage();
	            controller.saveImagePng(image, file);

			}
		}
	}

	private List<String> readRules()
	{
		if(this.rulesArea.getText().trim().equals("")||this.rulesArea.getText()==null)
		{
			return null;
		}
		
		List<String> rules = new LinkedList<String>();
		StringTokenizer st = new StringTokenizer(rulesArea.getText(), "\n");
		String rule;
		
		while(st.hasMoreTokens())
		{
			rule=st.nextToken();
			rules.add(rule);
		}
		
		return rules;
	}

	
	public String getAxiom() 
	{
		if(this.axiomText.getText().trim().equals("")||this.axiomText.getText()==null)
			return null;
		
		axiom=this.axiomText.getText();
		return axiom;
	}

	public List<String> getRules() 
	{
		return readRules();
	}

	//Used by the controller
	public int getnIterations()
	{
		try
		{
			nIterations=Integer.parseInt(this.nText.getText());
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
		return nIterations;
	}

	//Used by the controller
	public double getAngle()
	{
		try
		{
			angle=Double.parseDouble(this.angleText.getText())/180*Math.PI;
		}
		catch(NumberFormatException e)
		{
			return 0;
		}
		return angle;
	}

	public void setMilliseconds(long milliseconds)
	{
		this.millisecondsLabel.setText("Elapsed millis: "+milliseconds);
	}
	
	public void setAxiom(String axiom) 
	{
		this.axiom = axiom;
		this.axiomText.setText(axiom);
	}

	public void setRules(List<String> rules)
	{
		if(rules!=null)
		{
			this.rules = rules;
			StringBuilder sb = new StringBuilder();
			for(String rule : rules)
			{
				sb.append(rule);
				sb.append('\n');
			}
			this.rulesArea.setText(sb.toString().trim());
		}
	}

	public void setnIterations(int nIterations) 
	{
		this.nIterations = nIterations;
		this.nText.setText(""+nIterations);
	}

	public void setAngle(double angle)
	{
		this.angle = angle;
		this.angleText.setText(""+(angle/Math.PI*180));
	}

	public double getProbabilityToMiss() {
		return probabilityToMiss;
	}

	public void setProbabilityToMiss(double probabilityToMiss) 
	{
		this.probabilityToMiss = probabilityToMiss;
	}
	
	public void showProgressBar()
	{
		progressBar.setVisible(true);
		upperPanel.revalidate();
	}
	
	public void HideProgressBar()
	{
		progressBar.setVisible(false);
		upperPanel.revalidate();
	}

	public char getInvisibleChar() 
	{
		return invisibleChar;
	}

	public void setInvisibleChar(char invisibleChar) 
	{
		this.invisibleChar = invisibleChar;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}
}
