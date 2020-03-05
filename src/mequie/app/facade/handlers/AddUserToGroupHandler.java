package mequie.app.facade.handlers;

import java.io.IOException;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.ErrorSavingGroupInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;
import mequie.utils.WriteInDisk;

public class AddUserToGroupHandler{

    private User currentUser;
    
    private Group currentGroup;
    
    private User currentUserToAdd;

    public AddUserToGroupHandler(Session s) {
        currentUser = s.getUser();
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
    
    public void addNewUserToGroup() throws ErrorAddingUserToGroupException, Exception {
    	if ( !currentGroup.addUserByID(currentUserToAdd) )
            throw new ErrorAddingUserToGroupException();
    }
    
    public void save() throws ErrorSavingGroupInDiskException {
    	try {
			WriteInDisk write = new WriteInDisk("Data/group.txt");
			write.saveStringSeparatedBy(currentUserToAdd.getUserID(), ":");
		} catch (IOException e) {
			throw new ErrorSavingGroupInDiskException();
		}
    }

}