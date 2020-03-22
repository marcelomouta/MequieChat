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
/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to add users to a group
*/
public class AddUserToGroupHandler{

	// user that is using this handler
    private User currentUser;
    // group that the user wants to add users
    private Group currentGroup;
    // user that is going to be added to the group
    private User currentUserToAdd;

    /**
     * @param s session to be used in this handler
     */
    public AddUserToGroupHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    /**
     * @param userID user id of the user to add to be added to a group
     * @throws NotExistingUserException if the userID doesn't match with any existing user
     */
    public void getUserByID(String userID) throws NotExistingUserException {
    	currentUserToAdd = UserCatalog.getInstance().getUserById(userID);
        if (currentUserToAdd == null)
            throw new NotExistingUserException();
    }
    
    /**
     * @param groupID group id of the group to add users to
     * @throws NotExistingGroupException if the groupID doesn't match any existing group
     */
    public void getGroupByID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    /**
     * Adds the user to the group
     * @throws ErrorAddingUserToGroupException if there was a problem adding the user to the group
     * @throws UserNotHavePermissionException if the user doesn't have permission to add users to this group
     */
    public void addNewUserToGroup() throws ErrorAddingUserToGroupException, UserNotHavePermissionException {
    	if (!currentGroup.getOwner().equals(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	if ( !currentGroup.addUserByID(currentUserToAdd) )
            throw new ErrorAddingUserToGroupException();
    }
    
    /**
     * Saves Makes the operation persistent on disk
     * @throws ErrorSavingInDiskException
     */
    public void save() throws ErrorSavingInDiskException {
  		if ( !OperationsToDiskHandler.saveUserToGroupInDisk(currentUserToAdd, currentGroup) )
  			throw new ErrorSavingInDiskException();
    }

}