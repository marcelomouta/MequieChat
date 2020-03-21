package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;
import mequieclient.app.facade.exceptions.NotExistingGroupException;

public class SendTextMessageHandler{

    private User currentUser;
    private Group currentGroup;
    private TextMessage currentMsg;

    public SendTextMessageHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }
    
    public void getGroupByID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public void createMessage(String text) {
    	currentMsg = currentGroup.createTextMessage(text, currentUser);
    }

    public void sendMessageToGroup() {
    	currentGroup.saveMessage(currentMsg);
    }
    
    public void save() throws ErrorSavingInDiskException {
    	if ( !OperationsToDiskHandler.saveTextMessageInDisk(currentMsg, currentGroup) )
    		throw new ErrorSavingInDiskException();
    }

}