/***************************************************************************
*   Seguranca e Confiabilidade 2016/17
*
*
***************************************************************************/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

		private Socket socket = null;

		ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("thread do server para cada cliente");
		}
 
		public void run(){
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				String user = null;
				String passwd = null;
			
				try {
					user = (String)inStream.readObject();
					passwd = (String)inStream.readObject();
					System.out.println("thread: depois de receber a password e o user");
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}


				if (user.equals("user01") && passwd.equals("passwd")){
					outStream.writeObject(true);

					System.out.println("user01 foi autenticado com sucesso.");
					System.out.println("A receber ficheiro...");

					int fileSize = (Integer) inStream.readObject();

					byte[] fileBuf = new byte[fileSize];
					inStream.read(fileBuf,0,fileSize);

					System.out.println("Ficheiro recebido.");
				}
				else {
					outStream.writeObject(false);
					System.out.println("Autenticacao falhou: username ou password incorretos");
				}

				outStream.close();
				inStream.close();
 			
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}