package mequie.app.facade.handlers;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
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
	
	public static List<User> getAllUsersFromDisk() throws FileNotFoundException {
		ReadFromDisk reader = new ReadFromDisk("Data/passwd.txt");
		
		List<String> idOfUsersAndPass = reader.readAllLines();
		List<User> users = new ArrayList<>();
		
		for (String userIDandPass : idOfUsersAndPass) {
			String[] userIDandPassSplited = userIDandPass.split(":");
			String userID = userIDandPassSplited[1];
			String pass =  userIDandPassSplited[2];
			users.add(new User(userID, pass));
		}
		
		return users;
	}
	
	public List<Group> getAllGroupsFromDisk() throws FileNotFoundException, Exception {
		ReadFromDisk reader = new ReadFromDisk("Data/group.txt");
		
		List<String> idOfGroupsAndOwners = reader.readAllLines();
		List<Group> groups = new ArrayList<>();
		
		for (String groupIDandUserID : idOfGroupsAndOwners) {
			String[] groupIDandUsersIDSplited = groupIDandUserID.split(":");
			String groupID = groupIDandUsersIDSplited[1];
			String ownerID = groupIDandUsersIDSplited[2];
			Group g = new Group(groupID, UserCatalog.getInstance().getUserById(ownerID));
			
			for (int i = 3; i < groupIDandUsersIDSplited.length; i++) {
				String userID = groupIDandUsersIDSplited[i];
				g.addUserByID(UserCatalog.getInstance().getUserById(userID));
			}
			
			groups.add(g);
		}
		
		return groups;
	}
	
	public List<Message> getAllMessagesFromDisk(Group g) throws FileNotFoundException {
		ReadFromDisk reader = new ReadFromDisk("Data/group" + g.getGoupID() + "_msgs.txt");
		
		List<String> msgsIDandTexts = reader.readAllLines();
		List<Message> msgs = new ArrayList<>();
		
		for (String msgIDandText : msgsIDandTexts) {
			String[] msgIDandTextSplited = msgIDandText.split(":");
			String msgID = msgIDandTextSplited[1];
			String text = msgIDandTextSplited[2];			
			msgs.add(new Message(msgID, text));
		}
		
		return msgs;
	}
	
	
	
//	public Optional<Session> authenticate(String username, String password) {
//		Session maybeSession = new Session(username, password);
//		if (maybeSession.getUser() == null) return Optional.empty();
//		return Optional.of(maybeSession);
//	}

}
