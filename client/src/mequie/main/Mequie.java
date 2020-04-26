package mequie.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import mequie.app.facade.exceptions.MequieException;
import mequie.app.network.NetworkClient;
import mequie.app.skel.CommandHandler;
import mequie.utils.ClientEncryption;
/**
 * Mequie Client 
 * @author Grupo 37
 * @author 51021 - Pedro Marques
 * @author 51110 - Marcelo Mouta
 * @author 51468 - Bruno Freitas
 *
 */
public class Mequie {

	private static Scanner scanner = new Scanner(System.in);
	private static NetworkClient network = NetworkClient.getInstance();
	private static CommandHandler cHandler = new CommandHandler(network);

	public static void main(String[] args) {

		if (args.length != 5) {
			System.out.println(
					"Incorrect usage of arguments. Example of use:\n\tMequie <serverAddress> <truststore> <keystore> <keystore_password> <localUserID>");
			System.exit(-1);
		}

		try {

            System.out.println("Establishing connection to server...");

			String host = args[0].split(":")[0];
			int port = Integer.parseInt(args[0].split(":")[1]);

			network.connectToServer(host,port, args[1]);

		} catch (NumberFormatException | UnknownHostException e) {
			System.err.println(e.getMessage());
			System.out.println("Nao foi possivel resolver o endereco IP indicado. A terminar...");
            System.exit(-1);
        } catch (IOException e) {
			System.err.println(e.getMessage());
			System.out.println("Nao foi possivel estabelecer a ligacao ao servidor. A terminar...");
            System.exit(-1);
		}
		
		ClientEncryption.loadKeys(args[2], args[3]);	

		Boolean auth = cHandler.authentication(args[4]);
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
			System.err.println(e.getMessage());
		} catch (MequieException e) {
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

}
