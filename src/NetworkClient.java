import mequie.app.facade.Session;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkClient {

    private NetworkClient() {
    }

    private static NetworkClient INSTANCE;

    public static NetworkClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetworkClient();
        }

        return INSTANCE;
    }

    private Socket echoSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /*
     * Esta função deve: - Obter o endereço do servidor (struct sockaddr_in) a base
     * da informação guardada na estrutura rtable; - Estabelecer a ligação com o
     * servidor; - Guardar toda a informação necessária (e.g., descritor do socket)
     * na estrutura rtable; - Retornar 0 (OK) ou -1 (erro).
     */
    public void connectToServer(String serverAddress) throws NumberFormatException, UnknownHostException, IOException {

        String[] serverHostnamePort = serverAddress.split(":");
        String host = serverHostnamePort[0];
        int port = Integer.parseInt(serverHostnamePort[1]);

        this.echoSocket = new Socket(host, port);

        this.in = new ObjectInputStream(this.echoSocket.getInputStream());
        this.out = new ObjectOutputStream(this.echoSocket.getOutputStream());
    }

    /*
     * Esta função deve: - Obter o descritor da ligação (socket) da estrutura
     * rtable_t; - Serializar a mensagem contida em msg; - Enviar a mensagem
     * serializada para o servidor; - Esperar a resposta do servidor; -
     * De-serializar a mensagem de resposta; - Retornar a mensagem de-serializada ou
     * NULL em caso de erro.
     */
    public void sendTestFile() throws IOException, FileNotFoundException {

        out.writeObject("test");

        String fileName = "file.txt";

        FileInputStream inFile = new FileInputStream(fileName);

        //TODO decide if long file is unsupported OR send through several byte[] if necessary
        int size = (int) new File(fileName).length();
        out.writeObject(size);

        byte[] buf = new byte[size];
        inFile.read(buf, 0, size);

        out.write(buf, 0, size);
        out.flush();
        System.out.println("Ficheiro '" + fileName + "' enviado.");

        inFile.close();
    }

    /*
     * A função network_close() fecha a ligação estabelecida por network_connect().
     */
    public void networkClose() throws IOException {

        if (this.out != null)
            this.out.close();

        if (this.in != null)
            this.in.close();

        this.echoSocket.close();
    }

    public Boolean autenticaUser(Session session) {
        try {
            out.writeObject(session);

            return (Boolean) in.readObject();

        } catch (ClassNotFoundException | IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
	}
}