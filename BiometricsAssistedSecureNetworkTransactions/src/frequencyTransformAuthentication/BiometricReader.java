package frequencyTransformAuthentication;
import helperClasses.Complex;
import helperClasses.Fourier2D;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Vector;
import java.io.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Biometric Reader - client. Implementation unfinished. Establishes a connection with the remote server. Take in a fingerprint image,
 * then generate an authentication key to send to the server. When the server receives the key, it generates its own version and compares.
 * Authentication approved if the two keys match, authentication refused otherwise. 
 * Calculations used to generate Authentication key unfinished - requires further implementation.
 *
 */
public class BiometricReader {

	private static final String password = "j9a4jfsjo!9%7$h7";
	public static void main(String[] args) {
		try {
			BufferedImage bloodVesselImage = getBloodVesselImage();
			BufferedImage fingerprint = getFingerprint();
			boolean isLive = testLiveliness(bloodVesselImage);
			if(!isLive)
			{
				System.out.println("Not a live fingerprint! Closing...");
				return;
			}
			else
				System.out.println("Live fingerprint! Generating AuthKey from Fingerprint Image");
			
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextInt();
			String publicPassphrase = "public";
			KeyStore biometricReaderKeyStore = KeyStore.getInstance( "JKS" );
			biometricReaderKeyStore.load( new FileInputStream( "keys/biometricreader.private" ), "biometricreaderprivate".toCharArray() );
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( biometricReaderKeyStore, "biometricreaderprivate".toCharArray() );
			
			
			KeyStore remoteServerKeyStore = KeyStore.getInstance( "JKS");
			remoteServerKeyStore.load( new FileInputStream ("keys/remoteserver.public"), publicPassphrase.toCharArray());
			TrustManagerFactory remoteServerTMF = TrustManagerFactory.getInstance( "SunX509");
			remoteServerTMF.init( remoteServerKeyStore);
			SSLContext remoteServerSslContext = SSLContext.getInstance( "TLS");
			remoteServerSslContext.init(kmf.getKeyManagers(), remoteServerTMF.getTrustManagers(), secureRandom );
			SSLSocketFactory remoteServerSslSocketFactory = remoteServerSslContext.getSocketFactory();
			SSLSocket remoteServerSocket = (SSLSocket) remoteServerSslSocketFactory.createSocket("localhost", 5678);
			remoteServerSocket.setUseClientMode(true);
			
			
			String ready = "Ready";
			Container readyContainer = new Container(ready);

			// (2) BR --> RS: Ready
			ObjectOutputStream toRemoteServer = new ObjectOutputStream(remoteServerSocket.getOutputStream());
			toRemoteServer.writeObject(readyContainer);
			System.out.println("Sent Ready Message to Remote Server");
			toRemoteServer.flush();
			
			// (6) BR --> RS: Auth Key

			Complex authKey[][] = generateAuthKey(fingerprint);
			Container authKeyContainer = new Container(authKey);
			//Send authKey
			toRemoteServer.writeObject(authKeyContainer);
			toRemoteServer.flush();
			System.out.println("Sent Auth Key to Remote Server");

			/*for(int x = 0; x < authKey.length; x++)
			{
				String line = "";
				for(int y = 0; y < authKey[x].length; y++)
				{
					line += " [" + authKey[x][y].real + "]";
				}
				System.out.println(line);
			}*/
			// (8) RS --> BR: ACK
			ObjectInputStream fromRemoteServer = new ObjectInputStream(remoteServerSocket.getInputStream());
			Container ackContainer = (Container)fromRemoteServer.readObject();
			String ack = ackContainer.getMessage();
			System.out.println("Received ACK from Remote Server: " + ack);
			if (ack.equals("OK")) {
				System.out.println("ACCEPTED!");
			} else {
				System.out.println("REJECTED!");
			}

			fromRemoteServer.close();
			toRemoteServer.close();
			remoteServerSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static boolean testLiveliness(BufferedImage bloodVesselImage) {
		// TODO Auto-generated method stub
		if(bloodVesselImage.getData().getHeight() > 0 && bloodVesselImage.getData().getWidth() > 0)
			return true;
		else
			return false;
	}


	private static Complex[][] generateAuthKey(BufferedImage fingerprint) throws IOException {
		int[][] fingerprintPixelValues;
		
		int fingerprintWidth = fingerprint.getWidth();
		int fingerprintHeight = fingerprint.getHeight();
		
		int highestPower2Width = Integer.highestOneBit(fingerprintWidth-1);
		int highestPower2Height = Integer.highestOneBit(fingerprintHeight-1);
		if(highestPower2Width != fingerprintWidth || highestPower2Height != fingerprintHeight)
		{
			BufferedImage scaledFingerprint = toBufferedImage(fingerprint.getScaledInstance(highestPower2Width, highestPower2Height, Image.SCALE_SMOOTH));
			fingerprintPixelValues = getPixelValues(scaledFingerprint);
			fingerprintWidth = scaledFingerprint.getWidth();
			fingerprintHeight = scaledFingerprint.getHeight();
		}
		else
		{
			fingerprintPixelValues = getPixelValues(fingerprint);
		}

	
		System.out.println("Width: " + fingerprintWidth);
		System.out.println("Height: " + fingerprintHeight);
		Fourier2D f2d = new Fourier2D(fingerprintWidth, fingerprintHeight);
		Complex G[][] = new Complex[fingerprintWidth][fingerprintHeight];
		for(int x = 0; x < fingerprintWidth; x ++)
		{
			for(int y = 0; y < fingerprintHeight; y++)
			{
				G[x][y] = new Complex((1.0) * fingerprintPixelValues[x][y], 0.0);
			}
		}
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Beginning fourier calculations on Client at: " + sdf.format(cal.getTime()));
		Complex result[][] = f2d.fft(G);
		System.out.println("Ending fourier calculations on Client at: " + sdf.format(cal.getTime()));
		return result;
	}


	private static BufferedImage getFingerprint() {
		BufferedImage fingerprint = null;
		try {
			fingerprint = ImageIO.read(new File("images/fingerprint.jpg"));
		} catch (IOException e) {
		}
		return fingerprint;
	}

	private static BufferedImage getBloodVesselImage() {
		BufferedImage bloodVesselImage = null;
		try {
			bloodVesselImage = ImageIO.read(new File("images/bloodVesselImage.jpg"));
		} catch (IOException e) {
		}
		return bloodVesselImage;
	}

	private static int[][] getPixelValues(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] result = new int[width][height];

		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
		    	result[col][row] = image.getRGB(col, row);
		    }
		}
		return result;
	}
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
}
