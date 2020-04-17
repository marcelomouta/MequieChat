package mequie.app.network;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import mequie.app.facade.Session;
import mequie.app.facade.network.NetworkMessage;

/**
 * Client class that deals with network requests
 */
public class NetworkClient {

	private NetworkClient() {
	}

	private static NetworkClient INSTANCE;

	public static NetworkClient getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NetworkClient();
		}

		return INSTANCE;
	}

	private SSLSocket echoSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	
	/**
	 * Connects to the server
	 * @param host hostname of the server
	 * @param port port of the server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connectToServer(String host, int port, String truststore) throws UnknownHostException, IOException {
		System.setProperty("javax.net.ssl.trustStore", truststore);
		
		SocketFactory sf = SSLSocketFactory.getDefault();
		this.echoSocket = (SSLSocket) sf.createSocket(host, port);

		this.in = new ObjectInputStream(this.echoSocket.getInputStream());
		this.out = new ObjectOutputStream(this.echoSocket.getOutputStream());
	}


	/**
	 * Sends message to the server and receives its response
	 * @param msg network message to send
	 * @return received network message response from the server
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public NetworkMessage sendAndReceive(NetworkMessage msg) throws IOException, ClassNotFoundException {

		out.writeObject(msg);
		out.flush();
		NetworkMessage msgResponse = (NetworkMessage) this.in.readObject();
		return msgResponse;
	}
	
	/**
	 * Starts the authentication process, sendig a session to the server with the users ID and receiving a nonce
	 * @param session Session with users id
	 * @return session with nonce and unknown user flag
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Session startAuthentication(Session session) throws ClassNotFoundException, IOException {

		out.writeObject(session);
		out.flush();
		
		return (Session) in.readObject();
	}

	/**
	 * Tries to authenticate the current user on the server,
	 *  sending a session with the necessary proof that the user is who says it is
	 *  and receives the servers answer with the authentication result
	 * @param session containing cifred nonce with user private key 
	 * @return response from the server
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public NetworkMessage authenticateUser(Session session) throws IOException, ClassNotFoundException {
		
		out.writeObject(session);
		out.flush();
		
		return (NetworkMessage) in.readObject();
	}


	/**
	 * Closes the connection to the server
	 * @throws IOException
	 */
	public void closeConnection() throws IOException {

		if (this.out != null)
			this.out.close();

		if (this.in != null)
			this.in.close();

		this.echoSocket.close();
	}



}