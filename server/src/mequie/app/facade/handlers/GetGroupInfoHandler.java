package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to get a group information
*/
public class GetGroupInfoHandler{

	// user that is using this handler
	private User currentUser;
    // group that the user wants to add users
    private Group currentGroup;

    /**
     * @param s session to be used in this handler
     */
    public GetGroupInfoHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    /**
     * @param groupID group id of the group
     * @throws NotExistingGroupException if the groupID doesn't match any existing group
     */
    public void indicateGroupID(String groupID) throws NotExistingGroupException, UserNotHavePermissionException {
    	this.currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
        if  (this.currentGroup == null)
            throw new NotExistingGroupException();
        
        if (!this.currentGroup.isUserOfGroup(currentUser))
        	throw new UserNotHavePermissionException();
    }
    
    /**
     * 
     * @return list with group id, number of users 
     * and, if this user is the group owner, the users in this group
     */
    public List<String> getInfo() {
    	List<String> info = new ArrayList<String>();
    	
    	info.add(this.currentGroup.getGroupID());
    	
    	int user_num = this.currentGroup.getNumberOfUsers();
    	info.add(String.valueOf(user_num));
    	
    	if ( currentGroup.getOwner().equals(currentUser) ) {
    		List<User> users = currentGroup.getAllUsers();
    		
    		// add them to info to send it to the client
    		users.forEach(u -> info.add(u.toString()));
    	}
    	
    	return  info;
    }

}