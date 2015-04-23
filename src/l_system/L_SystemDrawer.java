package l_system;


import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import l_system.gui.ProportionPoint;
import l_system.gui.TurtlePanel;


public class L_SystemDrawer 
{
	//private static final String forwardChars="qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
	private TurtlePanel turtle;
	private Map<Character, Color> charColor;//TODO
	
	public L_SystemDrawer(TurtlePanel turtle)
	{
		this.turtle=turtle;
		charColor=new HashMap<Character, Color>();
	}
	
	//A partire dal comando, chiedo al turtlePanel di disegnare
	public void draw(String command, double distance, double angle, ProportionPoint startingPoint, double zoom, char invisibleChar, boolean autoCenter)
	{
		if(command==null)
			return;
		int forward=0;
		int forwardInvisible=0;
		turtle.clear();
		if(startingPoint!=null)
		{
			turtle.setStartingPoint(startingPoint);
		}
		if(zoom>0)
		{
			turtle.setZoom(zoom);
		}
		for(int i=0; i<command.length(); i++)
		{
			if(command.charAt(i)==invisibleChar)
			{
				if(forward>0)
				{
					turtle.moveTurtleForward(distance*forward, true);
					forward=0;
				}
				forwardInvisible++;
				//turtle.moveTurtleForward(distance, false);
			}
			else if(charColor.containsKey(command.charAt(i)))  //TODO
			{
				turtle.moveTurtleForward(distance, true, charColor.get(command.charAt(i)));
			}
			else switch(command.charAt(i))
			{
				case '+': 	if(forward>0)
							{
								turtle.moveTurtleForward(distance*forward, true);
								forward=0;
							}
							else if(forwardInvisible>0)
							{
								turtle.moveTurtleForward(distance*forwardInvisible, false);
								forwardInvisible=0;
							}
							turtle.rotateTurtle(-angle); break;
				
				case '-': 	if(forward>0)
							{
								turtle.moveTurtleForward(distance*forward, true);
								forward=0;
							}
							else if(forwardInvisible>0)
							{
								turtle.moveTurtleForward(distance*forwardInvisible, false);
								forwardInvisible=0;
							}
							turtle.rotateTurtle(+angle); break;
				
				case '[': 	if(forward>0)
							{
								turtle.moveTurtleForward(distance*forward, true);
								forward=0;
							}
							else if(forwardInvisible>0)
							{
								turtle.moveTurtleForward(distance*forwardInvisible, false);
								forwardInvisible=0;
							}
							turtle.addPositionToStack(); break;
				
				case ']': 	if(forward>0)
							{
								turtle.moveTurtleForward(distance*forward, true);
								forward=0;
							}
							else if(forwardInvisible>0)
							{
								turtle.moveTurtleForward(distance*forwardInvisible, false);
								forwardInvisible=0;
							}
							turtle.restorePositionFromStack(); break;
				
				case ' ':   break;
				
				case '\t':  break;
				
				default:    if(forwardInvisible>0)
							{
								turtle.moveTurtleForward(distance*forwardInvisible, false);
								forwardInvisible=0;
							}
							forward++;  //finchè si va avanti non chiedo di aggiungere punti, appena si curva ne aggiungo uno alla distanza giusta
			}
		}
		turtle.moveTurtleForward(distance*forward, true);
		if(autoCenter)
			turtle.centerDrawing();
		else
		{
			turtle.repaint();
		}

	}
	/*
	public void draw(String command, double distance, double angle)
	{
		draw(command, distance, angle, null, -1,' ', true);

	}
	 */

	public Map<Character, Color> getCharColor() {
		return charColor;
	}

	public void setCharColor(Map<Character, Color> charColor) {
		this.charColor = charColor;
	}
}
