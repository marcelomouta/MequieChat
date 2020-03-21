package mequie.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import mequie.app.Mequie;
import mequie.app.domain.Group;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CreateGroupHandler;
import mequie.app.facade.handlers.LoadingFromDiskHandler;
import mequie.app.facade.handlers.RemoveUserOfGroupHandler;
import mequie.app.facade.handlers.OperationsToDiskHandler;
import mequieclient.app.facade.Session;

public class ClientExample {
	
	public static void main(String[] args) {
		
		Mequie m = new Mequie();
		
		try {
			// LOAD
			LoadingFromDiskHandler.load();
			
			System.out.println(UserCatalog.getInstance().getAllUsers().toString());
			System.out.println(GroupCatalog.getInstance().getAllGroups().toString());
			
			Session s = new Session("bfreitas", "123123123");
			
			String idU = "marcelo";
			String idG = "10001";
			
			// ADD
			
			AddUserToGroupHandler augh = m.getAddUserToGroupHandler(s);
			
			augh.getUserByID(idU);
			augh.getGroupByID(idG);
			augh.addNewUserToGroup();
			//augh.save();
			
			System.out.println(GroupCatalog.getInstance().getAllGroups().toString());
			
			// REMOVE
			
			RemoveUserOfGroupHandler rugh = m.getRemoveUserOfGroupHandler(s);
			rugh.indicateUserID(idU);
			rugh.indicateGroupID(idG);
			rugh.removeUserFromGroup();
			//rugh.save();
			
			System.out.println(GroupCatalog.getInstance().getAllGroups().toString());
			
			// CREATE GROUP
			
			CreateGroupHandler cgh = m.getCreateGroupHandler(s);
			cgh.makeGrupByID("10003");
			cgh.groupAssociation();
			//cgh.save();
			
			System.out.println(GroupCatalog.getInstance().getAllGroups().toString());
			
			// CREATE USER
			
//			CreateUserHandler cuh = m.getCreateUserHandler();
//			cuh.makeUser("admin", "123123123");
			//cuh.save();
			
			System.out.println(GroupCatalog.getInstance().getAllGroups().toString());
			
			File f = new File("pikachu.jpg");
//			byte[] file = msg.getArguments().get(0).getBytes(StandardCharsets.UTF_8);
//			SaveToDiskHandler.savePhotoMessageInDisk("pikachu", data, new Group("g1", new User("user01", "passwd")));
			
			//OperationsToDiskHandler.saveTextMessageInDisk(new TextMessage("123", new User("user01", "passwd"), new ArrayList(), idG), "texto teste");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
