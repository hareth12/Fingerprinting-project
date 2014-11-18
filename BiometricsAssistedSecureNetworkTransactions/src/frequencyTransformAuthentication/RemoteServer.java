package frequencyTransformAuthentication;
import helperClasses.Complex;
import helperClasses.Fourier2D;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Remote Server - Server Class. When an instance of this is running, it loops until it accepts a connection. It then waits to 
 * receive an authentication key from that client, generates its own key, and then compares the two. If they match, approve
 * the client; otherwise, reject the client.
 * Process for generating authentication key is unfinished.
 *
 */
public class RemoteServer implements Runnable {

	SSLSocket biometricReaderSocket;
	BufferedImage serverFingerprint = getFingerprint();
	int numRequiredPixels = 100;

	public RemoteServer(SSLSocket bioReaderSocket) {
		biometricReaderSocket = bioReaderSocket;
	}

	public static void main(String[] args) {
		try {
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextInt();
			
			String publicPassphrase = "public";
			KeyStore remoteServerKeyStore = KeyStore.getInstance( "JKS" );
			remoteServerKeyStore.load( new FileInputStream( "keys/remoteserver.private" ), "remoteserverprivate".toCharArray() );
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( remoteServerKeyStore, "remoteserverprivate".toCharArray() );
			
			KeyStore biometricreaderKeyStore = KeyStore.getInstance( "JKS" );
			biometricreaderKeyStore.load( new FileInputStream( "keys/biometricreader.public" ), publicPassphrase.toCharArray() );	
			TrustManagerFactory biometricreaderTMF = TrustManagerFactory.getInstance( "SunX509" );
			biometricreaderTMF.init( biometricreaderKeyStore );
			SSLContext biometricReaderSslContext = SSLContext.getInstance( "TLS" );
			biometricReaderSslContext.init( kmf.getKeyManagers(), biometricreaderTMF.getTrustManagers(), secureRandom );
			SSLServerSocketFactory biometricreaderSslSocketFactory = biometricReaderSslContext.getServerSocketFactory();
			
			SSLServerSocket receiveSocket = (SSLServerSocket) biometricreaderSslSocketFactory.createServerSocket(5678);
			receiveSocket.setNeedClientAuth( true );
			// (2) BR --> RS: Ready
			// Socket biometricReaderSocket = receiveSocket.accept();
			while(true)
			{
				(new Thread(new RemoteServer((SSLSocket)receiveSocket.accept()))).start();
			}
			//receiveSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {

			
			ObjectInputStream fromBiometricReader = new ObjectInputStream(biometricReaderSocket.getInputStream());
			Container readyContainer = (Container)fromBiometricReader.readObject();
			System.out.println("Received " + readyContainer.getMessage() + " Message from Biometric Reader");

			// (6) BR --> RS: Auth Key
			//Read authKey
			Container authKeyContainer = (Container)fromBiometricReader.readObject();

			Complex authKey[][] = authKeyContainer.getData();
			System.out.println("Received AuthKey from Biometric Reader");	   
			
			Complex[][] serverAuthKey = generateAuthKey(serverFingerprint);
			/*for(int x = 0; x < serverAuthKey.length; x++)
			{
				String line = "";
				for(int y = 0; y < serverAuthKey[x].length; y++)
				{
					line += " [" + serverAuthKey[x][y].real + "]";
				}
				System.out.println(line);
			}*/
			boolean validAuthKey = true;
			if(serverAuthKey.length != authKey.length || serverAuthKey[0].length != authKey[0].length)
			{
				validAuthKey = false;
				System.out.println("Dimensions of authKey: " + authKey.length + "x" + authKey[0].length + " do not match Server's");
			}
			else
			{
				outerloop:
				for(int x= 0; x < serverAuthKey.length; x++)
				{
					for(int y = 0; y < serverAuthKey[x].length; y++)
					{
						if(authKey[x][y].real != serverAuthKey[x][y].real)
						{
								validAuthKey = false;
								System.out.println("Value in authKey: " + authKey[x][y].real + " does not match Server: " + serverAuthKey[x][y]);
								break outerloop;
						}
					}
				}
			}
			
			// (8) RS --> BR: ACK
			ObjectOutputStream toBiometricReader = new ObjectOutputStream(biometricReaderSocket.getOutputStream());
			String ack;
			if (validAuthKey)
				ack = "OK";
			else
				ack = "NO";
			Container ackContainer = new Container(ack);
			toBiometricReader.writeObject(ackContainer);
			System.out.println("Sent ACK to Biometric Reader");
			toBiometricReader.flush();
			
			toBiometricReader.close();
			fromBiometricReader.close();
			biometricReaderSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				//System.out.println(x + ", " + y);
				G[x][y] = new Complex((1.0) * fingerprintPixelValues[x][y], 0.0);
			}
		}
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println("Beginning fourier calculations on Server at: " + sdf.format(cal.getTime()));
		Complex result[][] = f2d.fft(G);
		System.out.println("Ending fourier calculations on Server at: " + sdf.format(cal.getTime()));
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
