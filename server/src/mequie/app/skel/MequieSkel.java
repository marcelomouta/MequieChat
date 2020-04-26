package mequie.app.skel;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mequie.app.Mequie;
import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CollectMessagesHandler;
import mequie.app.facade.handlers.CreateGroupHandler;
import mequie.app.facade.handlers.GetGroupInfoHandler;
import mequie.app.facade.handlers.GetLastKeyOfGroupHandler;
import mequie.app.facade.handlers.GetUserFromSessionHandler;
import mequie.app.facade.handlers.GetUserInfoHandler;
import mequie.app.facade.handlers.MessageHistoryOfGroupHandler;
import mequie.app.facade.handlers.RemoveUserOfGroupHandler;
import mequie.app.facade.handlers.SendPhotoMessageHandler;
import mequie.app.facade.handlers.SendTextMessageHandler;
import mequie.app.facade.network.NetworkMessage;
import mequie.app.facade.network.NetworkMessageError;
import mequie.app.facade.network.NetworkMessageRequest;
import mequie.app.facade.network.NetworkMessageResponse;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.AuthenticationFailedException;
import mequie.app.facade.exceptions.ErrorInsufficientArgumentsException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.exceptions.MequieException;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents the skel class of the server.
 * This class is the intermediary between who receives the messages (network) and the application in memory.
 * Will authenticate a possible user and if successful the invoke method can be used.
 * Invoke will get the operation that the user asked for will execute it and the will build the message
 * to send to user with the result of that asked operation. The message built is a NetworkMessage.
 */
public class MequieSkel {
	// the system class to get the handlers
	private Mequie system = new Mequie();
	// the current session of an user
	private Session currentSession;
	
	/**
	 * Create a MequieSkel for a session
	 * (NOTE: will have to autenticate to use the invoke)
	 * @param s the possible session
	 */
	public MequieSkel(Session s) {
		this.currentSession = s;
	}
	
	/**
	 * Checks if a given user exists
	 * @param userID The username of the user
	 * @return true if the user exist on the system, false otherwise
	 */
	public static boolean userExists(String userID) {
		return GetUserFromSessionHandler.userExists(userID);
	}
	
	/**
	 * Authentication of a client
	 * @return a NetworkMessage to sent to client with authentication OK or an Error if authentication failed
	 * @throws ErrorSavingInDiskException 
	 */
	public NetworkMessage autentication() throws AuthenticationFailedException, ErrorSavingInDiskException {
		GetUserFromSessionHandler.authenticateSession(currentSession);
		return new NetworkMessageResponse(NetworkMessage.Opcode.AUTH, "OK");
	}
	
	/**
	 * Invoke method: will execute the operation of a client
	 * @param msg the NetworkMessageRequest by a client
	 * @return a NetworkMessage with the information of operation asked or an Error if wasn't successfully executed
	 */
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
		case GET_LAST_KEY_OF_GROUP:
			response = getLastKeyOfGroup(msg);
			break;
		default:
			response = new NetworkMessageError(msg.getOp(), new MequieException("ERROR invalid operation"));
		}
		
		return response;
	}

	/**
	 * Operation to create a group
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage with OK or an Error if wasn't successfully executed
	 */
	private NetworkMessage createGroup(NetworkMessageRequest msg) {
		try {
			CreateGroupHandler cgh = system.getCreateGroupHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			List<SimpleEntry<String, byte[]>> usersGroupKeys = msg.getUsersGroupKeys();
			
			if (args == null || args.isEmpty() || usersGroupKeys.isEmpty())
				throw new ErrorInsufficientArgumentsException();
			
			String g = args.get(0);
			if (g.equals(""))
				throw new ErrorInsufficientArgumentsException();

			cgh.makeGrupByID(g);
			cgh.groupAssociation();
			cgh.save(usersGroupKeys.get(0).getValue());
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
		
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}
	
	/**
	 * Operation to add a user to a group
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage with OK or an Error if wasn't successfully executed
	 */
	private NetworkMessage addUserToGroup(NetworkMessageRequest msg) {		
		try {
			AddUserToGroupHandler augh = system.getAddUserToGroupHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			List<SimpleEntry<String, byte[]>> usersGroupKeys = msg.getUsersGroupKeys();
			
			if (args == null || args.size() < 2 || usersGroupKeys.isEmpty())
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
			augh.save(usersGroupKeys);
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
			
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}
	
	/**
	 * Operation to remove a user from a group
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage with OK or an Error if wasn't successfully executed
	 */
	private NetworkMessage removeUserFromGroup(NetworkMessageRequest msg) {		
		try {
			RemoveUserOfGroupHandler rugh = system.getRemoveUserOfGroupHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			List<SimpleEntry<String, byte[]>> usersGroupKeys = msg.getUsersGroupKeys();
			
			if (args == null || args.size() < 2 || usersGroupKeys.isEmpty())
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
			rugh.save(usersGroupKeys);
			
			return new NetworkMessageResponse(msg.getOp(), "OK");
			
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}
	
	/**
	 * Operation to get the information of a group
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage the information asked or an Error if wasn't successfully executed
	 */
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
			
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}
	
	/**
	 * Operation to get the information of an user
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage the information asked or an Error if wasn't successfully executed
	 */
	private NetworkMessage getUserInfo(NetworkMessageRequest msg) {
		
		GetUserInfoHandler uih = system.getGetUserInfoHandler(currentSession);
			
		List<List<String>> groups = uih.getUserInfo();
			
		return new NetworkMessageResponse(msg.getOp(), "OK", new ArrayList<String>(groups.get(0)), new ArrayList<String>(groups.get(1)));
		
	}
	
	/**
	 * Operation to send a text message to a group
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage with OK or an Error if wasn't successfully executed
	 */
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
		
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}
	
	/**
	 * Operation to send a photo message to a group
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage with OK or an Error if wasn't successfully executed
	 */
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
			
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}
	
	/**
	 * Operation to collect the messages unseen of a group by the client 
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage with the text messages and the photos or an Error if wasn't successfully executed
	 */
	@SuppressWarnings("unchecked")
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
			ArrayList<SimpleEntry<Integer,String>> info1 = new ArrayList<>((Collection<SimpleEntry<Integer,String>>) msgs.get(0));
			ArrayList<SimpleEntry<Integer,byte[]>> info2 = new ArrayList<>((Collection<SimpleEntry<Integer,byte[]>>) msgs.get(1));
			HashMap<Integer,byte[]> userKeys = cmh.getUserKeys();
			cmh.save();
			
			return new NetworkMessageResponse(msg.getOp(), "OK", info1, info2, userKeys);
			
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}
	
	/**
	 * Operation to get the history of text messages of a group
	 * @param msg the message with the information of what the client asked for
	 * @return a NetworkMessage with the information asked or an Error if wasn't successfully executed
	 */
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
			ArrayList<SimpleEntry<Integer,String>> msgs = mhgh.getHistory();
			HashMap<Integer,byte[]> userKeys = mhgh.getUserKeys();
			
			return new NetworkMessageResponse(msg.getOp(), "OK", msgs, userKeys);
			
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}
	
	private NetworkMessage getLastKeyOfGroup(NetworkMessageRequest msg) {
		try {
			GetLastKeyOfGroupHandler gkg = new GetLastKeyOfGroupHandler(currentSession);
			
			// list of arguments
			List<String> args = msg.getArguments();
			
			if (args.isEmpty())
				throw new ErrorInsufficientArgumentsException();
			
			String g = args.get(0);
			if (g == null || g.equals(""))
				throw new ErrorInsufficientArgumentsException();
			
			byte[] encryptedKey = gkg.getLastKeyOfGroup(g);
			
			return new NetworkMessageResponse(msg.getOp(), "OK", null, new ArrayList<byte[]>(Arrays.asList(encryptedKey)));
			
		} catch (MequieException e) {
			return new NetworkMessageError(msg.getOp(), e);
		}
	}

}
