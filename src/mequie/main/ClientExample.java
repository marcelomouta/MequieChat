package mequie.main;

import java.io.FileNotFoundException;
import java.util.List;

import mequie.app.Mequie;
import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CreateUserHandler;
import mequie.app.facade.handlers.LoadingFromDiskHandler;

public class ClientExample {
	
	public static void main(String[] args) {
		
		Mequie m = new Mequie();
		
		LoadingFromDiskHandler loader = m.getLoaderHandler();
		
		try {
			List<User> users = LoadingFromDiskHandler.getAllUsersFromDisk();
			
			for (User u : users) {
				UserCatalog.getInstance().addUser(u);
			}
			
			List<Group> groups = LoadingFromDiskHandler.getAllGroupsFromDisk();
			
			for (Group g : groups) {
				GroupCatalog.getInstance().addGroup(g);
			}
			
			CreateUserHandler cuh = m.getCreateUserHandler();
			
			System.out.println(UserCatalog.getInstance().getAllUsers().toString());
			System.out.println(GroupCatalog.getInstance().getAllGroups().toString());
			
			String idU = "marcelo";
			String idG = "10001";
			Session s = new Session(idU, "123123123");
			
			AddUserToGroupHandler augh = m.getAddUserToGroupHandler(s);
			
			augh.getUserByID(idU);
			augh.getGroupByID(idG);
			augh.addNewUserToGroup();
			augh.save();
			
			System.out.println(GroupCatalog.getInstance().getAllGroups().toString());
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
