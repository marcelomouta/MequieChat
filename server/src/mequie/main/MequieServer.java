package mequie.main;
/***************************************************************************
*   Seguranca e Confiabilidade 2019/20
*
*
***************************************************************************/

import mequie.app.facade.exceptions.MequieException;
import mequie.app.facade.handlers.LoadingFromDiskHandler;
import mequie.app.network.NetworkServer;
import mequie.utils.Encryption;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents the main class of the server.
 * This class will load all the data in disk and start the server. Will put the
 * important information in memory to have a more efficient execution.
 */
public class MequieServer{

	public static void main(String[] args) {
		System.out.println("servidor: main");
		
		if (args.length != 3) {
			System.out.println("Invalid number of arguments. Usage example:\n\tMequieServer <port> <keystore> <keystore_password>");
			System.exit(-1);
		}

		try {
			String keystore = args[1];
			String passwdKeystore = args[2];
			
			// encryption configuration initialization
			Encryption.loadSecretKey(keystore, passwdKeystore);
			
			// load the system
			LoadingFromDiskHandler.load();
			
			// start the network
			NetworkServer network = new NetworkServer(Integer.parseInt(args[0]), keystore, passwdKeystore);
			network.start();
			
		} catch (NumberFormatException e) {
			System.out.println("ERROR the port has to be a number.");
		} catch (MequieException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR loading the system.\nProbably corrupted data in disk.");
		}
	}

}