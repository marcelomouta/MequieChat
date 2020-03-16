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

	/*public void test() throws IOException, ArithmeticException {

		String fileName = "file.txt";

		FileInputStream inFile = new FileInputStream(fileName);

		int size = Math.toIntExact(new File(fileName).length());

		network.sendTestFile(inFile, size);
		System.out.println("Ficheiro '" + fileName + "' enviado.");

		inFile.close();
	}*/

	public boolean authentication(String user, String pass) {
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

		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.CREATE_GROUP,
				new ArrayList(Arrays.asList(newGroupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			System.out.println(((NetworkMessageResponse) msgServer).getResult());
		}
	}



	public void add(String userID, String groupID) throws MequieException, ClassNotFoundException, IOException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.ADD_USER_TO_GROUP,
				new ArrayList(Arrays.asList(userID, groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			System.out.println(((NetworkMessageResponse) msgServer).getResult());
		}
	}

	public void remove(String userID, String groupID) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.REMOVE_USER_FROM_GROUP,
				new ArrayList(Arrays.asList(userID, groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			System.out.println(((NetworkMessageResponse) msgServer).getResult());
		}
	}

	public String groupInfo(String groupID) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_GROUP_INFO,
				new ArrayList(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			res.getMsgs().forEach(text -> System.out.println(text) );
		}

		NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer;
		return msgResponse.getResult();
	}

	public String userInfo() throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_USER_INFO,
				new ArrayList());
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			System.out.println("Grupos a que pertence:");
			res.getMsgs().forEach(text -> System.out.println(text) );
			System.out.println("Grupos a que e dono:");
			res.getPhotos().forEach(text -> System.out.println(text) );
		}

		NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer;
		return msgResponse.getResult();
	}

	public void message(String groupID, String txtMsg) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.SEND_TEXT_MESSAGE,
				new ArrayList(Arrays.asList(groupID, txtMsg)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			System.out.println(((NetworkMessageResponse) msgServer).getResult());
		}
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

		if(msgServer instanceof NetworkMessageResponse) {
			System.out.println(((NetworkMessageResponse) msgServer).getResult());
		}
	}

	public String collect(String groupID) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.COLLECT_NOT_VIEWED_MESSAGES_OF_GROUP,
				new ArrayList(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer; 

		msgResponse.getMsgs().forEach(text -> System.out.println(text) );
		// TODO: Salvar todas as fotos em msgResponse.getPhotos() numa pasta à parte
		List<byte[]> photos = msgResponse.getPhotos();
		if (!photos.isEmpty()) {
			String path = "ClientData/photos_" + groupID + "/" + (generator++);
			System.out.println("Fotos: " + path);
			for (byte[] photo : photos) {
				writePhoto(photo, path);
			}
		}

		return msgResponse.getResult();
	}

	public String history(String groupID) throws MequieException, ClassNotFoundException, IOException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.MESSAGE_HISTORY_OF_GROUP,
				new ArrayList(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			res.getMsgs().forEach(text -> System.out.println(text) );
		}

		NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer;
		return msgResponse.getResult();
	}    

	private void checkIfMessageIsAnError(NetworkMessage msgServer) throws MequieException {
		if(msgServer instanceof NetworkMessageError) {
			NetworkMessageError msgError = (NetworkMessageError) msgServer;
			throw msgError.getException();
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
