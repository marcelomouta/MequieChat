package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;

public class CollectMessagesHandler {
	
	private User currentUser;
    private Group currentGroup;

    public CollectMessagesHandler(Session s) {
        currentUser = s.getUser();
    }
    
    public void indicateGroupID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public List<String> getNotSeenMessages() throws UserNotHavePermissionException {
    	if (!currentGroup.isUserOfGroup(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	List<Message> msgs = currentGroup.collectMessagesUnseenByUser(currentUser);
    	// podemos passar so o texto das msg para ler mas as imagens sao gigantes!? como passamo-las?
    	return null;
    }

}
