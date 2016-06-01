package l_system;

import java.awt.Color;

import l_system.gui.ProportionPoint;

public interface Turtle {
	void clear();
	
	void setStartingPoint(ProportionPoint startingPoint);
	
	void setZoom(double zoom);
	
	void moveTurtleForward(double distance, boolean visible);
	
	void moveTurtleForward(double distance, boolean visible, Color color);
	
	void rotateTurtle(double angle);
	
	void restorePositionFromStack();
	
	void addPositionToStack();
	
	void centerDrawing();
	
	void repaint();
}
