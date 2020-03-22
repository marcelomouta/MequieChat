package mequieclient.app.network;

import mequieclient.app.facade.Session;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

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

	private Socket echoSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	
	/**
	 * Connects to the server
	 * @param host hostname of the server
	 * @param port port of the server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connectToServer(String host, int port) throws UnknownHostException, IOException {


		this.echoSocket = new Socket(host, port);

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
	 * Tries to authenticate the current user on the server
	 * @param session containing current user credentials
	 * @return response from the server
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public NetworkMessage authenticateUser(Session session) throws IOException, ClassNotFoundException {
		
		out.writeObject(session);
		out.flush();
		NetworkMessage msgResponse = (NetworkMessage) in.readObject();
		return  msgResponse;
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