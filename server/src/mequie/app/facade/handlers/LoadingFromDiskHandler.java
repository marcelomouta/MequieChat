package mequie.app.facade.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.PhotoMessage;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.exceptions.MequieException;
import mequie.utils.Configuration;
import mequie.utils.Encryption;
import mequie.utils.ReadFromDisk;
import mequie.utils.ReadOperation;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class aims to load all the system to memory
*/
public class LoadingFromDiskHandler {
	
	private LoadingFromDiskHandler() {}
	
	/**
	 * Get all the users saved in disk
	 * Will get the information on: passwd.txt
	 * @return a list of users created by the information in disk
	 * @throws MequieException 
	 */
	private static List<User> getAllUsersFromDisk() throws IOException, MequieException {
		ReadFromDisk reader = new ReadFromDisk(Configuration.getUsersPathName(), ReadOperation.ENCRYPTEDLINES);
		
		List<String> idOfUsersAndPass = reader.readAllLines();
		List<User> users = new ArrayList<>();
		
		for (String userIDandPass : idOfUsersAndPass) {
			String[] userIDandPassSplited = userIDandPass.split(":");
			// get generic information
			String userID = userIDandPassSplited[0];
			String pass =  userIDandPassSplited[1];
			users.add(new User(userID, pass));
		}
		
		return users;
	}
	
	/**
	 * Get all the groups saved in disk
	 * Will get the information on: groups.txt
	 * @return a list of groups created by the information in disk
	 */
	private static List<Group> getAllGroupsFromDisk() throws IOException, Exception {
		ReadFromDisk reader = new ReadFromDisk(Configuration.getGroupPathName(), ReadOperation.ENCRYPTEDFILE);
		
		List<String> idOfGroupsAndOwners = reader.readAllLines();
		List<Group> groups = new ArrayList<>();
		
		for (String groupIDandUserID : idOfGroupsAndOwners) {
			String[] groupIDandUsersIDSplited = groupIDandUserID.split(":");
			// get generic informations
			String groupID = groupIDandUsersIDSplited[0];
			
			String ownerID = groupIDandUsersIDSplited[1];
			User owner = UserCatalog.getInstance().getUserById(ownerID);
			
			Group g = owner.createGroup(groupID);
			
			owner.addGroupToOwnededGroups(g);
			owner.addGroupToBelongedGroups(g);
			
			ReadFromDisk readerKeys = new ReadFromDisk(Configuration.getLocationKeysOfGroupPath(groupID), ReadOperation.ENCRYPTEDFILE);
			List<String> usersAndKeys = readerKeys.readAllLines();
			
			for (int i = 2; i < usersAndKeys.size(); i++) {
				String[] usersWithKeys = usersAndKeys.get(i).split(":");
				String userID = usersWithKeys[0];
				String keyFileName = usersWithKeys[1];
				g.addUserByID(UserCatalog.getInstance().getUserById(userID), keyFileName);
			}
			
			// sets keyID to correct saved value
			g.setCurrentKeyID(Integer.parseInt(usersAndKeys.get(0)));
			
			// add the group to group list
			groups.add(g);
			
			// add mutex in Operations class
			OperationsToDiskHandler.initializeGroupMutexes(g);
		}
		
		return groups;
	}
	
	/**
	 * Criar todas as mensagens enviadas num grupo (gravadas em disco)
	 * Vai buscar a informacao a 2 ficheiros:
	 *     messages.txt: contem todas as mensagens de texto
	 *     messages_users.txt: contem quais os utilizadores que faltam ler para a msg
	 * @param g o grupo que iremos fazer o load das mensagens
	 */
	
	/**
	 * Create all the messages sent in a group (saved in disk)
	 * Will get the information on multiple files:
	 *     - messages_info.txt have which users are left to read the message
	 *     - text_messages.txt have all text messages sent in the group
	 *     - <photoID> have the bytes of a photo sent in the group 
	 * @param g the group to load the messages
	 */
	private static void getAllMessagesFromDisk(Group g) throws IOException, MequieException {
		
		// all messages id and user who not read the message
		ReadFromDisk reader = new ReadFromDisk(Configuration.getMessageInfoPathName(g.getGroupID()), ReadOperation.ENCRYPTEDLINES);
		List<String> allMsgsIDandUsers = reader.readAllLines();		
		Iterator<String> it = allMsgsIDandUsers.iterator();

		// text messages content
		reader = new ReadFromDisk(Configuration.getTextMessagesPathName(g.getGroupID()), ReadOperation.ENCRYPTEDLINES);
		List<String> msgsIDandTexts = reader.readAllLines();
		Iterator<String> itTxt = msgsIDandTexts.iterator();

		// last read msg id
		String lastMsgID = null;
		
		while (it.hasNext()) {
			String[] msgIDandUsersSplited = it.next().split(":");
			
			Message m = null;
			String msgID = msgIDandUsersSplited[0];
			String flag = msgIDandUsersSplited[1];
			int keyID = Integer.parseInt(msgIDandUsersSplited[2]);
			
			// users who havent read the message yet
			List<User> usersIDs = getMsgUnseenUsers(msgIDandUsersSplited);
			
			if (flag.equals(Configuration.TXT_MSG_FLAG)) { // is a text message
				if (itTxt.hasNext()) {
					
					//get txt msg contents: sender and text
					String[] msgIDandTextSplited = itTxt.next().split(":",3);
					
					User sender = UserCatalog.getInstance().getUserById(msgIDandTextSplited[1]);
					String text = msgIDandTextSplited[2];
					
					m = new TextMessage(msgID, keyID, sender, usersIDs, text);
				}
			} else if (flag.equals(Configuration.PHOTO_MSG_FLAG)) { // is a photo message
				
				m = new PhotoMessage(msgID, keyID, usersIDs);
			}
			
			// add to group g
			if (m != null) {
				addMessageToGroup(m, g);
				lastMsgID = msgID;
			}
			
		}
		
		// sets group MsgNumberId it has loaded messages from disk
		if (lastMsgID != null) {
			int lastMsgIDNumber = Integer.parseInt(lastMsgID.replace(g.getGroupID(), ""));
			g.setMsgNumberID(lastMsgIDNumber+1);			
		}
	}
	
	/**
	 * Get a list of users who haven't read a message
	 * @param msgIDandUsersSplited a line list (of the file) with msgID and users separated
	 * @return a list of users who haven't read a message
	 */
	private static List<User> getMsgUnseenUsers(String[] msgIDandUsersSplited) {
		List<User> usersIDs = new ArrayList<>();
		
		for (int i = 3; i < msgIDandUsersSplited.length; i++) {
			User u = UserCatalog.getInstance().getUserById(msgIDandUsersSplited[i]);
			if (u != null)
				usersIDs.add(u);
		}
		
		return usersIDs;
	}
	
	/**
	 * Add a message to a group
	 * @param m the message to add to group
	 * @param g the group that will be added the message
	 */
	private static void addMessageToGroup(Message m, Group g) {
		if (m.getUsersWhoNotReadMessages().isEmpty()) {
			g.moveToHistory(m);
		} else {
			g.saveMessage(m);
		}
	}
	
	/**
	 * Do the load of all system to memory
	 */
	public static void load() throws IOException, Exception {		
		// users load
		List<User> users = getAllUsersFromDisk();
		for (User u : users) {
			UserCatalog.getInstance().addUser(u);
		}
		
		// groups load
		List<Group> groups = getAllGroupsFromDisk();
		for (Group g : groups) {
			GroupCatalog.getInstance().addGroup(g);
		}
		
		// messages load
		for (Group g : groups) {
			getAllMessagesFromDisk(g);
		}
	}

}
