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
		server.startServer(Integer.parseInt(args[0]));
	}

	public void startServer (int port){
		ServerSocket sSoc = null;
        
		try {
			sSoc = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
         
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
		//sSoc.close();
	}


	//Threads utilizadas para comunicacao com os clientes
	class ServerThread extends Thread {

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

					int fileSize = inStream.readInt();

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
			}
		}
	}
}