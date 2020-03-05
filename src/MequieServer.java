/***************************************************************************
*   Seguranca e Confiabilidade 2016/17
*
*
***************************************************************************/

import mequie.app.facade.Session;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

//Servidor myServer

public class MequieServer{

	public static void main(String[] args) {
		System.out.println("servidor: main");
		MequieServer server = new MequieServer();

		if (args.length != 1) {
			System.out.println("Numero de argumentos invalido. Exemplo de uso:\n\tMequieServer <port>");
			System.exit(-1);
		}

		try {
			server.startServer(Integer.parseInt(args[0]));
		} catch (NumberFormatException e) {
			System.out.println("O porto tem que ser um numero");
		}
	}

	private void startServer(int port){
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
			
				try {
					sessao = (Session) inStream.readObject();
					System.out.println("thread: depois de receber a password e o user");
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}


				if (sessao.getUser().getUserID().equals("user01") && sessao.getUser().getPassword().equals("passwd")){
					outStream.writeObject(true);

					System.out.println("User " + sessao.getUser().getUserID() + " was successfully authenticated");

					while(true) {
						NetworkMessage msg = (NetworkMessage) inStream.readObject();
						System.out.println(msg.toString());
						receiveFile(msg);
					}
				}
				else {
					outStream.writeObject(false);
					System.out.println("Autenticacao falhou: username ou password incorretos");
				}



			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				System.out.println("User " + sessao.getUser().getUserID() + " disconnected from server.");
				try {
					outStream.close();
					inStream.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void receiveFile(NetworkMessage msg) throws IOException, ClassNotFoundException {
			System.out.println("A receber ficheiro...");

			byte[] file = msg.getArguments().get(0).getBytes(StandardCharsets.UTF_8);
			
			System.out.println("Recebeu o conteudo do ficheiro: " + msg.getArguments().get(0));

			System.out.println("Ficheiro recebido.");
		}
	}
}