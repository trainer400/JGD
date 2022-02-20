package jgd.window.listener;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

import jgd.window.listener.MouseEvent.MouseEventType;

/**
 * This class describes the mouse listener for the window and the 
 * mouse button callback.
 * @author Matteo Pignataro
 */
public class MouseListener extends GLFWMouseButtonCallback
{
	/**
	 * Last event registered
	 */
	private MouseEvent lastMouseEvent;
	
	/**
	 * Mouse Position Listener
	 */
	private MousePositionListener positionListener;
	
	/**
	 * Constructor
	 */
	public MouseListener()
	{
		//Initialize the position listener
		positionListener = new MousePositionListener();
	}
	
	@Override
	public void invoke(long window, int button, int action, int mods) 
	{
		//Check if the user pressed the button
		if(action == GLFW_PRESS)
		{
			//Create the new event
			lastMouseEvent = new MouseEvent(positionListener.getPosX(),
											positionListener.getPosY(),
											MouseEventType.values()[button < 3 ? button : 3]);
		}
		else if(action == GLFW_RELEASE)
		{
			//In case of a release i select the correspondent release
			lastMouseEvent = new MouseEvent(positionListener.getPosX(),
											positionListener.getPosY(),
											MouseEventType.values()[button < 3? button + 4 : 7]);
		}
	}
	
	/**
	 * @return Last mouse event. Null value in case of no recent event.
	 */
	public MouseEvent getLastMouseEvent() 
	{
		//Check if there is something to return. In case there isn't i return the simple mouse position movement
		if(lastMouseEvent == null) 
		{
			return new MouseEvent(positionListener.getPosX(),
								  positionListener.getPosY(),
								  MouseEventType.MOVE); 
		}
		
		//Create a temporary variable where i store the details
		MouseEvent temp = new MouseEvent(lastMouseEvent.getPosX(),
				 						 lastMouseEvent.getPosY(),
				 						 lastMouseEvent.getEventType());
										 
		//Destroy the mouse event
		this.lastMouseEvent = null;
		 
		//Return the temporary variable
		return temp; 
	}
	
	/**
	 * @return Mouse Position Listener
	 */
	public MousePositionListener getMousePositionListener() { return positionListener; }
}
