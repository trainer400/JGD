package jgd.render;

/**
 * Class that describes the drawable standard
 * @author Matteo Pignataro
 */
public abstract class DrawableElement 
{	
	/**
	 * Boolean that represents the element status.
	 * If it needs to be refreshed by the VAO it is true.
	 * Default value = true
	 */
	protected boolean updated = true;
	
	/**
	 * @return the internal updated status
	 */
	public boolean isUpdated()	{ return updated; }
	
	/**
	 * Method that toggles the updated flag
	 */
	public void toggleUpdate() { updated = false; }
	
	/**
	 * Method to change the shape in case of a window size update
	 * @param width The new width
	 * @param height The new height
	 */
	public abstract void updateWindowSize(int width, int height);
	
	/**
	 * @return the object's vertices array
	 */
	public abstract float[] getVertices();
	
	/**
	 * @return the vertices array size
	 */
	public abstract int getVerticesSize();
	
	/**
	 * @return the object's elements array
	 */
	public abstract int[] getElements();
	
	/**
	 * @return the element array size
	 */
	public abstract int getElementsSize();
}
