package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.PhotoMessage;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;
import mequieclient.app.facade.exceptions.NotExistingGroupException;
import mequieclient.app.facade.exceptions.UserNotHavePermissionException;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to send a user photos to a group
*/
public class SendPhotoMessageHandler{

	// user that is using this handler
	private User currentUser;
    // group that the user wants to add users
    private Group currentGroup;
    
    private PhotoMessage currentMsg;

    public SendPhotoMessageHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    public void getGroupByID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public void createMessage() throws UserNotHavePermissionException {
    	if (!currentGroup.isUserOfGroup(currentUser))
			throw new UserNotHavePermissionException();
    	
    	currentMsg = currentGroup.createPhotoMessage();
    }
    
    public void sendMessageToGroup() {
    	currentGroup.saveMessage(currentMsg);
    }
    
    public void save(byte[] photo) throws ErrorSavingInDiskException {
    	if ( !OperationsToDiskHandler.savePhotoMessageInDisk(photo, currentMsg, currentGroup) )
    		throw new ErrorSavingInDiskException();
    }

}