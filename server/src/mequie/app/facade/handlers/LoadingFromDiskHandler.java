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
import mequie.utils.Configuration;
import mequie.utils.ReadFromDisk;

/**
 * Classe para fazer load dos dados que estao em disco
 */

public class LoadingFromDiskHandler {
	
	private LoadingFromDiskHandler() {}
	/***
	 * Buscar todos os utilizadores (gravados em disco)
	 * Vai buscar a informacao a: passwd.txt
	 * @return Lista de utilizadores criados a partir dos dados do disco
	 */
	private static List<User> getAllUsersFromDisk() throws IOException {
		ReadFromDisk reader = new ReadFromDisk(Configuration.getPasswordPathName());
		
		List<String> idOfUsersAndPass = reader.readAllLines();
		List<User> users = new ArrayList<>();
		
		for (String userIDandPass : idOfUsersAndPass) {
			String[] userIDandPassSplited = userIDandPass.split(":");
			String userID = userIDandPassSplited[0];
			String pass =  userIDandPassSplited[1];
			users.add(new User(userID, pass));
		}
		
		return users;
	}
	
	/**
	 * Buscar todos os grupos (gravados em disco)
	 * Vai buscar a informacao a: groups.txt
	 * @return Lista de grupos criados a partir dos dados do disco
	 */
	private static List<Group> getAllGroupsFromDisk() throws IOException, Exception {
		ReadFromDisk reader = new ReadFromDisk(Configuration.getGroupPathName());
		
		List<String> idOfGroupsAndOwners = reader.readAllLines();
		List<Group> groups = new ArrayList<>();
		
		for (String groupIDandUserID : idOfGroupsAndOwners) {
			String[] groupIDandUsersIDSplited = groupIDandUserID.split(":");
			String groupID = groupIDandUsersIDSplited[0];
			String ownerID = groupIDandUsersIDSplited[1];
			User owner = UserCatalog.getInstance().getUserById(ownerID);
			Group g = owner.createGroup(groupID);

			
			for (int i = 2; i < groupIDandUsersIDSplited.length; i++) {
				String userID = groupIDandUsersIDSplited[i];
				g.addUserByID(UserCatalog.getInstance().getUserById(userID));
			}
			groups.add(g);
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
	private static void getAllMessagesFromDisk(Group g) throws IOException {
		
		// all messages id and user who not read the message
		ReadFromDisk reader = new ReadFromDisk(Configuration.getMessageInfoPathName(g.getGoupID()));
		List<String> allMsgsIDandUsers = reader.readAllLines();		
		Iterator<String> it = allMsgsIDandUsers.iterator();

		// text messages content
		reader = new ReadFromDisk(Configuration.getTextMessagesPathName(g.getGoupID()));
		List<String> msgsIDandTexts = reader.readAllLines();
		Iterator<String> itTxt = msgsIDandTexts.iterator();

		String lastMsgID = null;
		
		while (it.hasNext()) {
			String[] msgIDandUsersSplited = it.next().split(":");
			
			Message m = null;
			String msgID = msgIDandUsersSplited[0];
			String flag = msgIDandUsersSplited[1];
			
			// users who havent read the message yet
			List<User> usersIDs = getMsgUnseenUsers(msgIDandUsersSplited);
			
			if (flag.equals(Configuration.TXT_MSG_FLAG)) { // is a text message
				if (itTxt.hasNext()) {
					
					//get txt msg contents: sender and text
					String[] msgIDandTextSplited = itTxt.next().split(":",3);
					
					User sender = UserCatalog.getInstance().getUserById(msgIDandTextSplited[1]);
					String text = msgIDandTextSplited[2];
					
					
					m = new TextMessage(msgID, sender, usersIDs, text);
				}
			} else if (flag.equals(Configuration.PHOTO_MSG_FLAG)) { // is a photo message
				
				m = new PhotoMessage(msgID, usersIDs);
			}
			
			// add to group g
			if (m != null) {
				addMessageToGroup(m, g);
				lastMsgID = msgID;
			}
			
		}
		
		int lastMsgIDNumber = Integer.parseInt(lastMsgID.replace(g.getGoupID(), ""));
		g.setMsgNumberID(lastMsgIDNumber);
	}
	
	
	private static List<User> getMsgUnseenUsers(String[] msgIDandUsersSplited) {
		List<User> usersIDs = new ArrayList<>();
		for (int i = 2; i < msgIDandUsersSplited.length; i++) {
			User u = UserCatalog.getInstance().getUserById(msgIDandUsersSplited[i]);
			if (u != null)
				usersIDs.add(u);
		}
		return usersIDs;
	}
	
	/**
	 * Adicionar uma mensagem a um grupo
	 * @param m a mensagem a adicionar ao grupo
	 * @param g o grupo ao qual iremos adicionar a mensagem
	 */
	private static void addMessageToGroup(Message m, Group g) {
		if (m.getUsersWhoNotReadMessages().isEmpty()) {
			g.moveToHistory(m);
		} else {
			g.saveMessage(m);
		}
	}
	
	/**
	 * Faz o load para memoria de todo o sistema em disco
	 */
	public static void load() throws IOException, Exception {
		// load dos users
		List<User> users = getAllUsersFromDisk();
		for (User u : users) {
			UserCatalog.getInstance().addUser(u);
		}
		
		// load dos grupos
		List<Group> groups = getAllGroupsFromDisk();
		for (Group g : groups) {
			GroupCatalog.getInstance().addGroup(g);
		}
		
		// load das mensagens
		for (Group g : groups) {
			getAllMessagesFromDisk(g);
		}
	}

}
