package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.PhotoMessage;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.utils.Configuration;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;
import mequieclient.app.facade.exceptions.NotExistingGroupException;
import mequieclient.app.facade.exceptions.UserNotHavePermissionException;

public class CollectMessagesHandler {

	private User currentUser;
	private Group currentGroup;

	private List<Message> readMsgs = new ArrayList<>();
	private List<PhotoMessage> photosToRemove = new ArrayList<>();

	public CollectMessagesHandler(Session s) {
		currentUser = GetUserFromSessionHandler.getUserFromSession(s);
	}

	public void indicateGroupID(String groupID) throws NotExistingGroupException {
		currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
		if  (currentGroup == null) 
			throw new NotExistingGroupException();
	}

	public List<List<? extends Object>> getNotSeenMessages() throws UserNotHavePermissionException {
		if (!currentGroup.isUserOfGroup(currentUser))
			throw new UserNotHavePermissionException();

		List<List<? extends Object>> msgsAndPhotosSeparated = new ArrayList<>(2);

		// inicializacao da lista de msgs
		List<String> msgsToAdd = new ArrayList<>();
		// inicializacao da lista de photos
		List<byte[]> photosToAdd = new ArrayList<>();

		readMsgs = currentGroup.collectMessagesUnseenByUser(currentUser);

		for(Message msg : readMsgs) {

			if (msg instanceof TextMessage) {
				msgsToAdd.add(msg.getInfo());

			} else if (msg instanceof PhotoMessage) {
				
				// ir buscar os bytes
				byte[] data = OperationsToDiskHandler.getFileContent(Configuration.getPhotoMsgPathName(currentGroup.getGoupID(), msg.getMsgID()));
				if (data != null) {
					photosToAdd.add(data);
					if (msg.allHaveSeenMessage())
						photosToRemove.add((PhotoMessage) msg);
				}
			}
		}
		msgsAndPhotosSeparated.add(msgsToAdd);
		msgsAndPhotosSeparated.add(photosToAdd);
		
		return msgsAndPhotosSeparated;
	}

	public void save() throws ErrorSavingInDiskException {
		if ( !OperationsToDiskHandler.updateSeenMessages(readMsgs, currentGroup, currentUser) )
			throw new ErrorSavingInDiskException();
		
		if ( !OperationsToDiskHandler.removeSeenPhotos(photosToRemove, currentGroup) )
			throw new ErrorSavingInDiskException();
	}
}
