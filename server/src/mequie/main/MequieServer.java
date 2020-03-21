package mequie.main;
/***************************************************************************
*   Seguranca e Confiabilidade 2019/20
*
*
***************************************************************************/

import mequie.app.facade.handlers.LoadingFromDiskHandler;
import mequie.app.network.NetworkServer;

public class MequieServer{

	public static void main(String[] args) {
		System.out.println("servidor: main");
		
		if (args.length != 1) {
			System.out.println("Numero de argumentos invalido. Exemplo de uso:\n\tMequieServer <port>");
			System.exit(-1);
		}

		try {
			LoadingFromDiskHandler.load();
			
			NetworkServer network = new NetworkServer(Integer.parseInt(args[0]));
			network.start();
			
		} catch (NumberFormatException e) {
			System.out.println("O porto tem que ser um numero");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}