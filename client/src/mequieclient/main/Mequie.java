package mequieclient.main;

import mequieclient.app.facade.exceptions.MequieException;
import mequieclient.app.network.NetworkClient;
import mequieclient.app.skel.CommandHandler;

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

		Boolean auth = cHandler.authentication(args[1], getPassword(args));
		if (auth) {
			
            printPossibleCommands();
            
            while (true)
            	doNextCommand();

		} else {
			System.out.println("Authentication failed. Ending program...");
		}

        close();

    }
	
	/**
	 * Closes the client.
	 */
    private static void close() {
        scanner.close();

        try {
            network.closeConnection();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * Read and execute next command from cli
     */
    private static void doNextCommand() {

		//reads command from cli
		String[] command = scanner.nextLine().split(" ",3);

		try {
			//TODO
			switch (command[0]) {
				case "create": case "c":
					cHandler.createGroup(command[1]);
					break;
				case "addu": case "a":
					cHandler.add(command[1],command[2]);
					break;
				case "removeu": case "r":
					cHandler.remove(command[1],command[2]);
					break;
				case "ginfo": case "g":
					cHandler.groupInfo(command[1]);
					break;
				case "uinfo": case "u":
					cHandler.userInfo();
					break;
				case "msg": case "m":
					cHandler.message(command[1],command[2]);
					break;
				case "photo": case "p":
					cHandler.photo(command[1],command[2]);
					break;
				case "collect": case "co":
					cHandler.collect(command[1]);
					break;
				case "history": case "h":
					cHandler.history(command[1]);
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
			incorrectCommand("That command requires more arguments");
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.out.println("File unavailable");
		} catch (ArithmeticException e) {
			System.out.println("File size is too large");
		} catch (IOException e) {
			System.out.println("Error executing command");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MequieException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

	}

    /**
     * @param message message to include in formated print
     * Formated print for incorrect command
     */
	private static void incorrectCommand(String message) {
		System.out.println(message);
		printPossibleCommands();
	}

	/**
	 * Standard print for incorrect command
	 */
	private static void incorrectCommand() {
		incorrectCommand("Incorrect command");

	}

	/**
	 * Prints possible commands to use in cli
	 */
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
                "\texit");
	}

	/**
	 * Gets password form args or asks it to user if not provided
	 * @param args program arguments
	 * @return read password
	 */
	private static String getPassword(String[] args) {
		if (args.length == 3) {
			return args[2];
		} else {
			System.out.println("Introduza a password:");
			return scanner.nextLine();
		}
	}

}
