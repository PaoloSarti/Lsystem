package l_system.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import l_system.Controller;

public class AdvancedOptionsFrame extends JFrame implements ChangeListener, ActionListener 
{

	private static final long serialVersionUID = 1L;
	private  final char[] possibleInvisibleChars = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
	private char invisibleChar;
	private IOPanel ioPanel;
	private JSlider slider;
	private double probabilityToMiss;
	private JPanel panelProbability;
	private JPanel invisibleCharPanel;
	private JButton okButton1;
	private JButton okButton2;
	private JComboBox<Character> invisibleCharCombo;
	private Controller controller;
	private int width=300;
	private int height=200;
	
	public AdvancedOptionsFrame(double probabilityToMiss, IOPanel ioPanel, Controller controller)throws HeadlessException 
	{
		super("Advanced Options");
		
		//si mette al centro del frame
		int x=(int) (controller.getFrame().getLocationOnScreen().getX()+(controller.getFrame().getWidth()-width)/2);
		int y=(int) (controller.getFrame().getLocationOnScreen().getY()+(controller.getFrame().getHeight()-height)/2);
		this.setBounds( x, y, width, height);
		
		this.ioPanel = ioPanel;
		this.probabilityToMiss=probabilityToMiss;
		this.controller=controller;
		
		JTabbedPane tabs = new JTabbedPane();
		
		panelProbability = new JPanel(new GridLayout(3, 1));
		panelProbability.setBackground(Color.white);
		
		JLabel probabilityLabel = new JLabel("Set the probability to miss the rule:");
		panelProbability.add(probabilityLabel);
		
		this.setupSlider();
		
		tabs.add("Probability", panelProbability);
		
		invisibleCharPanel = new JPanel(new GridLayout(3,1));
		invisibleCharPanel.setBackground(Color.white);
		
		JLabel invisibleLabel = new JLabel("Set a character that won't be drawn");
		invisibleCharPanel.add(invisibleLabel);
		
		Character[] invisibleChars = new Character[this.possibleInvisibleChars.length];
		for(int i=0; i<this.possibleInvisibleChars.length; i++)
		{
			invisibleChars[i]=this.possibleInvisibleChars[i];
		}
		this.invisibleCharCombo= new JComboBox<Character>(invisibleChars);
		invisibleCharCombo.addActionListener(this);
		invisibleCharPanel.add(invisibleCharCombo);
		this.okButton2=new JButton("Ok");
		okButton2.addActionListener(this);
		invisibleCharPanel.add(okButton2);
		
		tabs.addTab("Invisible char", invisibleCharPanel);
		
		
		this.getContentPane().add(tabs);
	}

	private void setupSlider()
	{
		slider= new JSlider(JSlider.HORIZONTAL, 0, 100,(int) (probabilityToMiss*100));
		slider.addChangeListener(this);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(25);
		slider.setBackground(Color.white);
		panelProbability.add(slider);
		okButton1 = new JButton("Ok");
		panelProbability.add(okButton1);
		okButton1.addActionListener(this);
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) 
	{
		ioPanel.setProbabilityToMiss(slider.getValue()/100.0);
		this.probabilityToMiss=slider.getValue();
	}


	public double getProbabilityToMiss()
	{
		return probabilityToMiss;
	}

	//It has to update the slider
	public void setProbabilityToMiss(double probabilityToMiss) 
	{
		this.probabilityToMiss = probabilityToMiss;
		JSlider sliderUpdate= new JSlider(JSlider.HORIZONTAL, 0, 100,(int) (probabilityToMiss*100));
		sliderUpdate.addChangeListener(this);
		sliderUpdate.setPaintLabels(true);
		sliderUpdate.setMajorTickSpacing(25);
		panelProbability.remove(slider);
		panelProbability.remove(okButton1);
		slider=sliderUpdate;
		slider.setBackground(Color.WHITE);
		panelProbability.add(slider);
		panelProbability.add(okButton1);
		panelProbability.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if((arg0.getSource()==okButton1)||(arg0.getSource()==okButton2))
		{
			this.setVisible(false);
			probabilityToMiss=slider.getValue()/100.0;
			this.controller.startDrawing(ioPanel.getAxiom(), ioPanel.getRules(), ioPanel.getnIterations(), ioPanel.getAngle(), probabilityToMiss);
		}
		else if(arg0.getSource()==this.invisibleCharCombo)
		{
			invisibleChar=(Character)this.invisibleCharCombo.getSelectedItem();
			ioPanel.setInvisibleChar(invisibleChar);
		}
	}

	public char getInvisibleChar() 
	{
		return invisibleChar;
	}

	public void setInvisibleChar(char invisibleChar) 
	{
		this.invisibleChar = invisibleChar;
		this.invisibleCharCombo.setSelectedItem(new Character(invisibleChar));
	}
	
}
