package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;
import mequieclient.app.facade.exceptions.NotExistingGroupException;
import mequieclient.app.facade.exceptions.NotExistingUserException;
import mequieclient.app.facade.exceptions.UserNotHavePermissionException;

public class AddUserToGroupHandler{

    private User currentUser;
    
    private Group currentGroup;
    
    private User currentUserToAdd;

    public AddUserToGroupHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    public void getUserByID(String userID) throws NotExistingUserException {
    	currentUserToAdd = UserCatalog.getInstance().getUserById(userID);
        if (currentUserToAdd == null)
            throw new NotExistingUserException();
    }
    
    public void getGroupByID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public void addNewUserToGroup() throws ErrorAddingUserToGroupException, UserNotHavePermissionException, Exception {
    	if (!currentGroup.getOwner().equals(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	if ( !currentGroup.addUserByID(currentUserToAdd) )
            throw new ErrorAddingUserToGroupException();
    }
    
    public void save() throws ErrorSavingInDiskException {
  		if ( !SaveToDiskHandler.saveUserToGroupInDisk(currentUserToAdd, currentGroup) )
  			throw new ErrorSavingInDiskException();
    }

}