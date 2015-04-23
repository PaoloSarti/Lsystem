package l_system.persistence;

import java.io.Serializable;

public class WindowRestore implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int x;
	private int y;
	private int width;
	private int height;
	private L_System restore;
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public WindowRestore(int x, int y, int width, int height, L_System restore) 
	{
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.restore=restore;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public L_System getRestore() {
		return restore;
	}
	public void setRestore(L_System restore) {
		this.restore = restore;
	}
}
