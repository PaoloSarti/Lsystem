package l_system.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import l_system.Controller;

public class AdvancedOptionsFrame extends JFrame implements ChangeListener, ActionListener 
{

	private static final long serialVersionUID = 1L;
	private IOPanel ioPanel;
	private JSlider slider;
	private JPanel panelProbability;
	private JButton okButton1;
	private JButton okButton2;
	private JButton okButton3;
	private JComboBox<Character> invisibleCharCombo;
	private Controller controller;
	private JTextField seedField;

	public AdvancedOptionsFrame(double probabilityToMiss, IOPanel ioPanel, long seed, Controller controller)throws HeadlessException
	{
		super("Advanced Options");
		
		//si mette al centro del frame
		int width = 300;
		int x=(int) (controller.getFrame().getLocationOnScreen().getX()+(controller.getFrame().getWidth()- width)/2);
		int height = 200;
		int y=(int) (controller.getFrame().getLocationOnScreen().getY()+(controller.getFrame().getHeight()- height)/2);
		this.setBounds( x, y, width, height);
		
		this.ioPanel = ioPanel;
		this.controller=controller;
		
		JTabbedPane tabs = new JTabbedPane();
		
		panelProbability = new JPanel(new GridLayout(3, 1));
		panelProbability.setBackground(Color.white);
		
		JLabel probabilityLabel = new JLabel("Set the probability to miss the rule:");
		panelProbability.add(probabilityLabel);
		
		this.setupSlider(probabilityToMiss);
		
		tabs.add("Probability", panelProbability);

		JPanel invisibleCharPanel = new JPanel(new GridLayout(3, 1));
		invisibleCharPanel.setBackground(Color.white);
		
		JLabel invisibleLabel = new JLabel("Set a character that won't be drawn");
		invisibleCharPanel.add(invisibleLabel);

		char[] possibleInvisibleChars = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		Character[] invisibleChars = new Character[possibleInvisibleChars.length];
		for(int i = 0; i< possibleInvisibleChars.length; i++)
		{
			invisibleChars[i]= possibleInvisibleChars[i];
		}
		this.invisibleCharCombo= new JComboBox<>(invisibleChars);
		invisibleCharCombo.addActionListener(this);
		invisibleCharPanel.add(invisibleCharCombo);
		this.okButton2=new JButton("Ok");
		okButton2.addActionListener(this);
		invisibleCharPanel.add(okButton2);
		
		tabs.addTab("Invisible char", invisibleCharPanel);

		JPanel seedPanel = new JPanel(new GridLayout(3, 1));
		seedPanel.setBackground(Color.white);

		seedPanel.add(new JLabel("Set Seed"));
		seedField = new JTextField(""+seed);
		seedPanel.add(seedField);
		okButton3 = new JButton("Ok");
		okButton3.addActionListener(this);
		seedPanel.add(okButton3);

		tabs.addTab("Set seed", seedPanel);

		this.getContentPane().add(tabs);
	}

	private void setupSlider(double probabilityToMiss)
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
	}

	//It has to update the slider
	public void setProbabilityToMiss(double probabilityToMiss) 
	{
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
		if((arg0.getSource()==okButton1)||(arg0.getSource()==okButton2)||(arg0.getSource()==okButton3))
		{
			this.setVisible(false);
			double probabilityToMiss=slider.getValue()/100.0;
			long seed =  Long.parseLong(seedField.getText());
			ioPanel.setSeed(seed);
			this.controller.startDrawing(ioPanel.getAxiom(), ioPanel.getRules(), ioPanel.getnIterations(), ioPanel.getAngle(), probabilityToMiss, seed);
		}
		else if(arg0.getSource()==this.invisibleCharCombo)
		{
			Character invisibleChar=(Character)this.invisibleCharCombo.getSelectedItem();
			ioPanel.setInvisibleChar(invisibleChar);
		}
	}

	public void setInvisibleChar(char invisibleChar) 
	{
		this.invisibleCharCombo.setSelectedItem(new Character(invisibleChar));
	}

	public void setSeed(long seed) {
		this.seedField.setText(""+seed);
	}
}
