import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    public void sendAndReceive() throws IOException, FileNotFoundException {
        String fileName = "src/file.txt";

        FileInputStream inFile = new FileInputStream(fileName);

        int size = inFile.available();
        out.writeObject(size);

        byte[] buf = new byte[size];
        inFile.read(buf, 0, size);

        out.write(buf, 0, size);
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

    public Boolean autenticaUser(String username, String password) {
        try {
            out.writeObject(username);
            out.writeObject(password);

            return (Boolean) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
	}
}