package mequie.main;

import java.io.FileNotFoundException;
import java.util.List;

import mequie.app.Mequie;
import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.handlers.CreateUserHandler;
import mequie.app.facade.handlers.LoadingFromDiskHandler;

public class ClientExample {
	
	public static void main(String[] args) {
		
		Mequie m = new Mequie();
		
		LoadingFromDiskHandler loader = m.getLoaderHandler();
		
		try {
			List<User> users = LoadingFromDiskHandler.getAllUsersFromDisk();
			List<Group> groups = LoadingFromDiskHandler.getAllGroupsFromDisk();
			
			CreateUserHandler cuh = m.getCreateUserHandler();
			
			for (User u : users) {
				UserCatalog.getInstance().addUser(u);
			}
			
			for (Group g : groups) {
				GroupCatalog.getInstance().addGroup(g);
			}
			
			System.out.println(UserCatalog.getInstance().getAllUsers().toString());
			System.out.println(GroupCatalog.getInstance().getAllGroups().toString());
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
