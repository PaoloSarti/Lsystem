package l_system.gui;

import java.awt.Color;
import java.io.Serializable;


//sono punti che se il pannello fosse quadrato (per mantenere le proporzioni verticale-orizzontale),
//sarebbero visibili solo se con valori da 0 a 1
//vedi il metodo paintComponent di TurtlePanel per i dettagli della resa a schermo
public class ProportionPoint implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private boolean visible;
	private Color color;
	
	public ProportionPoint(double x, double y, boolean visible, Color color)
	{
		this.x=x;
		this.y=y;
		this.visible=visible;
		this.color=color;
	}

	public ProportionPoint(double x, double y, boolean visible)
	{
		this(x, y, visible, Color.black);
	}
	
	public double getX()
	{
		return this.x;
	}
	
	public double getY()
	{
		return this.y;
	}

	public boolean isVisible() 
	{
		return visible;
	}

	public Color getColor() 
	{
		return color;
	}

	//a new one invisible
	public ProportionPoint getInvisible()
	{
		return new ProportionPoint(this.x, this.y, false);
	}
	
	public double distanceToOrigin()
	{
		return Math.sqrt(x*x+y*y);
	}
	
	@Override
	public String toString()
	{
		return "("+this.getX()+','+ this.getY()+')';
	}
	
	public double distanceTo(ProportionPoint a)
	{
		return Math.sqrt((this.getX()-a.getX())*(this.getX()-a.getX())+(this.getY()-a.getY())*(this.getY()-a.getY()));
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}
}