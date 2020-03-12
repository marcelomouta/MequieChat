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
import mequie.app.facade.exceptions.MequieException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;
import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CollectMessagesHandler;
import mequie.app.facade.handlers.CreateGroupHandler;
import mequie.app.facade.handlers.CreateUserHandler;
import mequie.app.facade.handlers.GetGroupInfoHandler;
import mequie.app.facade.handlers.GetUserInfoHandler;
import mequie.app.facade.handlers.MessageHistoryOfGroupHandler;
import mequie.app.facade.handlers.RemoveUserOfGroupHandler;
import mequie.app.facade.handlers.SendPhotoMessageHandler;
import mequie.app.facade.handlers.SendTextMessageHandler;
import mequie.app.network.NetworkMessage;
import mequie.app.network.NetworkMessageError;
import mequie.app.network.NetworkMessageRequest;
import mequie.app.network.NetworkMessageResponse;

public class MequieSkel {
	
	private Mequie system = new Mequie();
	
	private Session currentSession;
	
	public MequieSkel(Session s) {
		this.currentSession = s;
	}
	
	public NetworkMessage invoke(NetworkMessageRequest msg) {
		NetworkMessage response;

		switch (msg.getOp()) {
		case CREATE_GROUP:
			response = createGroup(msg);
			break;
		case ADD_USER_TO_GROUP:
			response = addUserToGroup(msg);
			break;
		case REMOVE_USER_FROM_GROUP:
			response = removeUserFromGroup(msg);
			break;
		case GET_GROUP_INFO:
			response = getGroupInfo(msg);
			break;
		case GET_USER_INFO:
			response = getUserInfo(msg);
			break;
		case SEND_TEXT_MESSAGE:
			response = sendMsg(msg);
			break;
		case SEND_PHOTO_MESSAGE:
			response = sendPhoto(msg);
			break;
		case COLLECT_NOT_VIEWED_MESSAGES_OF_GROUP:
			response = collectMsgs(msg);
			break;
		case MESSAGE_HISTORY_OF_GROUP:
			response = history(msg);
			break;
		default:
			return null;
		}
		
		return response;
	}
	
	private void createNewUser(String u, String p) {
		CreateUserHandler cuh = system.getCreateUserHandler();
		
//		try {
			cuh.makeUser(u, p);
			cuh.userAssociation();
			//cuh.save();
			
//		} catch (ErrorSavingInDiskException e) {
//			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
//		}
	}
	
	private NetworkMessage createGroup(NetworkMessageRequest msg) {
		CreateGroupHandler cgh = system.getCreateGroupHandler(currentSession);
		
		// list of arguments
		List<String> args = msg.getArguments();
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			cgh.makeGrupByID(g);
			cgh.groupAssociation();
			//cgh.save();
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
			
		} catch (ErrorCreatingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorCreatingGroupException());
//		} catch (ErrorSavingInDiskException e) {
//			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage addUserToGroup(NetworkMessageRequest msg) {
		AddUserToGroupHandler augh = system.getAddUserToGroupHandler(currentSession);
		
		// list of arguments
		List<String> args = msg.getArguments();
		
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
			//augh.save();
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
			
		} catch (NotExistingUserException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingUserException());
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (ErrorAddingUserToGroupException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorAddingUserToGroupException());
		} catch (UserNotHavePermissionException e) {
			return new NetworkMessageError(msg.getOp(), new UserNotHavePermissionException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		} catch (Exception e) {
			return new NetworkMessageError(msg.getOp(), new MequieException("ERROR not defined"));
		}
	}
	
	private NetworkMessage removeUserFromGroup(NetworkMessageRequest msg) {
		RemoveUserOfGroupHandler rugh = system.getRemoveUserOfGroupHandler(currentSession);
		
		// list of arguments
		List<String> args = msg.getArguments();
		
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
			//rugh.save();
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
			
		} catch (NotExistingUserException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingUserException());
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (ErrorRemovingUserOfGroupException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorRemovingUserOfGroupException());
		} catch (UserNotHavePermissionException e) {
			return new NetworkMessageError(msg.getOp(), new UserNotHavePermissionException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		} catch (Exception e) {
			return new NetworkMessageError(msg.getOp(), new MequieException("ERROR not defined"));
		}
	}
	
	private NetworkMessage getGroupInfo(NetworkMessageRequest msg) {
		GetGroupInfoHandler gih = system.getGetGroupInfoHandler(currentSession);
		
		// list of arguments
		List<String> args = msg.getArguments();
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			gih.indicateGroupID(g);
			String info = gih.getInfo();
			
			return new NetworkMessageResponse(msg.getOp(),info);
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage getUserInfo(NetworkMessageRequest msg) {
		GetUserInfoHandler uih = system.getGetUserInfoHandler(currentSession);
		
		uih.getUserInfo();
		
		return new NetworkMessageResponse(msg.getOp(), "TODO");
	}
	
	private NetworkMessage sendMsg(NetworkMessageRequest msg) {
		SendTextMessageHandler stmh = system.getSendTextMessageHandler(currentSession);
		
		// list of arguments
		List<String> args = msg.getArguments();
		
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
			//stmh.save();
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
//		} catch (ErrorSavingInDiskException e) {
//			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage sendPhoto(NetworkMessageRequest msg) {
		SendPhotoMessageHandler spmh = system.getSendPhotoMessageHandler(currentSession);
		
		// list of arguments
		List<String> args = msg.getArguments();
		
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
			//spmh.save();
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
//		} catch (ErrorSavingInDiskException e) {
//			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage collectMsgs(NetworkMessageRequest msg) {
		CollectMessagesHandler cmh = system.getCollectMessagesHandler(currentSession);
		
		// list of arguments
		List<String> args = msg.getArguments();
		
		try {
			String g = args.get(0); //TODO verificacao ERRADA -> da indexOutOfBoundException pois nem um elemento tem e acedemos a 0
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			cmh.indicateGroupID(g);
			List<String> msgs = cmh.getNotSeenMessages();
			
			return null; //TODO
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage history(NetworkMessageRequest msg) {
		MessageHistoryOfGroupHandler mhgh = system.getMessageHistoryOfGroupHandler(currentSession);
		
		// list of arguments
		List<String> args = msg.getArguments();
		
		try {
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			mhgh.indicateGroupID(g);
			mhgh.getHistory(); // TODO
			
			return null; //TODO
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (UserNotHavePermissionException e) {
			return new NetworkMessageError(msg.getOp(), new UserNotHavePermissionException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}

}
