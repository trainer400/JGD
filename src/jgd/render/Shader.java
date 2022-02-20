package jgd.render;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Shader class represents all the GLSL programs that the pipeline GPU
 * executes when rasterizing the image
 * @author Matteo Pignataro
 */
public class Shader 
{
	/**
	 * Shader program ID
	 */
	private int programID;
	
	/**
	 * Vertex shader program ID
	 */
	private int vertexShaderID;
	
	/**
	 * Fragment shader program ID
	 */
	private int fragmentShaderID;
	
	/**
	 * Geomtry shader program ID
	 */
	private int geometryShaderID;
	
	/**
	 * Constructor
	 * @param vertexFile The file path to the vertex GLSL code
	 * @param fragmentFile The file path to the fragment GLSL code
	 * @param geometryFile The file path to the geometry GLSL code 
	 */
	public Shader(String vertexFile, String fragmentFile, String geometryFile)
	{
		//Create the gl program
		programID = glCreateProgram();
		
		//If the vertex is a file i create the shader
		if(vertexFile != null && (new File(vertexFile).isFile()))
		{
			vertexShaderID = createShader(vertexFile, GL_VERTEX_SHADER);
		}
		else 
		{
			vertexShaderID = -1;
		}
		
		//If the fragment is a file i create the shader
		if(fragmentFile != null && (new File(fragmentFile).isFile()))
		{
			fragmentShaderID = createShader(fragmentFile, GL_FRAGMENT_SHADER);
		}
		else
		{
			fragmentShaderID = -1;
		}
		
		//If the geometry is a file i create the shader
		if(geometryFile != null && (new File(geometryFile).isFile()))
		{
			geometryShaderID = createShader(geometryFile, GL_GEOMETRY_SHADER);
		}
		else 
		{
			geometryShaderID = -1;
		}
		
		//After all the shaders have been read and compiled i can link the program
		link();
	}
	
	/**
	 * This method reads the shader file, compiles it and links it
	 * @param filePath The string path to the shader file
	 * @param type The shader type
	 * @return The integer program ID
	 */
	private int createShader(String filePath, int type)
	{
		//The file at this point should be present
		String code;
		byte byteArray[] = null;
		
		//Create the shader
		int IDshader = glCreateShader(type);
		
		//Try to open the file and store it into an array
		try { byteArray = Files.readAllBytes(Paths.get(filePath)); }
		catch (Exception e) { return -1; }
		
		//Create the code
		code = new String(byteArray);
		
		//Set the shader code
		glShaderSource(IDshader, code);
		//Compile the code
		glCompileShader(IDshader);
		
		//Check the compilation results
		if(glGetShaderi(IDshader, GL_COMPILE_STATUS) == 0)
		{
			throw new RuntimeException("Shader compile error [Shader]: " + glGetShaderInfoLog(IDshader, 1024));
		}
		
		//If all went good i attach the shader to the program
		glAttachShader(programID, IDshader);
		
		return IDshader;
	}
	
	/**
	 * This method links all the shaders
	 */
	private void link()
	{
		//Linking process
		glLinkProgram(programID);
		
		//Check the linking results
		if(glGetProgrami(programID, GL_LINK_STATUS) == 0)
		{
			throw new RuntimeException("Shader program linking error [Shader]: " + glGetProgramInfoLog(programID, 1024));
		}
		
		//Detach all the shaders if they are present
		if(vertexShaderID != -1)
		{
			glDetachShader(programID, vertexShaderID);
		}
		
		if(fragmentShaderID != -1)
		{
			glDetachShader(programID, fragmentShaderID);
		}
		
		if(geometryShaderID != -1)
		{
			glDetachShader(programID, geometryShaderID);
		}
		
		//Validate the program
		glValidateProgram(programID);
		
		//Check the results
		if(glGetProgrami(programID, GL_VALIDATE_STATUS) == 0)
		{
			throw new RuntimeException("Shader program validation error [Shader]: " + glGetProgramInfoLog(programID, 1024));
		}
	}
	
	/**
	 * Bind method
	 */
	public void bind() { glUseProgram(programID); }
	
	/**
	 * Unbind method
	 */
	public void unbind() { glUseProgram(0); }
	
	/**
	 * Clean method
	 */
	public void clean()
	{
		//To clean all of the stuff i have to unbind first
		unbind();
		
		//If the program actually exists i delete it
		if(programID != 0)
		{
			glDeleteProgram(programID);
			//Turn the programID to 0
			programID = 0;
		}
	}
}
