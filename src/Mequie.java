
import mequie.app.facade.Session;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Mequie {

	private static Scanner scanner = new Scanner(System.in);
	private static NetworkClient network = NetworkClient.getInstance();
	private static CommandHandler cHandler = new CommandHandler(network);


	public static void main(String[] args) {

		if (args.length < 2 || args.length > 3) {
			System.out.println(
					"Incorrect usage of arguments. Example of use:\n\tMequie <serverAddress> <localUserID> [password]");
			System.exit(-1);
		}

		try {

            System.out.println("Establishing connection to server...");

			String host = args[0].split(":")[0];
			int port = Integer.parseInt(args[0].split(":")[1]);

			network.connectToServer(host,port);

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
					doNextCommand();

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

    private static void doNextCommand() throws IOException {

		//reads command from cli
		String[] command = scanner.nextLine().split(" ",3);

		try {
			//TODO
			switch (command[0]) {
				case "create":
					if (cHandler.createGroup(command[1]))
						System.out.println("Group" + command[1] + "created.");
					else
						System.out.println("Error creating group.");
					break;
				case "addu":
					cHandler.add(command[1],command[2]);
					break;
				case "removeu":
					cHandler.remove(command[1],command[2]);
					break;
				case "ginfo":
					String groupInfo = cHandler.groupInfo();
					if (groupInfo != null)
						System.out.println(groupInfo);
					else
						System.out.println("Error retrieving group info");
					break;
				case "uinfo":
					String userInfo = cHandler.userInfo();
					if (userInfo != null)
						System.out.println(userInfo);
					else
						System.out.println("Error retrieving user info");
					break;
				case "msg":
					cHandler.message(command[1],command[2]);
					break;
				case "photo":
					cHandler.photo(command[1],command[2]);
					break;
				case "collect":
					cHandler.collect(command[1]);
					break;
				case "history":
					cHandler.history(command[1]);
					break;
				case "test":
					cHandler.test();
					break;
				case "exit":
					System.out.println("Exiting program...");
					close();
					System.exit(0);
					break;
				default:
					incorrectCommand();
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			incorrectCommand();
		}

	}

	private static void incorrectCommand() {
		System.out.println("Incorrect command");
		printPossibleCommands();
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
