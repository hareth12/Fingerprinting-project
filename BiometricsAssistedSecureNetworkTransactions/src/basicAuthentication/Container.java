package basicAuthentication;
import java.io.File;
import java.io.Serializable;

/**
 * Container - A serializable, multipurpose class that can hold three types of data. Used to pass data between the client and the server.
 *
 */
public class Container implements Serializable{
	private String message;
	private byte data[];
	private File file;
	
	/**
	 * Constructor - creates Container with a String message
	 * @param inputMessage
	 */
	public Container(String inputMessage)
	{
		message = inputMessage;
		data = null;
		file = null;
	}
	
	/**
	 * Constructor - creates Container with a byte array field.
	 * @param inputData
	 */
	public Container(byte inputData[])
	{
		data = new byte[inputData.length];
		for(int k =0; k < data.length; k++)
		{
			data[k] = inputData[k];
		}
		message = null;
		file = null;
	}
	
	/**
	 * Constructor - creates Container with a File field.
	 * @param inputFile
	 */
	public Container(File inputFile)
	{
		inputFile = new File(inputFile.getAbsolutePath());
		message = null;
		data = null;
	}
	
	/**
	 * Gets the message field value.
	 * @return value of message.
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Gets the byte array field value.
	 * @return value of data.
	 */
	public byte[] getData()
	{
		return data;
	}
	
	/**
	 * Gets the File field value.
	 * @return value of file.
	 */
	public File getFile()
	{
		return file;
	}
}
