package mequie.app.facade.handlers;

import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;

public class GetGroupInfoHandler{

    private User currentUser;
    
    private Group currentGroup;

    public GetGroupInfoHandler(Session s) {
        currentUser = s.getUser();
    }

    // mostrar dono do grupo, numero de utilizadores (se for o dono do grupo, os utilizadores tmb)
    public void indicateGroupID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
        if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public String getInfo() {
    	int user_num = currentGroup.getNumberOfUsers();
    	
    	List<User> users = null;
    	
    	if ( currentGroup.getOwner().equals(currentUser) ) {
    		users = currentGroup.getAllUsers();
    	}
    	
    	return null; // falta empactorar e enviar a info
    }

}