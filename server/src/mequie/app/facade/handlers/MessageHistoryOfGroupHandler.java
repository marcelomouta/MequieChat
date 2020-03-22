package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.NotExistingGroupException;
import mequieclient.app.facade.exceptions.UserNotHavePermissionException;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to get messages history of a group
*/
public class MessageHistoryOfGroupHandler{

	// user that is using this handler
	private User currentUser;
    // group that the user wants to add users
    private Group currentGroup;

    public MessageHistoryOfGroupHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    // mensagens vistas por todos do grupo
    public void indicateGroupID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public List<String> getHistory() throws UserNotHavePermissionException {
    	if (!currentGroup.isUserOfGroup(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	List<String> textOfMsgs = new ArrayList<>();
    	List<Message> msgSeen = currentGroup.getHistory();
    	
    	for (Message m : msgSeen) {
    		textOfMsgs.add(m.toString());
    	}
    	
    	return textOfMsgs;
    }

}