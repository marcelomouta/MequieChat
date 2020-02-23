/***************************************************************************
*   Seguranca e Confiabilidade 2016/17
*
*
***************************************************************************/

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//Servidor myServer

public class MequieServer{

	public static void main(String[] args) {
		System.out.println("servidor: main");

		if (args.length != 1) {
			System.out.println("Numero de argumentos invalido. Exemplo de uso:\n\tMequieServer <port>");
			System.exit(-1);
		}

		try {
			int port = Integer.parseInt(args[0]);
			startServer(port);

		} catch (NumberFormatException e) {
			System.out.println("O porto tem que ser um numero");
			System.exit(-1);
		}

	}

	private static void startServer(int port){
        
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

}