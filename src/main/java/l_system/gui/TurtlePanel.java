package l_system.gui;


import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JPanel;

import l_system.Turtle;


public class TurtlePanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener, Turtle
{
	private static final long serialVersionUID = 1L;

	private double turtleAngle; //radiants
	private ArrayList<ProportionPoint> proportionPoints;
	private LinkedList<ProportionPoint> stackPoints; //for [ and ]
	private LinkedList<Double> stackAngles;//for [ and ]
	private ProportionPoint startingPoint;
	private double zoom;
	private static final double baseZoom=1.2;
	private int mousePressedX;
	private int mousePressedY;
	private boolean repainting;
	
	public TurtlePanel() 
	{
		super();
		this.turtleAngle=Math.PI;
		this.proportionPoints=new ArrayList<>();
		this.stackPoints= new  LinkedList<>();
		this.stackAngles = new LinkedList<>();
		this.startingPoint=new ProportionPoint(0.5, 0.5, true);
		this.zoom=1;
		this.repainting=false;
		this.addMouseWheelListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		this.setBackground(Color.white);
		 //tengo la lunghezza minima come riferimento, per non deformare
		int minLength = this.getWidth()>this.getHeight()?this.getHeight():this.getWidth();
		int x0;
		int y0;
		int x1;
		int y1;
		
		if(proportionPoints.size()>1)
		{
			for(int i=0; i<proportionPoints.size()-1; i++)
			{
				if(proportionPoints.get(i+1).isVisible()) 
				{
					g.setColor(proportionPoints.get(i+1).getColor()); //non ancora usato di fatto
					
					x0=this.proportionToPixels(startingPoint.getX()+zoom*proportionPoints.get(i).getX(), minLength);
					y0=this.proportionToPixels(startingPoint.getY()+zoom*proportionPoints.get(i).getY(), minLength);
					x1=this.proportionToPixels(startingPoint.getX()+zoom*proportionPoints.get(i+1).getX(), minLength);
					y1=this.proportionToPixels(startingPoint.getY()+zoom*proportionPoints.get(i+1).getY(), minLength);
					//se almeno uno visibile, e non sono lo stesso punto
					if((this.isVisiblePoint(x0, y0)||this.isVisiblePoint(x1, y1))&&!(x0==x1&&y0==y1))
						g.drawLine( x0,y0,x1,y1);
				}
			}
			//System.out.println("Numero di punti: "+proportionPoints.size());  //DEBUG a schifo in mezzo al codice
		}
	}
	
	private boolean isVisiblePoint(int x, int y)
	{
		return x>=0&&y>=0&&x<=this.getWidth()&&y<=this.getHeight();
	}
	
	private int proportionToPixels(double proportion, int length)
	{
		return this.roundDoubleToInt(proportion*length);
	}
	
	private int roundDoubleToInt(double d)
	{
		if((d-(int) d)<0.5)
		{
			return (int) d;
		}
		else
		{
			return ((int) d) +1;
		}
	}
	
	
	public void moveTurtleForward(double distance, boolean visible, Color color)
	{
		if(proportionPoints.size()==0)
			proportionPoints.add(new ProportionPoint(0, 0, true));
		double turtleDestX=this.proportionPoints.get(proportionPoints.size()-1).getX()+distance*Math.sin(turtleAngle);
		double turtleDestY=this.proportionPoints.get(proportionPoints.size()-1).getY()+distance*Math.cos(turtleAngle);
		this.proportionPoints.add(new ProportionPoint(turtleDestX, turtleDestY, visible, color));
	}
	
	public void moveTurtleForward(double distance, boolean visible)
	{
		if(proportionPoints.size()==0)
			proportionPoints.add(new ProportionPoint(0, 0, true));
		double turtleDestX=this.proportionPoints.get(proportionPoints.size()-1).getX()+distance*Math.sin(turtleAngle);
		double turtleDestY=this.proportionPoints.get(proportionPoints.size()-1).getY()+distance*Math.cos(turtleAngle);
		this.proportionPoints.add(new ProportionPoint(turtleDestX, turtleDestY, visible, Color.black));
	}
	
	public void rotateTurtle(double angle)
	{
		this.turtleAngle+=angle;
	}
	
	
	//for [
	public void addPositionToStack()
	{
		if(proportionPoints.size()==0)
			proportionPoints.add(new ProportionPoint(0, 0, true));
		this.stackAngles.push(this.turtleAngle);
		this.stackPoints.push(this.proportionPoints.get(proportionPoints.size()-1).getInvisible());
	}
	
	//for ]
	public void restorePositionFromStack()
	{
		this.proportionPoints.add(stackPoints.pop());
		this.turtleAngle=this.stackAngles.pop();
	}
	
	public void clear()
	{
		//Throw away the old references hoping that the Garbage collector will free the rest
		this.proportionPoints = new ArrayList<>();//.clear();
		this.stackAngles = new LinkedList<>(); //.clear();
		this.stackPoints = new LinkedList<>(); //clear();
		this.turtleAngle=Math.PI;
		this.repaint();
	}
	
	private double pixelsToProportion(int pixels, int length)
	{
		return pixels/(double) length;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		if(!repainting)
		{
			//zoom che segue il mouse!!!
			//uso un incremento/decremento logaritmico, in modo che facendone l'esponenziale,lo zoom sia sempre positivo
			double logZoom=Math.log(zoom)/Math.log(baseZoom); //cambio di base, equivale al logaritmo in base baseZoom
			logZoom-=e.getWheelRotation();
			double newZoom=Math.pow(baseZoom, logZoom);
			double proportionMouseX=pixelsToProportion(e.getX(), this.getWidth());
			double proportionMouseY=pixelsToProportion(e.getY(), this.getHeight());
			double startXNew=proportionMouseX-(newZoom/zoom)*(proportionMouseX-startingPoint.getX());
			double startYNew=proportionMouseY-(newZoom/zoom)*(proportionMouseY-startingPoint.getY());
			startingPoint.setX(startXNew);
			startingPoint.setY(startYNew);
			zoom=newZoom;
			this.repainting=true;
			this.repaint();
			this.repainting=false;
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		if(this.proportionPoints.size()>1)
		{	
			this.mousePressedX=arg0.getX();
			this.mousePressedY=arg0.getY();
			this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) 
	{
		if(!repainting)
		{
			if(this.proportionPoints.size()>1)
			{	
				double newStartingX = this.startingPoint.getX()+this.pixelsToProportion(arg0.getX()-mousePressedX, this.getWidth());
				double newStartingY =  this.startingPoint.getY()+this.pixelsToProportion(arg0.getY()-mousePressedY, this.getHeight());
				startingPoint.setX(newStartingX);
				startingPoint.setY(newStartingY);
				mousePressedX=arg0.getX();
				mousePressedY=arg0.getY();
				this.repainting=true;
				this.repaint();
				this.repainting=false;
			}
		}
		
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	//lowest rispetto a y, in realt in alto
	public double proportionLowest()
	{
		double lowest;
		
		lowest=proportionPoints.get(0).getY();
		for(ProportionPoint p : proportionPoints)
		{
			lowest=lowest<p.getY()?lowest:p.getY();
		}
		return lowest;
	}
	//highest rispetto a y, in realt in basso
	public double proportionHighest()
	{
		double highest;
		
		highest=proportionPoints.get(0).getY();
		for(ProportionPoint p : proportionPoints)
		{
			highest=highest>p.getY()?highest:p.getY();
		}
		return highest;
	}
	
	public double proportionWestest()
	{
		double westest;
		
		westest=proportionPoints.get(0).getX();
		for(ProportionPoint p : proportionPoints)
		{
			westest=westest<p.getX()?westest:p.getX();
		}
		return westest;
	}
	
	public double proportionEastest()
	{
		double eastest;
		
		eastest=proportionPoints.get(0).getX();
		for(ProportionPoint p : proportionPoints)
		{
			eastest=eastest>p.getX()?eastest:p.getX();
		}
		return eastest;
	}
	
	public void centerDrawing()
	{
		if(this.proportionPoints.size()>1)
		{
			double maxProportionY = this.proportionHighest();
			double minProportionY = this.proportionLowest();
			double minProportionX = this.proportionWestest();
			double maxProportionX = this.proportionEastest();
			
			double zoomX = 1/(maxProportionX-minProportionX);
			double zoomY = 1/(maxProportionY-minProportionY);
			double zoom = (zoomX>zoomY?zoomY:zoomX);
			double startingPointX=(1-zoom*(maxProportionX+minProportionX))/2;
			double startingPointY=(1-zoom*(maxProportionY+minProportionY))/2;
			startingPoint.setX(startingPointX);
			startingPoint.setY(startingPointY);
			this.setZoom(zoom);
			this.repaint();
		}
		else
		{
			//this.setStartingPoint(new ProportionPoint(0.5, 0.5, false));
			this.repaint();

		}
	}
	
	
	public ProportionPoint getStartingPoint() 
	{
		return startingPoint;
	}

	public double getZoom() 
	{
		return zoom;
	}

	public void setStartingPoint(ProportionPoint startingPoint) 
	{
		this.startingPoint = startingPoint;
	}

	public void setZoom(double zoom) 
	{
		this.zoom = zoom;
	}

	//copyed from Stack OverFlow :)
	public BufferedImage getImage()
	{
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics(); 

        this.paint(graphics2D);
        return image;
	}
	
	//I don't need these ones
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



}
