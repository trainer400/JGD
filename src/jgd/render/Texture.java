package jgd.render;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * This class represents a texture opened from a file
 * @author Matteo Pignataro
 */
public class Texture 
{
	/**
	 * Texture ID
	 */
	private int textureID;
	
	/**
	 * Texture width
	 */
	private int width;
	
	/**
	 * Texture height
	 */
	private int height;
	
	/**
	 * Constructor
	 */
	public Texture(String path)
	{
		//Generate the texture with OpenGL
		textureID = glGenTextures();
		
		//Check the texture exists and actually is a file
		if(path != null && (new File(path).isFile()))
		{
			createTexture(path);
		}
		else 
		{
			throw new RuntimeException("No texture file detected [Texture]");
		}
	}
	
	/**
	 * This method reads the file and links it to the texture generated
	 * with OpenGL
	 * @param filePath Texture file path
	 */
	private void createTexture(String filePath)
	{
		//First of all we bind the texture
		bind();
		
		//Set the texture parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); //Repeat in case of >1 coordinates
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); //Same on the y axis
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); //Select the nearest pixel
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); //The same in expansion
		
		//Create the buffer and read the image
		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
		
		//Read the image
		ByteBuffer image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 0);
		
		//Check if the image was read
		if(image == null)
		{
			throw new RuntimeException("Error reading the texture [Texture]: " + filePath);
		}
		
		//If all is good i assign the variables
		width = widthBuffer.get(0);
		height = heightBuffer.get(0);
		
		//Check how many channels the image is composed of and link it to the texture
		if(channelsBuffer.get(0) == 3)
		{
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, widthBuffer.get(0), heightBuffer.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
		}
		else if(channelsBuffer.get(0) == 4)
		{
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, widthBuffer.get(0), heightBuffer.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		}
		else
		{
			//Channel unreadable error
			throw new RuntimeException("Error reading the texture color scheme [Texture]: " + filePath);
		}
		
		//Free the memory
		stbi_image_free(image);
	}
	
	/**
	 * Binding method
	 */
	public void bind() { glBindTexture(GL_TEXTURE_2D, textureID); }
	
	/**
	 * Unbinding method
	 */
	public void unbind() { glBindTexture(GL_TEXTURE_2D, 0); }
	
	/**
	 * Clean method
	 */
	public void clean()
	{
		//unbind the texture
		unbind();
		
		//If the texture was created i delete it
		if(textureID != 0)
		{
			glDeleteTextures(textureID);
			//Turn the textureID to 0
			textureID = 0;
		}
	}
	
	/**
	 * Getters
	 */
	public int getWidth() 	{ return width; }
	public int getHeight() 	{ return height; }
}
