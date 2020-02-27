package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;
import mequie.app.facade.exceptions.ErrorRemovingUserOfGroupException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;

public class RemoveUserOfGroupHandler{

    private User currentUser;

    public RemoveUserOfGroupHandler(Session s) {
        currentUser = s.getUser();
    }

    public void indicateUserIDAndGroupID(String userID, String groupID) throws ErrorRemovingUserOfGroupException, NotExistingGroupException, NotExistingUserException, Exception {
        User userToRemove = UserCatalog.getInstance().getUserById(userID);
        if (userToRemove == null)
            throw new NotExistingUserException();
        
            Group currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
        if  (currentGroup == null) 
            throw new NotExistingGroupException();

        if ( !currentGroup.removeUserByID(userToRemove) )
            throw new ErrorRemovingUserOfGroupException();
    }

}