package mequie.app.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
//import java.nio.charset.StandardCharsets;

import mequie.app.skel.MequieSkel;
import mequieClient.app.facade.Session;
import mequieClient.app.facade.exceptions.AuthenticationFailedException;
import mequieClient.app.network.NetworkMessage;
import mequieClient.app.network.NetworkMessageError;
import mequieClient.app.network.NetworkMessageRequest;
import mequieClient.app.network.NetworkMessageResponse;

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

				// Receive the session to authenticate client
				sessao = (Session) inStream.readObject();

				// Initialization of Skell
				MequieSkel skel = new MequieSkel(sessao);

				// Make authentication and send the Msg Packet to client
				skel.autentication();
				sendMessage(new NetworkMessageResponse(NetworkMessage.Opcode.TEST, "OK"));

				System.out.println("User " + sessao.getUsername() + " was successfully authenticated");

				// Execute the commands given by client (after authentication)
				while(true) {
					NetworkMessageRequest msg = (NetworkMessageRequest) inStream.readObject();
					System.out.println(msg.toString());
					
					// receiveFile(msg);

					NetworkMessage resp = skel.invoke(msg);
					sendMessage(resp);

				}

			} catch (AuthenticationFailedException e) { // username and password doesnt match
				try {
					System.out.println("Autenticacao falhou: username ou password incorretos");
					sendMessage(new NetworkMessageError(NetworkMessage.Opcode.TEST, new AuthenticationFailedException()));
				} catch (IOException e1) {
					// Do nothing because finally will be called
				}
			} catch (Exception e) {
				// Do nothing because finally will be called
			} finally {
				disconnect();
				this.interrupt();
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

//		private void receiveFile(NetworkMessageRequest msg) throws IOException, ClassNotFoundException {
//			System.out.println("A receber ficheiro...");
//
//			byte[] file = msg.getArguments().get(0).getBytes(StandardCharsets.UTF_8);
//
//			System.out.println("Recebeu o conteudo do ficheiro: " + msg.getArguments().get(0));
//
//			System.out.println("Ficheiro recebido.");
//		}
	}

}
