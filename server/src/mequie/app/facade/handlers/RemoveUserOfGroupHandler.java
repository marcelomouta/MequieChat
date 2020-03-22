package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.ErrorRemovingUserOfGroupException;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;
import mequieclient.app.facade.exceptions.NotExistingGroupException;
import mequieclient.app.facade.exceptions.NotExistingUserException;
import mequieclient.app.facade.exceptions.UserNotHavePermissionException;

public class RemoveUserOfGroupHandler{

	// user that is using this handler
	private User currentUser;
    
    private User currentUserToRemove;
    
    private Group currentGroup;

    public RemoveUserOfGroupHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    public void indicateUserID(String userID) throws NotExistingUserException {
    	currentUserToRemove = UserCatalog.getInstance().getUserById(userID);
        if (currentUserToRemove == null)
            throw new NotExistingUserException();
    }
    
    public void indicateGroupID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public void removeUserFromGroup() throws ErrorRemovingUserOfGroupException, UserNotHavePermissionException, Exception {
    	if (!currentGroup.getOwner().equals(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	if ( !currentGroup.removeUserByID(currentUserToRemove) )
            throw new ErrorRemovingUserOfGroupException();
    }
    
    public void save() throws ErrorSavingInDiskException {
    	if ( !OperationsToDiskHandler.saveRemoveUserFromGroup(currentUserToRemove, currentGroup) )
    		throw new ErrorSavingInDiskException();
    
    }

}