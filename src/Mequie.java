
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Mequie {
	public static void main(String[] args) {
		System.out.println("cliente: myClient");
		Mequie client = new Mequie();
		client.connectToServer(args[0]);
	}

	private void connectToServer(String serverAddress) {

		try {

			String [] serverHostnamePort = serverAddress.split(":");
			String host = serverHostnamePort[0];
			int port = Integer.parseInt(serverHostnamePort[1]);

			Socket echoSocket = new Socket(host, port);

			ObjectInputStream in = new ObjectInputStream(echoSocket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(echoSocket.getOutputStream());

			Scanner scanner = new Scanner(System.in);

			System.out.println("Introduza as suas credenciais.");

			System.out.print("username: ");
	        String username = scanner.nextLine();

	        System.out.print("password: ");
			String password = scanner.nextLine();

			out.writeObject(username);
			out.writeObject(password);

			boolean auth = (Boolean) in.readObject();
			if (auth) {
				System.out.println("Autenticado com sucesso!");

				String fileName = "file.txt";
				FileInputStream inFile = new FileInputStream(fileName);

				int size = inFile.available();
				out.writeInt(size);

//				Java 9 way
//				byte[] buf = inFile.readAllBytes();
				byte[] buf = new byte[size];
				inFile.read(buf);

				out.write(buf, 0, size);
				System.out.println("Ficheiro '" + fileName + "' enviado.");

				inFile.close();

			} else {
				System.out.println("Autenticacao falhou.");
			}

			scanner.close();

			out.close();
			in.close();

			echoSocket.close();

		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
			System.out.println("Nao foi possivel resolver o endereco IP indicado. A terminar...");
			System.exit(-1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.out.println("Nao foi possivel estabelecer a ligacao ao servidor. A terminar...");
			System.exit(-1);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
