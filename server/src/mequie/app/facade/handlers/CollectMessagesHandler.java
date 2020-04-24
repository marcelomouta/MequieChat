package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.PhotoMessage;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.utils.Configuration;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorRetrievingUserKeysException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to collect group mesages of a user
*/
public class CollectMessagesHandler {

	// user that is using this handler
	private User currentUser;
	// group that the user wants to add users
	private Group currentGroup;
	// list of group messages to be send to the user
	private List<Message> readMsgs = new ArrayList<>();
	// list of photos to remove from disk
	private List<PhotoMessage> photosToRemove = new ArrayList<>();

    /**
     * @param s session to be used in this handler
     */
	public CollectMessagesHandler(Session s) {
		currentUser = GetUserFromSessionHandler.getUserFromSession(s);
	}
	
	/**
	 * 
	 * @param u The current user
	 */
	public CollectMessagesHandler(User u) {
		currentUser = u;
	}

    /**
     * @param groupID group id of the group
     * @throws NotExistingGroupException if the groupID doesn't match any existing group
     */
	public void indicateGroupID(String groupID) throws NotExistingGroupException {
		currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
		if  (currentGroup == null) 
			throw new NotExistingGroupException();
	}

	/**
	 * 
	 * @return list with text messages and photos for the user to see 
	 * @throws UserNotHavePermissionException
	 */
	public List<List<? extends Object>> getNotSeenMessages() throws UserNotHavePermissionException {
		if (!currentGroup.isUserOfGroup(currentUser))
			throw new UserNotHavePermissionException();

		List<List<? extends Object>> msgsAndPhotosSeparated = new ArrayList<>(2);

		// inicializacao da lista de msgs
		List<SimpleEntry<Integer,String>> msgsToAdd = new ArrayList<>();
		// inicializacao da lista de photos
		List<SimpleEntry<Integer,byte[]>> photosToAdd = new ArrayList<>();

		readMsgs = currentGroup.collectMessagesUnseenByUser(currentUser);

		for(Message msg : readMsgs) {
			if (msg.allHaveSeenMessage())
				currentGroup.moveToHistory(msg);

			if (msg instanceof TextMessage) {
				msgsToAdd.add(new SimpleEntry<>(msg.getKeyID(),msg.getInfo()));

			} else if (msg instanceof PhotoMessage) {
				
				// ir buscar os bytes
				byte[] data = OperationsToDiskHandler.getFileContent(Configuration.getPhotoMsgPathName(currentGroup.getGroupID(), msg.getMsgID()));
				if (data != null) {
					photosToAdd.add(new SimpleEntry<>(msg.getKeyID() ,data));
					if (msg.allHaveSeenMessage())
						photosToRemove.add((PhotoMessage) msg);
				}
			}
		}
		msgsAndPhotosSeparated.add(msgsToAdd);
		msgsAndPhotosSeparated.add(photosToAdd);
		
		return msgsAndPhotosSeparated;
	}
	
	/**
	 * Sets the readMessages and photos to delete from server
	 */
	public void readMessagesAndPhotos() {
		readMsgs = currentGroup.collectMessagesUnseenByUser(currentUser);
		
		for(Message msg : readMsgs) {
			if (msg.allHaveSeenMessage())
				currentGroup.moveToHistory(msg);

			if (msg instanceof TextMessage) {
				// empty
			} else if (msg instanceof PhotoMessage) {
				if (msg.allHaveSeenMessage())
					photosToRemove.add((PhotoMessage) msg);
			}
		}
	}

    /**
     * Saves Makes the operation persistent on disk
     * @throws ErrorSavingInDiskException
     */
    public void save() throws ErrorSavingInDiskException {
		if ( !OperationsToDiskHandler.updateSeenMessages(readMsgs, currentGroup, currentUser) )
			throw new ErrorSavingInDiskException();
		
		if ( !OperationsToDiskHandler.removeSeenPhotos(photosToRemove, currentGroup) )
			throw new ErrorSavingInDiskException();
	}

	public HashMap<Integer, byte[]> getUserKeys() throws ErrorRetrievingUserKeysException {
		HashMap<Integer, byte[]> keys = OperationsToDiskHandler.getUserGroupKeys(currentUser, currentGroup);
		if (keys == null)
			throw new ErrorRetrievingUserKeysException();
		return keys;
	}
}
