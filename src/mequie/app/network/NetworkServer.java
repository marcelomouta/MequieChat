package mequie.app.network;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import mequie.app.facade.Session;
import mequie.app.facade.exceptions.AuthenticationFailedException;
import mequie.app.skel.MequieSkel;

public class NetworkServer {
	
	private int port;
	
	public NetworkServer(int port) {
		this.port = port;
	}
	
	public void start() {
//		ServerSocket sSoc = null;
        
		try (ServerSocket sSoc = new ServerSocket(port)){

			FileOutputStream logins = new FileOutputStream("users.txt",true);

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

		// sSoc.close();
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
					
					sessao = (Session) inStream.readObject();
					System.out.println("thread: depois de receber a password e o user");
					
					System.out.println(sessao.getUsername() + " : " + sessao.getPassword());
					
					MequieSkel skel = new MequieSkel(sessao);
					
					skel.autentication();
					sendMessage(new NetworkMessageResponse(NetworkMessage.Opcode.TEST, "OK"));

//					if (sessao.getUsername().equals("user01") && sessao.getPassword().equals("passwd")){
//						outStream.writeObject(true);

						System.out.println("User " + sessao.getUsername() + " was successfully authenticated");

						while(true) {
							NetworkMessageRequest msg = (NetworkMessageRequest) inStream.readObject();
							System.out.println(msg.toString());
//							receiveFile(msg);
							
							NetworkMessage resp = skel.invoke(msg);
							sendMessage(resp);
							
						}
//					} else {
//						outStream.writeObject(false);
//						System.out.println("Autenticacao falhou: username ou password incorretos");
//					}

				} catch (AuthenticationFailedException e) {
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
			
			private void sendMessage(NetworkMessage resp) throws IOException {
				outStream.writeObject(resp);
				outStream.flush();
				
				System.out.println("sent: " + resp.toString());
			}
			
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

			private void receiveFile(NetworkMessageRequest msg) throws IOException, ClassNotFoundException {
				System.out.println("A receber ficheiro...");

				byte[] file = msg.getArguments().get(0).getBytes(StandardCharsets.UTF_8);
				
				System.out.println("Recebeu o conteudo do ficheiro: " + msg.getArguments().get(0));

				System.out.println("Ficheiro recebido.");
			}
		}

}
