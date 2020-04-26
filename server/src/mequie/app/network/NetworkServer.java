package mequie.app.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
//import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import mequie.app.skel.MequieSkel;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.AuthenticationFailedException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.network.NetworkMessage;
import mequie.app.facade.network.NetworkMessageError;
import mequie.app.facade.network.NetworkMessageRequest;

public class NetworkServer {

	private int port;
	private String keystorePath;
	private String keystorePasswd;

	public NetworkServer(int port, String keystorePath, String keystorePasswd) {
		this.port = port;
		this.keystorePath = keystorePath;
		this.keystorePasswd = keystorePasswd;
	}

	public void start() {
		System.setProperty("javax.net.ssl.keyStore", this.keystorePath);
		System.setProperty("javax.net.ssl.keyStoreType", "JCEKS");
		System.setProperty("javax.net.ssl.keyStorePassword", this.keystorePasswd);
		
		ServerSocketFactory ssf = SSLServerSocketFactory.getDefault( );
		
		try (SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(this.port)){

			while(true) {
				try {
					Socket inSoc = ss.accept();
					ServerThread newServerThread = new ServerThread(inSoc);
					newServerThread.start();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
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

				// receives a Session with only the username of the user trying to authenticate
				sessao = (Session) inStream.readObject();
				
				// sends Session object to the client with nonce + unknown flag
				long nonce = new Random().nextLong();
				sessao.setNonce(nonce);
				
				boolean existsFlag = MequieSkel.userExists(sessao.getUsername());
				sessao.setUnknownUserFlag(!existsFlag);
				
				outStream.writeObject(sessao);
				outStream.flush();
				
				// Receive the session with the clients signature
				sessao = (Session) inStream.readObject();

				// resets session initial nonce + flag in case user changed them (safety precaution)
				sessao.setNonce(nonce);
				sessao.setUnknownUserFlag(!existsFlag);
				
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
					System.out.println("Authentication Failed");
					sendMessage(new NetworkMessageError(NetworkMessage.Opcode.AUTH, new AuthenticationFailedException()));
				} catch (IOException e1) {
					// Do nothing because finally will be called
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Invalid Message received");
			} catch (IOException e) {
				// Do nothing because finally will be called
			} catch (ErrorSavingInDiskException e) {
				System.out.println(e.getMessage());
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
			System.out.println("User " + (sessao == null ? "" : sessao.getUsername()) + " disconnected from server.");
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
