
import mequie.app.facade.Session;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Mequie {

	private static NetworkClient network = NetworkClient.getInstance();
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		if (args.length < 2 || args.length > 3) {
			System.out.println(
					"Incorrect usage of arguments. Example of use:\n\tMequie <serverAddress> <localUserID> [password]");
			System.exit(-1);
		}

		try {
            System.out.println("Establishing connection to server...");
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
		
		Session session = new Session(args[1], getPassword(args));

		Boolean auth = network.autenticaUser(session);
		if (auth) {
			System.out.println("Authentication successful!");

            printPossibleCommands();

            try {

				while (true)
					readNextCommand();

			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				System.out.println("Ficheiro nao encontrado");
			} catch ( IOException e) {
				System.err.println(e.getMessage());
				System.out.println("Erro ao enviar ficheiro");
			}

		} else {
			System.out.println("Authentication failed. Ending program...");
		}

        close();

    }

    private static void close() {
        scanner.close();

        try {
            network.networkClose();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void readNextCommand() throws IOException {
		String[] command = scanner.nextLine().split(" ");

		//TODO
		switch (command[0]) {

		    case "test" :
			    network.sendTestFile();
			    break;
			case "exit":
                System.out.println("Exiting program...");
			    close();
			    System.exit(0);
			    break;
			default:
				System.out.println("Incorrect command");
				printPossibleCommands();
		}

	}

	private static void printPossibleCommands() {
		System.out.println("Possible Commands:\n" +
				"\tcreate <groupID>\n" +
				"\taddu <userID> <groupID>\n" +
				"\tremoveu <userID> <groupID>\n" +
				"\tginfo <groupID>\n" +
				"\tuinfo\n" +
				"\tmsg <groupID> <msg>\n" +
				"\tphoto <groupID> <photo>\n" +
				"\tcollect <groupID>\n" +
				"\thistory <groupID>\n" +
                "\texit\n");
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
