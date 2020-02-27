package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;

public class AddUserToGroupHandler{

    private User currentUser;

    public AddUserToGroupHandler(Session s) {
        currentUser = s.getUser();
    }

    public void indicateUserIDAndGroupID(String userID, String groupID) throws ErrorAddingUserToGroupException, NotExistingGroupException, NotExistingUserException {
        User userToAdd = UserCatalog.getInstance().getUserById(userID);
        if (userToAdd == null)
            throw new NotExistingUserException();

        Group currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
        if  (currentGroup == null) 
            throw new NotExistingGroupException();
        
        if ( !currentGroup.addUserByID(userToAdd) )
            throw new ErrorAddingUserToGroupException();
    }

}