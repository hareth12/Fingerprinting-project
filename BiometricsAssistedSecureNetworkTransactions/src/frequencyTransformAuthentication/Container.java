package frequencyTransformAuthentication;
import helperClasses.Complex;

import java.io.File;
import java.io.Serializable;


public class Container implements Serializable{
	private String message;
	private Complex data[][];
	
	public Container(String inputMessage)
	{
		message = inputMessage;
		data = null;
	}
	
	public Container(Complex inputData[][])
	{
		data = new Complex[inputData.length][inputData[0].length];
		for(int i=0; i<inputData.length; i++)
		  for(int j=0; j<inputData[i].length; j++)
			    data[i][j]=inputData[i][j];
		message = null;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public Complex[][] getData()
	{
		return data;
	}
	
}
