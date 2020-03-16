package mequie.app.skel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mequie.app.Mequie;
import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CollectMessagesHandler;
import mequie.app.facade.handlers.CreateGroupHandler;
import mequie.app.facade.handlers.GetGroupInfoHandler;
import mequie.app.facade.handlers.GetUserFromSessionHandler;
import mequie.app.facade.handlers.GetUserInfoHandler;
import mequie.app.facade.handlers.MessageHistoryOfGroupHandler;
import mequie.app.facade.handlers.RemoveUserOfGroupHandler;
import mequie.app.facade.handlers.SendPhotoMessageHandler;
import mequie.app.facade.handlers.SendTextMessageHandler;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.AuthenticationFailedException;
import mequieclient.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequieclient.app.facade.exceptions.ErrorCreatingGroupException;
import mequieclient.app.facade.exceptions.ErrorInsufficientArgumentsException;
import mequieclient.app.facade.exceptions.ErrorRemovingUserOfGroupException;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;
import mequieclient.app.facade.exceptions.MequieException;
import mequieclient.app.facade.exceptions.NotExistingGroupException;
import mequieclient.app.facade.exceptions.NotExistingUserException;
import mequieclient.app.facade.exceptions.UserNotHavePermissionException;
import mequieclient.app.network.NetworkMessage;
import mequieclient.app.network.NetworkMessageError;
import mequieclient.app.network.NetworkMessageRequest;
import mequieclient.app.network.NetworkMessageResponse;

public class MequieSkel {
	
	private Mequie system = new Mequie();
	
	private Session currentSession;
	
	public MequieSkel(Session s) {
		this.currentSession = s;
	}
	
	public void autentication() throws AuthenticationFailedException {
		GetUserFromSessionHandler.authenticateSession(currentSession);
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

	private NetworkMessage createGroup(NetworkMessageRequest msg) {
		try {
			CreateGroupHandler cgh = system.getCreateGroupHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.isEmpty())
				throw new ErrorInsufficientArgumentsException();
			
			String g = args.get(0);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			cgh.makeGrupByID(g);
			cgh.groupAssociation();
			cgh.save();
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
		
		} catch (ErrorCreatingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorCreatingGroupException());
		} catch (ErrorSavingInDiskException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage addUserToGroup(NetworkMessageRequest msg) {		
		try {
			AddUserToGroupHandler augh = system.getAddUserToGroupHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.size() < 2)
				throw new ErrorInsufficientArgumentsException();
			
			String u = args.get(0);
			if (u.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			augh.getUserByID(u);
			
			String g = args.get(1);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			augh.getGroupByID(g);
			augh.addNewUserToGroup();
			augh.save();
			
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
		} catch (ErrorSavingInDiskException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
		} catch (Exception e) {
			return new NetworkMessageError(msg.getOp(), new MequieException("ERROR not defined"));
		}
	}
	
	private NetworkMessage removeUserFromGroup(NetworkMessageRequest msg) {		
		try {
			RemoveUserOfGroupHandler rugh = system.getRemoveUserOfGroupHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.size() < 2)
				throw new ErrorInsufficientArgumentsException();
			
			String u = args.get(0);
			if (u.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			rugh.indicateUserID(u);
			
			String g = args.get(1);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			rugh.indicateGroupID(g);
			rugh.removeUserFromGroup();
			rugh.save();
			
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
		} catch (ErrorSavingInDiskException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
		} catch (Exception e) {
			return new NetworkMessageError(msg.getOp(), new MequieException("ERROR not defined"));
		}
	}
	
	private NetworkMessage getGroupInfo(NetworkMessageRequest msg) {		
		try {
			GetGroupInfoHandler gih = system.getGetGroupInfoHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.isEmpty())
				throw new ErrorInsufficientArgumentsException();
			
			String g = args.get(0);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			gih.indicateGroupID(g);
			List<String> info = gih.getInfo();
			
			return new NetworkMessageResponse(msg.getOp(),"OK", new ArrayList<>(info));
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage getUserInfo(NetworkMessageRequest msg) {
		
		GetUserInfoHandler uih = system.getGetUserInfoHandler(currentSession);
			
		List<List<String>> groups = uih.getUserInfo();
			
		return new NetworkMessageResponse(msg.getOp(), "OK", new ArrayList<String>(groups.get(0)), new ArrayList<String>(groups.get(1)));
		
	}
	
	private NetworkMessage sendMsg(NetworkMessageRequest msg) {		
		try {
			SendTextMessageHandler stmh = system.getSendTextMessageHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.size() < 2)
				throw new ErrorInsufficientArgumentsException();
			
			String g = args.get(0);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			stmh.getGroupByID(g);
			
			String m = args.get(1);	
			if (m.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			stmh.createMessage(m);
			stmh.sendMessageToGroup();
			stmh.save();
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
		
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (ErrorSavingInDiskException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage sendPhoto(NetworkMessageRequest msg) {		
		try {
			SendPhotoMessageHandler spmh = system.getSendPhotoMessageHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.size() < 1)
				throw new ErrorInsufficientArgumentsException();
			
			String g = args.get(0);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			spmh.getGroupByID(g);
			
			byte[] photo = msg.getPhoto();
			if (photo == null || photo.length < 1)
				throw new ErrorInsufficientArgumentsException();
			
			spmh.createMessage();
			spmh.sendMessageToGroup();
			spmh.save(photo);
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (ErrorSavingInDiskException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorSavingInDiskException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage collectMsgs(NetworkMessageRequest msg) {		
		try {
			CollectMessagesHandler cmh = system.getCollectMessagesHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.isEmpty())
				throw new ErrorInsufficientArgumentsException();
			
			String g = args.get(0);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			cmh.indicateGroupID(g);
			List<List<? extends Object>> msgs = cmh.getNotSeenMessages();
			ArrayList<String> info1 = new ArrayList<>((Collection<? extends String>) msgs.get(0));
			ArrayList<byte[]> info2 = new ArrayList<>((Collection<? extends byte[]>) msgs.get(1));
			
			return new NetworkMessageResponse(msg.getOp(), "OK", info1, info2);
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (UserNotHavePermissionException e) {
			return new NetworkMessageError(msg.getOp(), new UserNotHavePermissionException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}
	
	private NetworkMessage history(NetworkMessageRequest msg) {
		try {
			MessageHistoryOfGroupHandler mhgh = system.getMessageHistoryOfGroupHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.isEmpty())
				throw new ErrorInsufficientArgumentsException();
			
			String g = args.get(0);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			mhgh.indicateGroupID(g);
			List<String> msgs = mhgh.getHistory();
			
			return new NetworkMessageResponse(msg.getOp(), "OK", new ArrayList<String>(msgs));
			
		} catch (NotExistingGroupException e) {
			return new NetworkMessageError(msg.getOp(), new NotExistingGroupException());
		} catch (UserNotHavePermissionException e) {
			return new NetworkMessageError(msg.getOp(), new UserNotHavePermissionException());
		} catch (ErrorInsufficientArgumentsException e) {
			return new NetworkMessageError(msg.getOp(), new ErrorInsufficientArgumentsException());
		}
	}

}
