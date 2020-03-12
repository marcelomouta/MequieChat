/***************************************************************************
*   Seguranca e Confiabilidade 2016/17
*
*
***************************************************************************/

import mequie.app.facade.handlers.LoadingFromDiskHandler;
import mequie.app.network.NetworkServer;

public class MequieServer{

	public static void main(String[] args) {
		System.out.println("servidor: main");
		MequieServer server = new MequieServer();
		
		try {
			LoadingFromDiskHandler.load();
		} catch (Exception e1) {
			System.out.println("ERROR ao fazer o loading");
		}

		if (args.length != 1) {
			System.out.println("Numero de argumentos invalido. Exemplo de uso:\n\tMequieServer <port>");
			System.exit(-1);
		}

		try {
			NetworkServer network = new NetworkServer(Integer.parseInt(args[0]));
			network.start();
		} catch (NumberFormatException e) {
			System.out.println("O porto tem que ser um numero");
		}
	}

}