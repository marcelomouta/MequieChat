package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to send a user text messages to a group
*/
public class SendTextMessageHandler{

	// user that is using this handler
	private User currentUser;
    // group that the user wants to add users
    private Group currentGroup;
    private TextMessage currentMsg;

    /**
     * @param s session to be used in this handler
     */
    public SendTextMessageHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }
    
    /**
     * @param groupID group id of the group
     * @throws NotExistingGroupException if the groupID doesn't match any existing group
     */
    public void getGroupByID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public void createMessage(String text) throws UserNotHavePermissionException {
    	if (!currentGroup.isUserOfGroup(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	currentMsg = currentGroup.createTextMessage(text, currentUser);
    }

    public void sendMessageToGroup() {
    	currentGroup.saveMessage(currentMsg);
    }
    
    /**
     * Saves Makes the operation persistent on disk
     * @throws ErrorSavingInDiskException
     */
    public void save() throws ErrorSavingInDiskException {
    	if ( !OperationsToDiskHandler.saveTextMessageInDisk(currentMsg, currentGroup) )
    		throw new ErrorSavingInDiskException();
    }

}