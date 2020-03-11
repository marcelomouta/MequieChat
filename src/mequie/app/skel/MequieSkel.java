package mequie.app.skel;

import java.util.List;

import mequie.app.Mequie;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;
import mequie.app.facade.exceptions.ErrorRemovingUserOfGroupException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;
import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CollectMessagesHandler;
import mequie.app.facade.handlers.CreateGroupHandler;
import mequie.app.facade.handlers.CreateUserHandler;
import mequie.app.facade.handlers.GetGroupInfoHandler;
import mequie.app.facade.handlers.GetUserInfoHandler;
import mequie.app.facade.handlers.LoadingFromDiskHandler;
import mequie.app.facade.handlers.MessageHistoryOfGroupHandler;
import mequie.app.facade.handlers.RemoveUserOfGroupHandler;
import mequie.app.facade.handlers.SendPhotoMessageHandler;
import mequie.app.facade.handlers.SendTextMessageHandler;
import mequie.app.network.NetworkMessageRequest;

public class MequieSkel {
	
	private Mequie system = new Mequie();
	
	public MequieSkel() {
		try {
			LoadingFromDiskHandler.load();
		} catch (Exception e) {
			System.out.println("Error loading the system");
		}
	}
	
	public void invoke(NetworkMessageRequest msg) {
		// ver as operacoes e chamar as funcoes apropriadas
	}
	
	private void createNewUser(String u, String p) {
		CreateUserHandler cuh = system.getCreateUserHandler();
		
		try {
			cuh.makeUser(u, p);
			cuh.userAssociation();
			cuh.save();
		} catch (ErrorSavingInDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createGroup(Session s) {
		CreateGroupHandler cgh = system.getCreateGroupHandler(s);
		
		cgh.makeGrupByID("TODO");
		try {
			cgh.groupAssociation();
			cgh.save();
		} catch (ErrorCreatingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorSavingInDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addUserToGroup(Session s) {
		AddUserToGroupHandler augh = system.getAddUserToGroupHandler(s);
		
		try {
			augh.getUserByID("TODO");
			augh.getGroupByID("TODO");
			augh.addNewUserToGroup();
			augh.save();
		} catch (NotExistingUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorAddingUserToGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void removeUserFromGroup(Session s) {
		RemoveUserOfGroupHandler rugh = system.getRemoveUserOfGroupHandler(s);
		
		try {
			rugh.indicateUserID("TODO");
			rugh.indicateGroupID("TODO");
			rugh.removeUserFromGroup();
			rugh.save();
		} catch (NotExistingUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorRemovingUserOfGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getGroupInfo(Session s) {
		GetGroupInfoHandler gih = system.getGetGroupInfoHandler(s);
		
		try {
			gih.indicateGroupID("TODO");
			String info = gih.getInfo();
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getUserInfo(Session s) {
		GetUserInfoHandler uih = system.getGetUserInfoHandler(s);
		
		uih.getUserInfo();
	}
	
	private void sendMsg(Session s) {
		SendTextMessageHandler stmh = system.getSendTextMessageHandler(s);
		
		try {
			stmh.getGroupByID("TODO");
			stmh.createMessage("TODO");
			stmh.sendMessageToGroup();
			stmh.save();
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorSavingInDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendPhoto(Session s) {
		SendPhotoMessageHandler spmh = system.getSendPhotoMessageHandler(s);
		
		try {
			spmh.getGroupByID("TODO");
			spmh.createMessage(new byte[1]); // TODO
			spmh.sendMessageToGroup();
			spmh.save();
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorSavingInDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void collectMsgs(Session s) {
		CollectMessagesHandler cmh = system.getCollectMessagesHandler(s);
		
		try {
			cmh.indicateGroupID("TODO");
			List<String> msgs = cmh.getNotSeenMessages();
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void history(Session s) {
		MessageHistoryOfGroupHandler mhgh = system.getMessageHistoryOfGroupHandler(s);
		
		try {
			mhgh.indicateGroupID("TODO");
			mhgh.getHistory(); // TODO
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserNotHavePermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
