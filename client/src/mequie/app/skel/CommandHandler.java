package mequie.app.skel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import mequie.app.facade.Session;
import mequie.app.facade.exceptions.MequieException;
import mequie.app.facade.network.NetworkMessage;
import mequie.app.facade.network.NetworkMessage.Opcode;
import mequie.app.facade.network.NetworkMessageError;
import mequie.app.facade.network.NetworkMessageRequest;
import mequie.app.facade.network.NetworkMessageResponse;
import mequie.app.network.NetworkClient;
import mequie.utils.ClientEncryption;

/**
 * Class that handles the commands received from the client, sends them and receives their results via NetworkClient
 */
public class CommandHandler {

	private static NetworkClient network;
	private int generator = 1; // to generate the id of income photos
	private String userID;
	
	public CommandHandler(NetworkClient nw) {
		network = nw;
	}

	/**
	 * Tries to authenticate the user in the server
	 * @param user username
	 * @param pass password
	 * @return true if the user was authenticated, false otherwise
	 */
	@SuppressWarnings("unused")
	public boolean authentication(String user) {
		if (user.contains(":")) {
			System.out.println("Invalid userID: ':' is a reserved symbol");
			return false;
		}
		
		try {
			
			Session session = network.startAuthentication(new Session(user));
			
			// cifra nonce com private key
			long nonce = session.getNonce();
			session.setSignature(ClientEncryption.signsNonce(nonce));
			
			
			if (session.isUnknownUserFlag()) {
				 session.setUserCertificate(ClientEncryption.getCertificate());
			}
			
			NetworkMessage res = network.authenticateUser(session);

			if (res instanceof NetworkMessageError) {
				NetworkMessageError err = (NetworkMessageError) res;
				System.out.println(res.toString());
				return false;
			} else {
				userID = user;
				System.out.println("Authentication successful!");
				return true;
			}
		} catch (MequieException e) {
			System.out.println(e.getMessage());
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Create a new group command
	 * @param newGroupID id of the group to be created
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws MequieException
	 */
	public void createGroup(String newGroupID) throws ClassNotFoundException, IOException, MequieException {
		if (newGroupID.contains(":")) {
			System.out.println("Invalid groupID: ':' is a reserved symbol");
			return;
		}
		
		byte[] wrappedGroupKey = ClientEncryption.generateAndWrapNewUserGroupKey();
		SimpleEntry<String,byte[]> userWrappedGroupKey = new SimpleEntry<>(userID,wrappedGroupKey); 

		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.CREATE_GROUP,
				new ArrayList<>(Arrays.asList(newGroupID)),  new ArrayList<>(Arrays.asList(userWrappedGroupKey)) );
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		printResult(msgServer);
	}


	/**
	 * Add new user to a group command
	 * @param userID id of the user to be added
	 * @param groupID id of the group
	 * @throws MequieException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void add(String userID, String groupID) throws MequieException, ClassNotFoundException, IOException {
		
		addOrRemove(userID, groupID, NetworkMessage.Opcode.ADD_USER_TO_GROUP);

	}

	/**
	 * @param userID
	 * @param groupID
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws MequieException
	 */
	private void addOrRemove(String userID, String groupID, Opcode flag)
			throws IOException, ClassNotFoundException, MequieException {
		NetworkMessage msgServer = groupInfoMessage(groupID);
		
		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			List<String> groupInfo = res.getMsgs();
			
			boolean userIsOwner = groupInfo.size() > 2;
			if (!userIsOwner)
				throw new MequieException("ERROR the user is not the owner of this group");
			
			List<String> groupMembers = groupInfo.subList(2, groupInfo.size());
			// user to add is not in groupInfo
			groupMembers.add(userID);
			
			ArrayList<SimpleEntry<String,byte[]>> usersWrappedGroupKeys = ClientEncryption.generateAndWrapUsersGroupKeys(groupMembers);
			
			
			NetworkMessageRequest msg = new NetworkMessageRequest(flag,
					new ArrayList<>(Arrays.asList(userID, groupID)), usersWrappedGroupKeys);
			
			msgServer = network.sendAndReceive(msg);
			
			checkIfMessageIsAnError(msgServer);
			
			printResult(msgServer);
		}
	}

	/**
	 * Remove user from group command
	 * @param userID id of the user to be removed
	 * @param groupID id of the group
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws MequieException
	 */
	public void remove(String userID, String groupID) throws ClassNotFoundException, IOException, MequieException {
	
		addOrRemove(userID, groupID, NetworkMessage.Opcode.REMOVE_USER_FROM_GROUP);
		
	}

	/**
	 * Get info from group command
	 * @param groupID id of the group
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws MequieException
	 */
	public void groupInfo(String groupID) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessage msgServer = groupInfoMessage(groupID);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			List<String> groupInfo = res.getMsgs();
			printFormatedGroupInfo(groupInfo);
			
		}
	}

	/**
	 * @param groupID
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws MequieException
	 */
	private NetworkMessage groupInfoMessage(String groupID)
			throws IOException, ClassNotFoundException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_GROUP_INFO,
				new ArrayList<>(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);
		return msgServer;
	}
	
	private void printFormatedGroupInfo(List<String> groupInfo) {
		boolean userIsOwner = groupInfo.size() > 2;
		String basicInfo = "The group '" + groupInfo.get(0) + "' has " + groupInfo.get(1) + " users";
		if (userIsOwner) {
			System.out.println(basicInfo + ":");
			groupInfo.subList(2, groupInfo.size()).forEach(user -> System.out.println("\t" + user) );
		} else {
			System.out.println(basicInfo + ".");
		}
	}

	/**
	 * Get info from current user's command
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws MequieException
	 */
	public void userInfo() throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_USER_INFO,
				new ArrayList<>());
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;

			System.out.println("Groups that you belong to:");
			List<String> groups = res.getMsgs();
			if (!groups.isEmpty()) {
				groups.forEach(text -> System.out.println("\t" + text) );
				
				List<String> ownedGroups = res.getMsg2();
				if (!ownedGroups.isEmpty()) {
					System.out.println("Groups that you own:");
					ownedGroups.forEach(text -> System.out.println("\t" + text) );					
				}
				
			} else {
				System.out.println("You do not belong to any group");
			}
		}
	}

	/**
	 * Send message to a group command
	 * @param groupID id of the group
	 * @param txtMsg message to send
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws MequieException
	 */
	public void message(String groupID, String txtMsg) throws ClassNotFoundException, IOException, MequieException {
		// first thing is to get the last key of the group to be able to sent the msg encrypted
		byte[] encryptedKey = getTheKeyFromGroup(groupID);
			
		// now encrypt the message
		byte[] encryptedMessage = ClientEncryption.encryptMessage(txtMsg.getBytes(), encryptedKey);
		String encriptedString = convertEncryptedBytesToString(encryptedMessage);
		
		// now send it encrypted
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.SEND_TEXT_MESSAGE,
				new ArrayList<>(Arrays.asList(groupID, encriptedString)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		printResult(msgServer);
	}

	/**
	 * Send photo to a group command
	 * @param groupID id of the group
	 * @param fileName path of the file containing the photo
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws MequieException
	 */
	public void photo(String groupID, String fileName) throws IOException, ArithmeticException, ClassNotFoundException, MequieException {
		// first thing is to get the last key of the group to be able to sent the msg encrypted
		byte[] encryptedKey = getTheKeyFromGroup(groupID);
		
		// get the image
		FileInputStream inFile = new FileInputStream(fileName);
		int size = Math.toIntExact(new File(fileName).length());
		byte[] buf = new byte[size];
		inFile.read(buf, 0, size);
		inFile.close();
					
		// now encrypt the photo
		byte[] encryptedPhoto = ClientEncryption.encryptMessage(buf, encryptedKey);
		
		// now send it encrypted
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.SEND_PHOTO_MESSAGE,
				new ArrayList<>(Arrays.asList(groupID)), encryptedPhoto);
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		printResult(msgServer);
	}

	/**
	 * Collect unread messages from group command
	 * @param groupID id of the group
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws MequieException
	 */
	public void collect(String groupID) throws ClassNotFoundException, IOException, MequieException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.COLLECT_NOT_VIEWED_MESSAGES_OF_GROUP,
				new ArrayList<>(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		NetworkMessageResponse msgResponse = (NetworkMessageResponse) msgServer; 
		
		List<SimpleEntry<Integer, String>> msgsToRead = msgResponse.getTextMsgs();
		List<SimpleEntry<Integer, byte[]>> photos = msgResponse.getPhotos();
		Map<Integer, byte[]> keys = msgResponse.getUserKeys();
		
		if (msgsToRead.isEmpty() && photos.isEmpty())
			printEmptyCollectMsgs(groupID, "");
		else {
			if (!msgsToRead.isEmpty())
				msgsToRead.forEach(entry -> {
					byte[] key = keys.get(entry.getKey());
					decryptAndDisplayMsg(entry, key);
				});
			else
				printEmptyCollectMsgs(groupID, "text ");
			
			if (!photos.isEmpty()) {
				photos.forEach(entry -> {
					byte[] key = keys.get(entry.getKey());
					byte[] photo;
					try {
						photo = ClientEncryption.decryptMessage(entry.getValue(), key);
						String path = "ClientData/" + userID + "/photos_" + groupID + "/" + (generator++);
						System.out.println("Photo: " + path);
						writePhoto(photo, path);
					} catch (MequieException e) {
						System.out.println("ERROR could not decrypt this photo");
					}
					
				}); 
			} else
				printEmptyCollectMsgs(groupID, "photo ");
		}
		
	}

	/**
	 * @param entry
	 * @param key
	 */
	private void decryptAndDisplayMsg(SimpleEntry<Integer, String> entry, byte[] key) {
		String[] msgInfo = entry.getValue().split(":",3);
		byte[] encryptedMsg = convertEncryptedStringToBytes(msgInfo[2]);
		
		byte[] decryptedMsg;
		try {
			decryptedMsg = ClientEncryption.decryptMessage(encryptedMsg, key);
			System.out.println(msgInfo[1] + ": " + new String(decryptedMsg)); 
		} catch (MequieException e) {
			System.out.println("ERROR could not decrypt this message");
		}
	}

	/**
	 * @param encryptedString
	 * @return
	 */
	private byte[] convertEncryptedStringToBytes(String encryptedString) {
		return DatatypeConverter.parseBase64Binary(encryptedString);
	}
	
	/**
	 * @param encryptedBytes
	 * @return
	 */
	private String convertEncryptedBytesToString(byte[] encryptedBytes) {
		return DatatypeConverter.printBase64Binary(encryptedBytes);
	}

	/**
	 * Formated empty collect messages print
	 * @param groupID id of the group
	 * @param msgType empty message type 
	 */
	private void printEmptyCollectMsgs(String groupID, String msgType) {
		System.out.println("There are no " + msgType + "messages to read on '" + groupID + "'.");
	}

	/**
	 * Get message history from group command
	 * @param groupID id of the group
	 * @throws MequieException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void history(String groupID) throws MequieException, ClassNotFoundException, IOException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.MESSAGE_HISTORY_OF_GROUP,
				new ArrayList<>(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);

		checkIfMessageIsAnError(msgServer);

		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			List<SimpleEntry<Integer, String>> history = res.getTextMsgs();
			Map<Integer, byte[]> keys = res.getUserKeys();
			
			if (!history.isEmpty())
				
				history.forEach(entry -> {
					
					byte[] key = keys.get(entry.getKey());
					if (key != null) {
						
						decryptAndDisplayMsg(entry, key);
					}
				});
			else 
				System.out.println("Group '" + groupID + "' does not have a message history yet.");
		}
	}    

	/**
	 * Checks if an error was received from network
	 * @param msgServer message received from the server
	 * @throws MequieException
	 */
	private void checkIfMessageIsAnError(NetworkMessage msgServer) throws MequieException {
		if(msgServer instanceof NetworkMessageError) {
			NetworkMessageError msgError = (NetworkMessageError) msgServer;
			throw msgError.getException();
		}
	}
	
	/**
	 * Prints result received form network message
	 * @param msgServer message received from the server
	 */
	private void printResult(NetworkMessage msgServer) {
		if(msgServer instanceof NetworkMessageResponse) {
			System.out.println(((NetworkMessageResponse) msgServer).getResult());
		}
	}

	/**
	 * Writes received photo to disk
	 * @param photo photo content in bytes
	 * @param path path to write the photo in
	 */
	private void writePhoto(byte[] photo, String path) {
		File fileToWrite = new File(path);
		fileToWrite.getParentFile().mkdirs();
		
		try(FileOutputStream writer = new FileOutputStream(fileToWrite)) {
			writer.write(photo);
			writer.flush();
			addFileExtensionToPhoto(fileToWrite);
		} catch (IOException e) {
			System.out.println("Nao foi possivel gravar a foto recebida.");
		}
	}
	
	/**
	 * Adds the file extension to a photo
	 * @param file file object of the saved file
	 */
	private static void addFileExtensionToPhoto(File fileToWrite) throws IOException {
		String fileType = Files.probeContentType(fileToWrite.toPath());
		if(fileType != null && !fileType.equals("text/plain")) {
			String fileExtension = fileType.replaceAll(".*/", "");
			fileToWrite.renameTo(new File(fileToWrite.getPath() + "." + fileExtension));
		}
	}
	
	/**
	 * 
	 * @param groupID The group to get the key
	 * @return The key of the group encrypted
	 * @throws MequieException 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private byte[] getTheKeyFromGroup(String groupID) throws MequieException, ClassNotFoundException, IOException {
		NetworkMessageRequest msg = new NetworkMessageRequest(NetworkMessage.Opcode.GET_LAST_KEY_OF_GROUP, 
				new ArrayList<>(Arrays.asList(groupID)));
		NetworkMessage msgServer = network.sendAndReceive(msg);
							
		checkIfMessageIsAnError(msgServer);
						
		// get the key from msg
		byte[] encryptedKey = null;
		if(msgServer instanceof NetworkMessageResponse) {
			NetworkMessageResponse res = (NetworkMessageResponse) msgServer;
			encryptedKey = res.getKeyOfGroup();
			if (encryptedKey == null || encryptedKey.length == 0)
				throw new MequieException("Error empty key of group.");
		}
		
		return encryptedKey;
	}
}
