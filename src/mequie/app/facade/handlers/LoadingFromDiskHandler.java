package mequie.app.facade.handlers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.utils.ReadFromDisk;

public class LoadingFromDiskHandler {
	
	private static LoadingFromDiskHandler INSTANCE;
	
	private LoadingFromDiskHandler() {}
	
	public static LoadingFromDiskHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LoadingFromDiskHandler();
		}
		return INSTANCE;
	}
	
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
	
	private static List<Group> getAllGroupsFromDisk() throws IOException, Exception {
		ReadFromDisk reader = new ReadFromDisk("Data/group.txt");
		
		List<String> idOfGroupsAndOwners = reader.readAllLines();
		List<Group> groups = new ArrayList<>();
		
		for (String groupIDandUserID : idOfGroupsAndOwners) {
			String[] groupIDandUsersIDSplited = groupIDandUserID.split(":");
			String groupID = groupIDandUsersIDSplited[0];
			String ownerID = groupIDandUsersIDSplited[1];
			Group g = new Group(groupID, UserCatalog.getInstance().getUserById(ownerID));
			
			for (int i = 2; i < groupIDandUsersIDSplited.length; i++) {
				String userID = groupIDandUsersIDSplited[i];
				g.addUserByID(UserCatalog.getInstance().getUserById(userID));
				groups.add(g);
			}
		}
		
		return groups;
	}
	
	private static List<Message> getAllMessagesFromDisk(Group g) throws IOException {
		ReadFromDisk reader = new ReadFromDisk("Data/group" + g.getGoupID() + "_msgs.txt");
		
		List<String> msgsIDandTexts = reader.readAllLines();
		List<Message> msgs = new ArrayList<>();
		
		for (String msgIDandText : msgsIDandTexts) {
			String[] msgIDandTextSplited = msgIDandText.split(":");
			String msgID = msgIDandTextSplited[0];
			String text = msgIDandTextSplited[1];			
			msgs.add(new Message(msgID, text));
		}
		
		return msgs;
	}
	
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
	
	
	
//	public Optional<Session> authenticate(String username, String password) {
//		Session maybeSession = new Session(username, password);
//		if (maybeSession.getUser() == null) return Optional.empty();
//		return Optional.of(maybeSession);
//	}

}
