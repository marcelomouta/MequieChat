package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorLoadingKeyException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.UserNotHavePermissionException;

public class GetLastKeyOfGroupHandler {

	// user that is using this handler
	private User currentUser;
	// group that the user wants to add users
	private Group currentGroup;
	
	public GetLastKeyOfGroupHandler(Session s) {
		currentUser = GetUserFromSessionHandler.getUserFromSession(s);
	}
	
	/**
	 * Get the key of the group (encrypted of course)
	 * @param groupID The group id
	 * @return the encrypted key of the group
	 * @throws NotExistingGroupException When the group doesn't exist
	 * @throws UserNotHavePermissionException When the user doesn't belong to group
	 * @throws ErrorLoadingKeyException When loading the key from the file fails
	 */
	public byte[] getLastKeyOfGroup(String groupID) throws NotExistingGroupException, 
		UserNotHavePermissionException, ErrorLoadingKeyException {
		
		currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
		// the group doesnt exist
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    	
    	// the user doesnt have permission (because isnt in the group)
    	if (!currentGroup.isUserOfGroup(currentUser))
			throw new UserNotHavePermissionException();
    	
    	byte[] key = OperationsToDiskHandler.readLastUserKey(currentUser, currentGroup);
    	if (key == null)
    		throw new ErrorLoadingKeyException(currentGroup.getGroupID());
    		
    	return key;
	}

}
