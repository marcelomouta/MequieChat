package mequie.app.facade.handlers;

import java.io.IOException;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;
import mequie.app.facade.exceptions.ErrorRemovingUserOfGroupException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;
import mequie.utils.WriteInDisk;

public class RemoveUserOfGroupHandler{

    private User currentUser;
    
    private User currentUserToRemove;
    
    private Group currentGroup;

    public RemoveUserOfGroupHandler(Session s) {
        currentUser = s.getUser();
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
    	if ( !SaveToDiskHandler.saveRemoveUserFromGroup(currentUserToRemove, currentGroup) )
    		throw new ErrorSavingInDiskException();
    
    }

}