package mequieclient.app.skel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.MequieException;
import mequieclient.app.network.NetworkClient;
import mequieclient.app.network.NetworkMessage;
import mequieclient.app.network.NetworkMessageError;
import mequieclient.app.network.NetworkMessageRequest;
import mequieclient.app.network.NetworkMessageResponse;

public class CommandHandler {

	private static NetworkClient network;
	private int generator = 1; // to generate the id of income photos

	public CommandHandler(NetworkClient network) {
		this.network = network;
	}

	public boolean authentication(String user, String pass) {
		if (user.contains(":")) {
			System.out.println("Invalid userID: ':' is a reserved symbol");
			return false;
		}
		try {
			Session session = new Session(user, pass);
			NetworkMessage res;

			res = network.autenticaUser(session);

			if (res instanceof NetworkMessageError) {
				NetworkMessageError err = (NetworkMessageError) res;
				System.out.println(res.toString());
				return false;
			} else {
				System.out.println("Authentication successful!");
				return true;
			}
		} catch (Exception e) {
			return false;
		}

	}

	public void createGroup(String newGroupID) throws ClassNotFoundException, IOException, MequieException {
		if (newGroupID.contains(":")) {
			System.out.println("Invalid groupID: ':' is a reserved symbol");
			return;
		}

		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.CREATE_GROUP,
				new ArrayList(Arrays.asList(newGroupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		printResult(msgServer);
	}



	public void add(String userID, String groupID) throws MequieException, ClassNotFoundException, IOException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.ADD_USER_TO_GROUP,
				new ArrayList(Arrays.asList(userID, groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		printResult(msgServer);
	}

	public void remove(String userID, String groupID) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.REMOVE_USER_FROM_GROUP,
				new ArrayList(Arrays.asList(userID, groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		printResult(msgServer);
	}

	public void groupInfo(String groupID) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_GROUP_INFO,
				new ArrayList(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			res.getMsgs().forEach(text -> System.out.println(text) );
		}
	}

	public void userInfo() throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_USER_INFO,
				new ArrayList());
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;

			System.out.println("Grupos a que pertence:");
			List<String> groups = res.getMsgs();
			if (!groups.isEmpty()) {
				groups.forEach(text -> System.out.println("\t" + text) );
				System.out.println("Grupos a que e dono:");
				res.getMsg2().forEach(text -> System.out.println("\t" + text) );
			} else {
				System.out.println("Geral");
			}
		}
	}

	public void message(String groupID, String txtMsg) throws ClassNotFoundException, IOException, MequieException {
		
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.SEND_TEXT_MESSAGE,
				new ArrayList(Arrays.asList(groupID, txtMsg)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		printResult(msgServer);
	}

	public void photo(String groupID, String fileName) throws IOException, ClassNotFoundException, MequieException {
		FileInputStream inFile = new FileInputStream(fileName);
		int size = Math.toIntExact(new File(fileName).length());
		byte[] buf = new byte[size];
		inFile.read(buf, 0, size);
		inFile.close();

		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.SEND_PHOTO_MESSAGE,
				new ArrayList(Arrays.asList(groupID)), buf);
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		printResult(msgServer);
	}

	public void collect(String groupID) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.COLLECT_NOT_VIEWED_MESSAGES_OF_GROUP,
				new ArrayList(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer; 
		
		List<String> msgsToRead = msgResponse.getMsgs();
		List<byte[]> photos = msgResponse.getPhotos();
		
		if (msgsToRead.isEmpty() && photos.isEmpty())
			printEmptyCollectMsgs(groupID, "");
		else {
			if (!msgsToRead.isEmpty())
				msgsToRead.forEach(text -> System.out.println(text) );
			else
				printEmptyCollectMsgs(groupID, "text ");
			
			if (!photos.isEmpty()) {
				for (byte[] photo : photos) {
	                String path = "ClientData/photos_" + groupID + "/" + (generator++);
	                System.out.println("Photo: " + path);
	                writePhoto(photo, path);
				}
			} else
				printEmptyCollectMsgs(groupID, "photo ");
		}
		
	}

	private void printEmptyCollectMsgs(String groupID, String msgType) {
		System.out.println("There are no " + msgType + "messages to read on '" + groupID + "'.");
	}

	public void history(String groupID) throws MequieException, ClassNotFoundException, IOException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.MESSAGE_HISTORY_OF_GROUP,
				new ArrayList(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			List<String> history = res.getMsgs();
			if (!history.isEmpty())
				history.forEach(text -> System.out.println(text) );
			else 
				System.out.println("Group '" + groupID + "' does not have a message history yet.");
		}
	}    

	private void checkIfMessageIsAnError(NetworkMessage msgServer) throws MequieException {
		if(msgServer instanceof NetworkMessageError) {
			NetworkMessageError msgError = (NetworkMessageError) msgServer;
			throw msgError.getException();
		}
	}
	
	private void printResult(NetworkMessage msgServer) {
		if(msgServer instanceof NetworkMessageResponse) {
			System.out.println(((NetworkMessageResponse) msgServer).getResult());
		}
	}

	private void writePhoto(byte[] photo, String path) {
		File fileToWrite = new File(path);
		fileToWrite.getParentFile().mkdirs();
		
		try(FileOutputStream writer = new FileOutputStream(fileToWrite)) {
			writer.write(photo);
			writer.flush();
		} catch (IOException e) {
			System.out.println("Nao foi possivel gravar a foto recebida.");
		}
	}
}
