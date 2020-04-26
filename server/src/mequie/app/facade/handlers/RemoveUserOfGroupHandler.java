package mequie.app.facade.handlers;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorRemovingUserOfGroupException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to remove a users of a group
*/
public class RemoveUserOfGroupHandler{

	// user that is using this handler
	private User currentUser;
    
    private User currentUserToRemove;
    // group that the user wants to add users
    private Group currentGroup;

	private String removedUserKeyfile;
	
	/**
	 * Collect handler to see the messages of the remove user
	 */
	private CollectMessagesHandler cmh;

    /**
     * @param s session to be used in this handler
     */
    public RemoveUserOfGroupHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    /**
     * @param userID user id of a user
     * @throws NotExistingUserException if the userID doesn't match with any existing user
     */
    public void indicateUserID(String userID) throws NotExistingUserException {
    	currentUserToRemove = UserCatalog.getInstance().getUserById(userID);
        if (currentUserToRemove == null)
            throw new NotExistingUserException();
        
        cmh = new CollectMessagesHandler(currentUserToRemove);
    }
    
    /**
     * @param groupID group id of the group
     * @throws NotExistingGroupException if the groupID doesn't match any existing group
     */
    public void indicateGroupID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    	
    	cmh.indicateGroupID(groupID);
    }
    
    public void removeUserFromGroup() throws ErrorRemovingUserOfGroupException, UserNotHavePermissionException {
    	if (!currentGroup.getOwner().equals(currentUser))
    		throw new UserNotHavePermissionException();
    	
    	if ((removedUserKeyfile = currentGroup.removeUserByID(currentUserToRemove)) == null) {
    		
    		if (currentUser.equals(currentUserToRemove)) {
    			throw new ErrorRemovingUserOfGroupException("ERROR removing user from group. Owner cannot remove himself");
    		}
    		
            throw new ErrorRemovingUserOfGroupException();
    	}
    	
    	// it's necessary to collect the message by the user
    	cmh.readMessagesAndPhotos();
    }
    
    /**
     * Saves Makes the operation persistent on disk
     * @param usersGroupKeys 
     * @throws ErrorSavingInDiskException
     */
    public void save(List<SimpleEntry<String, byte[]>> usersGroupKeys) throws ErrorSavingInDiskException {
    	if ( !OperationsToDiskHandler.saveRemoveUserFromGroup(currentUserToRemove, currentGroup) )
    		throw new ErrorSavingInDiskException();
    	
    	// save the collect made to persist the data
    	cmh.save();
    	
    	for (SimpleEntry<String, byte[]> e : usersGroupKeys) {
  			User user = UserCatalog.getInstance().getUserById(e.getKey());
  			boolean savedWithSucess;
  			if (user.equals(currentUserToRemove))
  				savedWithSucess = OperationsToDiskHandler.saveRemovedUserGroupKeyInDisk(e.getValue(), currentGroup, user, removedUserKeyfile);
  			else
  				savedWithSucess = OperationsToDiskHandler.saveUserGroupKeyInDisk(e.getValue(), currentGroup, user, false);
  			
  			if (!savedWithSucess)
  				throw new ErrorSavingInDiskException();
  		}
    	
    }

}