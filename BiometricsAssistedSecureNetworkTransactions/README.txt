Biometrics Assisted Secure Network Transactions

This directory contains two applications
-------------------------------------------

(1) Basic Client/Server implementation of Secure Network Authentication. A dummy fingerprint is used, pulled from the images/ directory. This implementation selects a set of randomly chosen bytes from the fingerprint image taken in by the client, and the server compares this set of bytes to those pulled from its own copy of the fingerprint image. If the two sets match, the client's attempt at authentication is approved. This implementation can be found in the src/basicAuthentication directory. 
	Classes:
		1) BiometricReader - the client application. Instances of this class will attempt to connect to a RemoteServer instance, so make sure to create such an instance before attempting to run the client. 
		2) RemoteServer - the server application. Uses socket 5678. 
		3) Container - a helper class for serializing different types of data between the BiometricReader client and the RemoteServer. 

(2) Spaced Frequency Transformation Authentication - An incomplete implementation of a more advanced form of authentication. The client takes in a fingerpritn image, uses Fourier Transformation and other techniques to binarize the fingerprint, then uses this binarized image as an authentication key. The server receives this key, generates its own from a saved finerprint image, and attempts to match the keys. If they do match, the client is authenticated; otherwise, it is rejected. This implementation is not complete - as of 10/27/2014, the client only sets the fingerprint to grayscale and performs the first few steps of transformation. Further work must be done in the Binarizer class located in src/helperClasses directory to complete this implementation.
	Classes:
		1) BiometricReader - the client application.
		2) RemoteServer  - the server application. Uses socket 5678.
		3) Container - a helper class.
		4) Various helperClasses located in the src/helperClasses directory - used for binarization and transformation of the fingerprint.

		
Other directories
----------------
(1) images/
Contains images used for testing, including a dummy fingerprint image (fingerprint.jpg). Output files created through the binarization process are stored here for testing purposes.

(2) keys/
Contains private and public key files for the Biometric Reader and Remote Server to communicate through SSL sockets. The machine running the remote server must have the biometric reader's public key, and the remote server's private key. The machine running a biometric reader client must have the remote server's public key, and the biometric reader's private key.

(3) libs/
Contains a jar file used for the binarization process.

(4) bin/
Contains the binary class files.