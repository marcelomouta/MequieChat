package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;

public class SendPhotoMessageHandler{

    private User currentUser;
    
    private Group currentGroup;
    
    private Message currentMsg;

    public SendPhotoMessageHandler(Session s) {
        currentUser = s.getUser();
    }

    public void getGroupByID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public void createMessage(byte[] photo) {
    	currentMsg = currentGroup.createPhotoMessage(photo, currentUser);
    }
    
    public void sendMessageToGroup() {
    	currentGroup.saveMessage(currentMsg);
    }
    
    public void save() throws ErrorSavingInDiskException {
    	if ( !SaveToDiskHandler.savePhotoMessageInDisk(currentMsg, currentGroup) )
    		throw new ErrorSavingInDiskException();
    }

}