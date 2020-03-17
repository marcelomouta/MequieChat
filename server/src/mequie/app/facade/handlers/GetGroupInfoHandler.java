package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.NotExistingGroupException;

public class GetGroupInfoHandler{

    private User currentUser;
    
    private Group currentGroup;

    public GetGroupInfoHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    // mostrar dono do grupo, numero de utilizadores (se for o dono do grupo, os utilizadores tmb)
    public void indicateGroupID(String groupID) throws NotExistingGroupException {
    	this.currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
        if  (this.currentGroup == null)
            throw new NotExistingGroupException();
    }
    
    public List<String> getInfo() {
    	List<String> info = new ArrayList<String>();
    	
    	info.add(this.currentGroup.getGoupID());
    	
    	int user_num = this.currentGroup.getNumberOfUsers();
    	info.add(String.valueOf(user_num));
    	
    	if ( currentGroup.getOwner().equals(currentUser) ) {
    		List<User> users = currentGroup.getAllUsers();
    		
    		// add them to info to send it to the client
    		users.forEach(u -> info.add(u.toString()));
    	}
    	
    	return  info;
    }

}