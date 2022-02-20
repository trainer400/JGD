package jgd.render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

/**
 * This class represents a VAO. A VAO is composed of a VBO and an EBO.
 * The VBO (Vertex Buffer Object) contains all the vertices informations.
 * The EBO all the vertices that create every triangle.
 * When the draw method is called for the first time, the vao instances
 * a buffer array for the vbo and another buffer array for the ebo.
 * They both have fixed dimensions so after the first draw it will be
 * impossible to add another element. It will be possible though to modify
 * the already registered elements but not their vertices number.
 * @author Matteo Pignataro
 */
public class VAO 
{
	/**
	 * vaoID
	 */
	private int vaoID;
	
	/**
	 * vboID
	 */
	private int vboID;
	
	/**
	 * eboID
	 */
	private int eboID;
	
	/**
	 * Vertex top index
	 */
	private int vertexSize;
	
	/**
	 * Element top index
	 */
	private int elementSize;
	
	/**
	 * Shader assigned to this VAO
	 */
	private Shader shader;
	
	/**
	 * The optional texture assigned
	 */
	private Texture texture;
	
	/**
	 * Attributes list
	 */
	private List<Integer> attributes;
	
	/**
	 * List of Drawable elements
	 */
	private List<DrawableElement> elements;
	
	/**
	 * Boolean that represents if the VAO has already been drawn
	 */
	private boolean drawn;
	
	/**
	 * Constructor
	 * @param shader The shader that the user wants to execute
	 * @param texture The texture that the user wants to bind
	 */
	public VAO(Shader shader, Texture texture)
	{
		//I expect shader and texture not null
		if(shader == null)
		{
			throw new NullPointerException("Error shader null pointer [VAO]");
		}
		else 
		{
			//Immutable class
			this.shader = shader;
		}
		
		//Create the VAO, VBO and EBO buffers
		vaoID = glGenVertexArrays();
		vboID = glGenBuffers();
		eboID = glGenBuffers();
		
		//Set the vertex and the element indices at 0
		vertexSize 	= 0;
		elementSize = 0;
		
		//Instance the attributes list
		attributes = new ArrayList<Integer>();
		
		//Instance the Drawable element list
		elements = new ArrayList<DrawableElement>();
		
		//The VAO has not been drawn
		drawn = false;
		
		//This is optional
		this.texture = texture;
	}
	
	/**
	 * Callback method called when the window size is changed
	 * @param width The new width
	 * @param height The new height
	 */
	public void updateWindowSize(int width, int height)
	{
		//Forall the elements in the VAO i call the same method
		elements.stream().forEach((DrawableElement e) -> e.updateWindowSize(width, height));
	}
	
	/**
	 * Constructor with only the shader support
	 * @param shader The shader that the user wants to execute
	 */
	public VAO(Shader shader)
	{
		this(shader, null);
	}
	
	/**
	 * Method to draw the entire VAO.
	 * It allocates the buffers if it is the first draw call.
	 */
	public void draw()
	{
		//First of all we bind the VAO
		bind();
		
		//In case of the first call i allocate the buffers
		if(!drawn)
		{
			//Bind the VBO
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			
			//Put a new buffer with the correct dimensions in the vbo
			//I use stream_draw because it is usal to change some parameters
			glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertexSize), GL_STREAM_DRAW);
			
			//Bind the EBO
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
			
			//Put a new buffer with the correct dimensions in the ebo
			//I use static_draw because it is difficult that the triangle
			//draw order is changed
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(elementSize), GL_STATIC_DRAW);
			
			//I invalidate the addElement function
			drawn = true;
		}
		
		//Temporary Indices useful for counting the vertex and element positions
		int vertexIndex = 0;
		int elementIndex = 0;
		//Sum of attributes per vertex
		int sumAttributes = (attributes.stream().reduce(0, Integer::sum)).intValue();
		//Temporary buffers to insert the values
		FloatBuffer bufferFloat;
		IntBuffer bufferInt;
		
		//For all the updated elements i update them in the buffers
		for(int i = 0; i < elements.size(); i++)
		{
			if(elements.get(i).isUpdated())
			{
				//Create the buffers
				bufferFloat = BufferUtils.createFloatBuffer(elements.get(i).getVerticesSize())
										 .put(elements.get(i).getVertices());
				
				//Cast the buffer to avoid compatibility errors
				((Buffer) bufferFloat).flip();
				
				bufferInt = BufferUtils.createIntBuffer(elements.get(i).getElementsSize());
				
				//I need to traslate the elements number with the number of vertices added before
				for(int j = 0; j < elements.get(i).getElementsSize(); j++)
				{
					bufferInt.put(elements.get(i).getElements()[j] + vertexIndex / sumAttributes);
				}
				
				//Flip the element buffer
				((Buffer) bufferInt).flip();
				
				//Bind the VBO
				glBindBuffer(GL_ARRAY_BUFFER, vboID);
				
				//Insert the vertex array
				glBufferSubData(GL_ARRAY_BUFFER, vertexIndex * Float.BYTES, bufferFloat);
				
				//Bind the EBO
				glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
				
				//Insert the element array
				glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, elementIndex * Integer.BYTES, bufferInt);
				
				//At the end i toggle the update
				elements.get(i).toggleUpdate();
			}
			
			vertexIndex += elements.get(i).getVerticesSize();
			elementIndex += elements.get(i).getElementsSize();
		}
		
		//Bind the VBO
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		
		//Partial sum of attributes. It is used to indicate the offset
		int partialSum = 0;
		
		//For all attributes i create the attribute pointer
		for(int i = 0; i < attributes.size(); i++)
		{
			//Create the attribute pointer
			glVertexAttribPointer(i, attributes.get(i).intValue(), GL_FLOAT, false, sumAttributes * Float.BYTES, partialSum * Float.BYTES);
			
			//Enable the vertex attribute pointer
			glEnableVertexAttribArray(i);
			
			//Increment the partial sum
			partialSum += attributes.get(i);
		}
		
		//Draw the VAO
		glDrawElements(GL_TRIANGLES, elementIndex, GL_UNSIGNED_INT, 0);
		
		//Bind the VBO
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		
		//Disable all the attrinutes pointers
		for(int i = 0; i < attributes.size(); i++)
		{
			glDisableVertexAttribArray(i);
		}
		
		//Unbind
		unbind();
	}
	
	/**
	 * Method to add a drawable element to the VAO BEFORE
	 * the first draw method call
	 * @param e the DrawableElement to be added
	 */
	public void addElement(DrawableElement e)
	{
		//If the list doesn't already contain the element i can add it
		if(!elements.contains(e) && !drawn)
		{
			//Add the element
			elements.add(e);
			//Increase vertexSize
			vertexSize += e.getVerticesSize();
			//Increase elementSize
			elementSize += e.getElementsSize();
		}
	}
	
	/**
	 * Add attribute size method. It is designed to identify groups of values
	 * in the vertex buffer object. (E.g. vector3f for a 3D position.. addAttribute(3))
	 * @param the group size
	 */
	public void addAttribute(int dim)
	{
		//If it has value
		if(dim > 0)
		{
			attributes.add(dim);
		}
	}
	
	/**
	 * Binding method
	 */
	public void bind() 
	{
		//Bind the vao
		glBindVertexArray(vaoID);
		//Bind the shader
		shader.bind();
		//Bind the texture in case not null
		if(texture != null)
		{
			texture.bind();
		}
	}
	
	/**
	 * Unbinding method
	 */
	public void unbind() 
	{
		//Unbind the vao
		glBindVertexArray(0);
		//Unbind the shader
		shader.unbind();
		//Unbind the texture in case not null
		if(texture != null)
		{
			texture.unbind();
		}
	}
	
	/**
	 * Clean method
	 */
	public void clean()
	{
		//Unbind all
		unbind();
		
		//If present delete the vao
		if(vaoID != 0)
		{
			glDeleteVertexArrays(vaoID);
		}
		
		//Clean the shader
		shader.clean();
		//Clean the texture if present
		if(texture != null)
		{
			texture.clean();
		}
	}
}
