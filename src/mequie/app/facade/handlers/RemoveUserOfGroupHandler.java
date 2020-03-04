package mequie.app.facade.handlers;

import java.io.IOException;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;
import mequie.app.facade.exceptions.ErrorRemovingUserOfGroupException;
import mequie.app.facade.exceptions.ErrorSavingGroupInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;
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
    
    public void removeUserFromGroup() throws ErrorRemovingUserOfGroupException, Exception {
    	if ( !currentGroup.removeUserByID(currentUserToRemove) )
            throw new ErrorRemovingUserOfGroupException();
    }
    
    public void save() {
    	try {
    		// ler as linhas e ver o grupo que foi alterado e remover do disco
    		// depois escrever a alteracao ao grupo no disco
			WriteInDisk write = new WriteInDisk("Data/group");
			
		} catch (IOException e) {
			// throw new 
		}
    
    }

}