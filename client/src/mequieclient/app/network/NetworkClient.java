package mequieclient.app.network;

import mequieclient.app.facade.Session;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

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

	/*
	 * Esta função deve: - Obter o endereço do servidor (struct sockaddr_in) a base
	 * da informação guardada na estrutura rtable; - Estabelecer a ligação com o
	 * servidor; - Guardar toda a informação necessária (e.g., descritor do socket)
	 * na estrutura rtable; - Retornar 0 (OK) ou -1 (erro).
	 */
	public void connectToServer(String host, int port) throws UnknownHostException, IOException {


		this.echoSocket = new Socket(host, port);

		this.in = new ObjectInputStream(this.echoSocket.getInputStream());
		this.out = new ObjectOutputStream(this.echoSocket.getOutputStream());
	}



	//    // ideia de SD
	//    public Resposta sendReceive(Pedido p)  {
	//    	// TODO
	//    }


	public NetworkMessage sendAndReceive(NetworkMessage msg) throws IOException, ClassNotFoundException {

		out.writeObject(msg);
		out.flush();
		NetworkMessage msgResponse = (NetworkMessage) this.in.readObject();
		return msgResponse;
	}
	
	public NetworkMessage autenticaUser(Session session) throws IOException, ClassNotFoundException {
		
		out.writeObject(session);
		out.flush();
		NetworkMessage msgResponse = (NetworkMessage) in.readObject();
		return  msgResponse;
	}


/*	public void sendTestFile(FileInputStream inFile, int size) throws IOException {

		//out.writeObject("test");

		//out.writeObject(size);

		byte[] buf = new byte[size];
		inFile.read(buf, 0, size);

		//out.write(buf, 0, size);
		//out.flush();

		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.AUTH,
				new ArrayList(Arrays.asList(new String(buf))));

		out.writeObject(msg);

		out.flush();
	}*/

	/*
	 * A função closeConnection() fecha a ligação estabelecida por network_connect().
	 */
	public void closeConnection() throws IOException {

		//out.writeObject("exit");

		if (this.out != null)
			this.out.close();

		if (this.in != null)
			this.in.close();

		this.echoSocket.close();
	}

}