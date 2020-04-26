package mequie.app.facade.handlers;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorRetrievingUserKeysException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;

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

    /**
     * @param s session to be used in this handler
     */
    public MessageHistoryOfGroupHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
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
     * @return list of the text messages in group's history
     * @throws UserNotHavePermissionException
     */
    public ArrayList<SimpleEntry<Integer,String>> getHistory() throws UserNotHavePermissionException {
    	if (!currentGroup.isUserOfGroup(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	ArrayList<SimpleEntry<Integer,String>> textOfMsgs = new ArrayList<>();
    	List<Message> msgSeen = currentGroup.getHistory();
    	
    	for (Message m : msgSeen) {
    		textOfMsgs.add(new SimpleEntry<>(m.getKeyID(), m.toString()));
    	}
    	
    	return textOfMsgs;
    }

	public HashMap<Integer, byte[]> getUserKeys() throws ErrorRetrievingUserKeysException {
		HashMap<Integer, byte[]> keys = OperationsToDiskHandler.getUserGroupKeys(currentUser, currentGroup);
		if (keys == null)
			throw new ErrorRetrievingUserKeysException();
		return keys;
	}

}