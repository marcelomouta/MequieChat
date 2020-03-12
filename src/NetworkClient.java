import mequie.app.facade.Session;
import mequie.app.network.NetworkMessage;
import mequie.app.network.NetworkMessageRequest;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

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
    public void connectToServer(String host, int port) throws UnknownHostException, IOException {


        this.echoSocket = new Socket(host, port);

        this.in = new ObjectInputStream(this.echoSocket.getInputStream());
        this.out = new ObjectOutputStream(this.echoSocket.getOutputStream());
    }

    
    
//    // ideia de SD
//    public Resposta sendReceive(Pedido p)  {
//    	// TODO
//    }
    
    
    public NetworkMessage sendAndReceive(NetworkMessage msg) throws IOException, ClassNotFoundException {
    	
        out.writeObject(msg);
        out.flush();
        NetworkMessage msgResponse = (NetworkMessage) this.in.readObject();
        return msgResponse;
    }
    
    /*
     * Esta função deve: - Obter o descritor da ligação (socket) da estrutura
     * rtable_t; - Serializar a mensagem contida em msg; - Enviar a mensagem
     * serializada para o servidor; - Esperar a resposta do servidor; -
     * De-serializar a mensagem de resposta; - Retornar a mensagem de-serializada ou
     * NULL em caso de erro.
     */
    public void sendTestFile(FileInputStream inFile, int size) throws IOException {
    	
        //out.writeObject("test");

        //out.writeObject(size);

        byte[] buf = new byte[size];
        inFile.read(buf, 0, size);

        //out.write(buf, 0, size);
        //out.flush();
        
        NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.TEST,
    			new ArrayList(Arrays.asList(new String(buf))));
        
        out.writeObject(msg);
        
        out.flush();
    }

    /*
     * A função closeConnection() fecha a ligação estabelecida por network_connect().
     */
    public void closeConnection() throws IOException {

        //out.writeObject("exit");

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