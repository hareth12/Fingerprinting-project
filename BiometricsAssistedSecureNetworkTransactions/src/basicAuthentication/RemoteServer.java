package basicAuthentication;
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
import java.util.Arrays;
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
 * RemoteServer - Server application. Launch before any Client instances. Establishes an SSL socket to receive messages from any
 * BiometricReader client instances with a certain public key. Loops continously, waiting to receive connections, then spins off a thread
 * when a connection is established to handle that client's authentication attempt.
 *
 */
public class RemoteServer implements Runnable {

	/**
	 * SSL Socket variable, and stored Fingerprint image - would pull from a database in actual implementation.
	 */
	SSLSocket biometricReaderSocket;
	File image = new File("images/fingerprint.jpg");

	/**
	 * Constructer - Takes in a socket after connection, RemoteServer object then has methods for handling an authentication request.
	 * @param bioReaderSocket
	 */
	public RemoteServer(SSLSocket bioReaderSocket) {
		biometricReaderSocket = bioReaderSocket;
	}

	public static void main(String[] args) {
		try {
			/**
			 * Generate a secure random number
			 */
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextInt();
			
			/**
			 * Dummy public passphrase, used for keystore
			 */
			String publicPassphrase = "public";
			
			/**
			 * Establish remote server key manager factory, using remoteserver private key located in main directory.
			 */
			KeyStore remoteServerKeyStore = KeyStore.getInstance( "JKS" );
			remoteServerKeyStore.load( new FileInputStream( "keys/remoteserver.private" ), "remoteserverprivate".toCharArray() );
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( remoteServerKeyStore, "remoteserverprivate".toCharArray() );
			
			/**
			 * Establish an SSL socket to receive connections from clients using a specified key
			 */
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
			/**
			 * Loop continuously, spin off a new thread when a connection is accepted for authentication handling.
			 */
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
	/**
	 * Called when a new connection is accepted - main method for handling authentication request.
	 */
	public void run() {
		try {

			/**
			 * Establish an input stream to receive objects from the Biometric Reader client.
			 */
			ObjectInputStream fromBiometricReader = new ObjectInputStream(biometricReaderSocket.getInputStream());
			
			/**
			 * Container holding the ready message from Client
			 */
			Container readyContainer = (Container)fromBiometricReader.readObject();
			System.out.println("Received " + readyContainer.getMessage() + " Message from Biometric Reader");
			
			//Convert image to byte array
			byte[] bytes = getBytes(image);
			int byteArraySize = bytes.length;
			
			/**
			 * Generate a set of random positions in the byte array - BIK.
			 */
			HashSet<Integer> randomPositions = generateBIK(byteArraySize);
			String bik = randomPositions.toString();
			
			/**
			 * Create a container holding these random byte positions - send to client.
			 */
			Container bikContainer = new Container(bik);
			ObjectOutputStream toBiometricReader = new ObjectOutputStream(biometricReaderSocket.getOutputStream());
			toBiometricReader.writeObject(bikContainer);
			System.out.println("Sent BIK to Remote Server");
			toBiometricReader.flush();

			// (6) BR --> RS: Auth Key - receive Authentication key from the client
			//Read authKey
			Container authKeyContainer = (Container)fromBiometricReader.readObject();
			byte authKey[] = authKeyContainer.getData();
			System.out.println("Received AuthKey from Biometric Reader. Size = " + authKey.length);	   
			
			/**
			 * Generate the server authentication key using the bytes from the Fingerprint file and the BIK
			 */
			byte[] serverAuthKey = generateAuthKey(bytes, bik);
			
			/**
			 * Compare the generated Authentication key to the one the client sent.
			 */
			boolean validAuthKey = Arrays.equals(authKey, serverAuthKey);
			
			// (8) RS --> BR: Send ACK / NACK if Authentication Keys matched / did not match.
			String ack;
			if (validAuthKey)
				ack = "OK";
			else
				ack = "NO";
			Container ackContainer = new Container(ack);
			toBiometricReader.writeObject(ackContainer);
			System.out.println("Sent ACK to Biometric Reader");
			toBiometricReader.flush();
			
			/**
			 * Close socket for communication
			 */
			toBiometricReader.close();
			fromBiometricReader.close();
			biometricReaderSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate Authentication key using byte and the Byte Index Key
	 * @param bytes - byte array representing a Fingerprint image
	 * @param bik - a string containing positions to pull from the byte array
	 * @return authentication key - a set of bytes pulled from the byte array based on positions listed in the BIK.
	 */
	private byte[] generateAuthKey(byte[] bytes, String bik) {
		//parse CIK for byte positions
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
			if(pos < bytes.length)
			{
				fingerByte = bytes[pos];
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
	 * Convert an image file into a byte array, used for generating the authentication key.
	 * @param image - a File.
	 * @return byte array representing the contents of the image file.
	 */
	private byte[] getBytes(File image) {
		
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
        	
        }
		return bytes;
	}

	/**
	 * Generate the Byte Index key through choosing (100 - default) random positions within the size of the byte array.
	 * @param byteArraySize
	 * @return a HashSet<Integer> which contains the randomly chosen positions.
	 */
	private HashSet<Integer> generateBIK(int byteArraySize) {

	    int numRequired= 100;
	    Random random = new Random();
	    HashSet<Integer> randomPositions = new HashSet<Integer>(numRequired);

	    while(randomPositions.size()< numRequired) {
	    	while (randomPositions.add(random.nextInt(byteArraySize)) != true);
	    }
	    assert randomPositions.size() == numRequired;
        return randomPositions;
        
	}

}
