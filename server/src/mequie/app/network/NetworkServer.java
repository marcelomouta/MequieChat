package mequie.app.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
//import java.nio.charset.StandardCharsets;

import mequie.app.skel.MequieSkel;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.AuthenticationFailedException;
import mequie.app.facade.network.NetworkMessage;
import mequie.app.facade.network.NetworkMessageError;
import mequie.app.facade.network.NetworkMessageRequest;

public class NetworkServer {

	private int port;

	public NetworkServer(int port) {
		this.port = port;
	}

	public void start() {
		try (ServerSocket sSoc = new ServerSocket(port)){

			while(true) {
				try {
					Socket inSoc = sSoc.accept();
					ServerThread newServerThread = new ServerThread(inSoc);
					newServerThread.start();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IllegalArgumentException | IOException e) {
			System.out.println("Erro ao abrir porto do servidor.");
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	//Threads utilizadas para comunicacao com os clientes
	private class ServerThread extends Thread {

		private Socket socket;
		private ObjectOutputStream outStream;
		private ObjectInputStream inStream;
		private Session sessao;

		ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("thread do server para cada cliente");
		}

		public void run(){
			try {
				outStream = new ObjectOutputStream(socket.getOutputStream());
				inStream = new ObjectInputStream(socket.getInputStream());

				// temos de enviar o nonce
				
				// recebemos o nonce e a sessao e verificamos se conseguimos
				//desencriptar o nonce corretamente
				
				// Receive the session to authenticate client
				sessao = (Session) inStream.readObject();

				// Initialization of Skell
				MequieSkel skel = new MequieSkel(sessao);

				// Make authentication and send the Msg Packet to client
				sendMessage(skel.autentication());

				System.out.println("User " + sessao.getUsername() + " was successfully authenticated");

				// Execute the commands given by client (after authentication)
				while(true) {
					NetworkMessageRequest msg = (NetworkMessageRequest) inStream.readObject();
					System.out.println(msg.toString());

					NetworkMessage resp = skel.invoke(msg);
					sendMessage(resp);

				}

			} catch (AuthenticationFailedException e) { // username and password doesnt match
				try {
					System.out.println("Autenticacao falhou: username ou password incorretos");
					sendMessage(new NetworkMessageError(NetworkMessage.Opcode.AUTH, new AuthenticationFailedException()));
				} catch (IOException e1) {
					// Do nothing because finally will be called
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Invalid Message received");
			} catch (IOException e) {
				// Do nothing because finally will be called
			} finally {
				disconnect();
			}
		}
		
		/**
		 * Send a NetworkMessage resp to client
		 * @param resp the message to send
		 */
		private void sendMessage(NetworkMessage resp) throws IOException {
			outStream.writeObject(resp);
			outStream.flush();

			System.out.println("sent: " + resp.toString());
		}
		
		/**
		 * Disconnect the client
		 */
		private void disconnect() {
			System.out.println("User " + sessao.getUsername() + " disconnected from server.");
			try {
				outStream.close();
				inStream.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
