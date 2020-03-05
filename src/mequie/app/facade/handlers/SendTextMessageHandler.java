package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;

public class SendTextMessageHandler{

    private User currentUser;
    private Group currentGroup;
    private Message currentMsg;

    public SendTextMessageHandler(Session s) {
        currentUser = s.getUser();
    }
    
    public void getGroupByID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public void createMessage(String text) {
//    	currentMsg = currentGroup.createMessage(text, currentUser);
    }

    public void sendMessageToGroup() {
    	currentGroup.saveMessage(currentMsg);
    }

}