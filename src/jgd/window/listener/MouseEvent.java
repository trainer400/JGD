package jgd.window.listener;

/**
 * Mouse events not mutable class with coordinates and click type informations
 * @author Matteo Pignataro
 */
public class MouseEvent 
{	
	public static enum MouseEventType
	{
		LEFT_CLICK, 	RIGHT_CLICK, 	CENTER_CLICK,	MOVE,
		LEFT_RELEASE,	RIGHT_RELEASE,	CENTER_RELEASE;
	}
	
	/**
	 * Click X coordinate
	 */
	private double X;
	/**
	 * Click Y coordinate
	 */
	private double Y;
	
	/**
	 * Click Event Type
	 */
	private MouseEventType type;
	
	/**
	 * Constructor
	 * @param X the mouse X position when the event was created
	 * @param Y the mouse Y position when the event was created
	 * @param type the event type
	 */
	public MouseEvent(double X, double Y, MouseEventType type) { this.X = X; this.Y = Y; this.type = type; }
	
	/**
	 * Void constructor
	 */
	public MouseEvent() { this(0, 0, MouseEventType.RIGHT_CLICK); }
	
	/**
	 * @return X mouse coordinate
	 */
	public double getPosX() { return X; }
	
	/**
	 * @return Y mouse coordinate
	 */
	public double getPosY() { return Y; }
	
	/**
	 * @return Click event type
	 */
	public MouseEventType getEventType() { return type; }
}