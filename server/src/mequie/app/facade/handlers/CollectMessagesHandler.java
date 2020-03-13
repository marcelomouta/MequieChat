package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.PhotoMessage;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.NotExistingGroupException;
import mequieclient.app.facade.exceptions.UserNotHavePermissionException;

public class CollectMessagesHandler {
	
	private User currentUser;
    private Group currentGroup;

    public CollectMessagesHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }
    
    public void indicateGroupID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public List<List<String>> getNotSeenMessages() throws UserNotHavePermissionException {
    	if (!currentGroup.isUserOfGroup(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	List<List<String>> msgsAndPhotosSeparated = new ArrayList<>(2);
    	
    	// inicializacao da lista de msgs
    	msgsAndPhotosSeparated.add(new ArrayList<>());
    	// inicializacao da lista de photos
    	msgsAndPhotosSeparated.add(new ArrayList<>());
    	
    	List<Message> msgs = currentGroup.collectMessagesUnseenByUser(currentUser);
    	
    	for(Message msg : msgs) {
    		if (msg instanceof TextMessage) {
    			msgsAndPhotosSeparated.get(0).add(msg.getInfo());
    		} else if (msg instanceof PhotoMessage) {
    			msgsAndPhotosSeparated.get(1).add(msg.getInfo());
    		}
    	}
    	
    	return msgsAndPhotosSeparated;
    }

}
