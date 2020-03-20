package mequie.app.facade.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
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
		ReadFromDisk reader = new ReadFromDisk("Data/passwd.txt");
		
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
		ReadFromDisk reader = new ReadFromDisk("Data/group.txt");
		
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
	 * Buscar todas as mensagens enviadas em todos os grupos (gravadas em disco)
	 * Vai buscar a informacao a 2 ficheiros:
	 *     messages.txt: contem todas as mensagens de texto
	 *     messages_users.txt: contem quais os utilizadores que faltam ler para a msg
	 * @return uma lista de mensagens criadas a partir dos dados do disco
	 */
	private static List<Message> getAllMessagesFromDisk(Group g) throws IOException {
		ReadFromDisk reader = new ReadFromDisk("Data/group" + g.getGoupID() + "_msgs.txt");
		
		List<String> msgsIDandTexts = reader.readAllLines();
		List<Message> msgs = new ArrayList<>();
		
		for (String msgIDandText : msgsIDandTexts) {
			String[] msgIDandTextSplited = msgIDandText.split(":");
			String msgID = msgIDandTextSplited[0];
			String text = msgIDandTextSplited[1];			
//			msgs.add(new Message(msgID, text));
		}
		
		return msgs;
	}
	
	/**
	 * Faz o load para memoria de todo o sistema em disco
	 */
	public static void load() throws IOException, Exception {
		List<User> users = getAllUsersFromDisk();
		for (User u : users) {
			UserCatalog.getInstance().addUser(u);
		}
		
		List<Group> groups = getAllGroupsFromDisk();
		
		for (Group g : groups) {
			GroupCatalog.getInstance().addGroup(g);
		}
		
		// load das mensagens
	}

}
