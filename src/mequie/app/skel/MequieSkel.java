package mequie.app.skel;

import java.nio.charset.StandardCharsets;
import java.util.List;

import mequie.app.Mequie;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;
import mequie.app.facade.exceptions.ErrorInsufficientArgumentsException;
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
	
	private Session currentSession;
	
	public MequieSkel(Session s) {
		this.currentSession = s;
	}
	
	public void invoke(NetworkMessageRequest msg) {
		List<String> args = msg.getArguments();

		switch (msg.getOp()) {
		case CREATE_GROUP:
			createGroup(currentSession, args);
			break;
		case ADD_USER_TO_GROUP:
			addUserToGroup(currentSession, args);
			break;
		case REMOVE_USER_FROM_GROUP:
			removeUserFromGroup(currentSession, args);
			break;
		case GET_GROUP_INFO:
			getGroupInfo(currentSession, args);
			break;
		case GET_USER_INFO:
			getUserInfo(currentSession);
			break;
		case SEND_TEXT_MESSAGE:
			sendMsg(currentSession, args);
			break;
		case SEND_PHOTO_MESSAGE:
			sendPhoto(currentSession, args);
			break;
		case COLLECT_NOT_VIEWED_MESSAGES_OF_GROUP:
			collectMsgs(currentSession, args);
			break;
		case MESSAGE_HISTORY_OF_GROUP:
			history(currentSession, args);
			break;
		default:
			break;
		}
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
	
	private void createGroup(Session s, List<String> args) {
		CreateGroupHandler cgh = system.getCreateGroupHandler(s);
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			cgh.makeGrupByID(g);
			cgh.groupAssociation();
			cgh.save();
			
		} catch (ErrorCreatingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorSavingInDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorInsufficientArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addUserToGroup(Session s, List<String> args) {
		AddUserToGroupHandler augh = system.getAddUserToGroupHandler(s);
		
		try {
			String u = args.get(0);
			if (u == null || u.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			augh.getUserByID(u);
			
			String g = args.get(1);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			augh.getGroupByID(g);
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
	
	private void removeUserFromGroup(Session s, List<String> args) {
		RemoveUserOfGroupHandler rugh = system.getRemoveUserOfGroupHandler(s);
		
		try {
			String u = args.get(0);
			if (u == null || u.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			rugh.indicateUserID(u);
			
			String g = args.get(1);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			rugh.indicateGroupID(g);
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
	
	private void getGroupInfo(Session s, List<String> args) {
		GetGroupInfoHandler gih = system.getGetGroupInfoHandler(s);
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			gih.indicateGroupID(g);
			String info = gih.getInfo();
			
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorInsufficientArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getUserInfo(Session s) {
		GetUserInfoHandler uih = system.getGetUserInfoHandler(s);
		
		uih.getUserInfo();
	}
	
	private void sendMsg(Session s, List<String> args) {
		SendTextMessageHandler stmh = system.getSendTextMessageHandler(s);
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			stmh.getGroupByID(g);
			
			String m = args.get(1);	
			if (m == null || m.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			stmh.createMessage(m);
			stmh.sendMessageToGroup();
			stmh.save();
			
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorSavingInDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorInsufficientArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendPhoto(Session s, List<String> args) {
		SendPhotoMessageHandler spmh = system.getSendPhotoMessageHandler(s);
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			spmh.getGroupByID(g);
			
			byte[] photo = args.get(1).getBytes(StandardCharsets.UTF_8); // TODO: Sera a melhor maneira?! 1024 bytes de cada vez ?!
			if (photo.length < 1)
				throw new ErrorInsufficientArgumentsException();
			
			spmh.createMessage(photo);
			spmh.sendMessageToGroup();
			spmh.save();
			
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorSavingInDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorInsufficientArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void collectMsgs(Session s, List<String> args) {
		CollectMessagesHandler cmh = system.getCollectMessagesHandler(s);
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			cmh.indicateGroupID(g);
			List<String> msgs = cmh.getNotSeenMessages();
			
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorInsufficientArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void history(Session s, List<String> args) {
		MessageHistoryOfGroupHandler mhgh = system.getMessageHistoryOfGroupHandler(s);
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			mhgh.indicateGroupID(g);
			mhgh.getHistory(); // TODO
			
		} catch (NotExistingGroupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserNotHavePermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorInsufficientArgumentsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
