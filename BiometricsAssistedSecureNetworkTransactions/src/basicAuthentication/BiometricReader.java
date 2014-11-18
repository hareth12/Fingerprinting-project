package basicAuthentication;
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
import java.util.HashSet;
import java.util.Vector;
import java.io.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * BiometricReader - The client application. Establishes a secure connection to a RemoteServer instance through the use of 
 * SSL and keystores. Key files can be found in the main project directory. 
 * 
 *
 */
public class BiometricReader {

	/**
	 * Predefined password used for testing
	 */
	private static final String password = "j9a4jfsjo!9%7$h7";
	
	/**
	 * Main method - Main thread of execution. 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			/**
			 * Generate a cryptographically strong random number
			 */
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextInt();
			
			/**
			 * Public passphrase for remote server key store
			 */
			String publicPassphrase = "public";
			
			/**
			 * Create instance for this class's key store
			 */
			KeyStore biometricReaderKeyStore = KeyStore.getInstance( "JKS" );
			biometricReaderKeyStore.load( new FileInputStream( "keys/biometricreader.private" ), "biometricreaderprivate".toCharArray() );
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( biometricReaderKeyStore, "biometricreaderprivate".toCharArray() );
			
			/**
			 * Establish instance for the remote server's key store
			 */
			KeyStore remoteServerKeyStore = KeyStore.getInstance( "JKS");
			remoteServerKeyStore.load( new FileInputStream ("keys/remoteserver.public"), publicPassphrase.toCharArray());
			TrustManagerFactory remoteServerTMF = TrustManagerFactory.getInstance( "SunX509");
			remoteServerTMF.init( remoteServerKeyStore);
			
			/**
			 * Generate SSL socket to connect to remote server
			 */
			SSLContext remoteServerSslContext = SSLContext.getInstance( "TLS");
			remoteServerSslContext.init(kmf.getKeyManagers(), remoteServerTMF.getTrustManagers(), secureRandom );
			SSLSocketFactory remoteServerSslSocketFactory = remoteServerSslContext.getSocketFactory();
			SSLSocket remoteServerSocket = (SSLSocket) remoteServerSslSocketFactory.createSocket("localhost", 5678);
			remoteServerSocket.setUseClientMode(true);
			
			/**
			 * Load dummy blood vessel image for comparison
			 */
			File bloodVesselImage = getBloodVesselImage();
			
			/**
			 * Load dummy fingerprint image
			 */
			File fingerprint = getFingerprint();
			
			/**
			 * Generate a byte array from the blood vesell image
			 */
			byte bloodVesselBytes[] = getBytes(bloodVesselImage);
			
			/**
			 * Format initial message to server
			 */
			String ready = "Ready";
			Container readyContainer = new Container(ready);

			// (2) BR --> RS: Send ready message to Remote Server
			ObjectOutputStream toRemoteServer = new ObjectOutputStream(remoteServerSocket.getOutputStream());
			toRemoteServer.writeObject(readyContainer);
			System.out.println("Sent Ready Message to Remote Server");
			toRemoteServer.flush();

			// (4) RS --> BR: BIK: Receive a Byte Key from Remote Server - Serves as an Authentication Key
			ObjectInputStream fromRemoteServer = new ObjectInputStream(remoteServerSocket.getInputStream());
			
			Container bikContainer = (Container) fromRemoteServer.readObject();
			String bik = bikContainer.getMessage();
			System.out.println("Received BIK from Remote Server: " + bik);
			
			// (6) BR --> RS: Auth Key - Generate an Authentication key on Client side, send it back to the Remote Server.
			byte authKey[] = generateAuthKey(bik, fingerprint);
			Container authKeyContainer = new Container(authKey);
			System.out.println("Number of bytes in Auth Key: " + authKey.length);
			
			//Send authKey
			toRemoteServer.writeObject(authKeyContainer);
			toRemoteServer.flush();
			System.out.println("Sent Auth Key to Remote Server");


			// (8) RS --> BR: Receive an Acknowledgement message from the Server - "OK" if the generate Auth key has been accepted, otherwise
			// Authentication failed
			Container ackContainer = (Container)fromRemoteServer.readObject();
			String ack = ackContainer.getMessage();
			System.out.println("Received ACK from Remote Server: " + ack);
			if (ack.equals("OK")) {
				System.out.println("ACCEPTED!");
			} else {
				System.out.println("REJECTED!");
			}

			/**
			 * Close sockets
			 */
			fromRemoteServer.close();
			toRemoteServer.close();
			remoteServerSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate Authentication key from the received BIK and the loaded fingerprint
	 * @param bik - Key from the server
	 * @param fingerprint - Fingerprint image
	 * @return byteArray Authentication Key
	 * @throws IOException
	 */
	private static byte[] generateAuthKey(String bik, File fingerprint) throws IOException {
		
		byte[] fingerprintBytes = getBytes(fingerprint);
		
		//parse BIK for byte positions
		String[] tokens = bik.split(", ");
		tokens[0] = tokens[0].substring(1);
		tokens[tokens.length-1] = tokens[tokens.length-1].substring(0, tokens[tokens.length-1].length()-1);
		
		//for each position, retrieve the byte in that position 
		//store the byte values as a string
		byte array[] = new byte[tokens.length];
		int resultPos = 0;
		for(String token: tokens)
		{
			int pos = Integer.parseInt(token);
			byte fingerByte;
			if(pos < fingerprintBytes.length)
			{
				fingerByte = fingerprintBytes[pos];
				array[resultPos] = fingerByte;
				resultPos++; 
			}
			else
			{
				array[resultPos] = -1;
				resultPos++;
			}
		}
		return array;
	}

	/**
	 * Load Fingerprint image from main directory - dummy method used for testing
	 * @return
	 */
	private static File getFingerprint() {
		File fingerPrint = new File("images/fingerprint.jpg");
		return fingerPrint;
	}
	
	/**
	 * Load BloodVeseel iamge - dummy method
	 * @return
	 */
	private static File getBloodVesselImage() {
		File bloodVesselImage = new File("images/bloodVesselImage.jpg");
		return bloodVesselImage;
	}

	/**
	 * Turn an image into a byte array
	 * @param image - the File to be digitized
	 * @return a byte array contain the binary value of each byte in the file
	 */
	private static byte[] getBytes(File image) {
		
        FileInputStream fis;
        byte[] bytes = null;
		try 
		{
			fis = new FileInputStream(image);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
            for (int readNum; (readNum = fis.read(buf)) != -1;) 
            {
                //Writes to this byte array output stream
                bos.write(buf, 0, readNum); 
                bytes = bos.toByteArray();
            }
            fis.close();
        } 
		catch (IOException ex) {
        	ex.printStackTrace();
        }
		return bytes;
	}
}
