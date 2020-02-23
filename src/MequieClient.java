
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MequieClient {

	private static NetworkClient network = NetworkClient.getInstance();
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("cliente: myClient");

		if (args.length < 2 || args.length > 3) {
			System.out.println(
					"Numero de parametros incorreto. Exemplo de uso:\n MequieClientClient <serverAddress> <localUserID> [password]");
			System.exit(-1);
		}

		try {
			network.connectToServer(args[0]);
		} catch (NumberFormatException | UnknownHostException e) {
			System.err.println(e.getMessage());
			System.out.println("Nao foi possivel resolver o endereco IP indicado. A terminar...");
            System.exit(-1);
        } catch (IOException e) {
			System.err.println(e.getMessage());
			System.out.println("Nao foi possivel estabelecer a ligacao ao servidor. A terminar...");
            System.exit(-1);
		}
		
		String username = args[1];
		String password = getPassword(args);

		Boolean auth = network.autenticaUser(username, password);
		if (auth) {
			System.out.println("Autenticado com sucesso!");

			try {
				network.sendAndReceive();
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				System.out.println("Ficheiro nao encontrado");
			} catch ( IOException e) {
				System.err.println(e.getMessage());
				System.out.println("Erro ao enviar ficheiro");
			}

		} else {
			System.out.println("Autenticacao falhou.");
		}

		scanner.close();
		
		try {
			network.networkClose();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	private static String getPassword(String[] args) {
		if (args.length == 3) {
			return args[2];
		} else {
			System.out.println("Introduza a password:");
			return scanner.nextLine();
		}
	}

}
