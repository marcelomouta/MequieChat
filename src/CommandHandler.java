import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import mequie.app.facade.exceptions.MequieException;
import mequie.app.network.NetworkMessage;
import mequie.app.network.NetworkMessageError;
import mequie.app.network.NetworkMessageRequest;
import mequie.app.network.NetworkMessageResponse;

public class CommandHandler {

    private static NetworkClient network;

    public CommandHandler(NetworkClient network) {
        this.network = network;
    }

    public void test() throws IOException, ArithmeticException {
    	  	
        String fileName = "file.txt";
        
        FileInputStream inFile = new FileInputStream(fileName);
        
        int size = Math.toIntExact(new File(fileName).length());
        
        network.sendTestFile(inFile, size);
        System.out.println("Ficheiro '" + fileName + "' enviado.");

        inFile.close();
    }

    public String createGroup(String newGroupID) throws ClassNotFoundException, IOException, MequieException {
    	
        NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.CREATE_GROUP,
    			new ArrayList(Arrays.asList(newGroupID)));
        NetworkMessage msgServer = network.sendAndReceive(msg);
        
        checkIfMessageIsAnError(msgServer);
        
        NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer;
        return msgResponse.getResult();
    }



	public void add(String userID, String groupID) throws MequieException, ClassNotFoundException, IOException {
        NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.ADD_USER_TO_GROUP,
    			new ArrayList(Arrays.asList(userID, groupID)));
        NetworkMessage msgServer = network.sendAndReceive(msg);
        
        checkIfMessageIsAnError(msgServer);
    }

    public void remove(String userID, String groupID) throws ClassNotFoundException, IOException, MequieException {
        NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.REMOVE_USER_FROM_GROUP,
    			new ArrayList(Arrays.asList(userID, groupID)));
        NetworkMessage msgServer = network.sendAndReceive(msg);
        
        checkIfMessageIsAnError(msgServer);
    }

    public String groupInfo(String groupID) throws ClassNotFoundException, IOException, MequieException {
        NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_GROUP_INFO,
    			new ArrayList(Arrays.asList(groupID)));
        NetworkMessage msgServer = network.sendAndReceive(msg);
        
        checkIfMessageIsAnError(msgServer);
        
        NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer;
        return msgResponse.getResult();
    }

    public String userInfo() throws ClassNotFoundException, IOException, MequieException {
        NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_USER_INFO,
        		new ArrayList());
        NetworkMessage msgServer = network.sendAndReceive(msg);
        
        checkIfMessageIsAnError(msgServer);
        
        NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer;
        return msgResponse.getResult();
    }

    public void message(String groupID, String msg) {
        //TODO
    }

    public void photo(String userID, String fileName) {
        //TODO
    }

    public void collect(String groupID) {
        //TODO
    }

    public void history(String groupID) {
        //TODO
    }    
    
    private void checkIfMessageIsAnError(NetworkMessage msgServer) throws MequieException {
        if(msgServer instanceof NetworkMessageError) {
        	NetworkMessageError msgError = (NetworkMessageError) msgServer;
        	throw msgError.getException();
        }
	}
}
