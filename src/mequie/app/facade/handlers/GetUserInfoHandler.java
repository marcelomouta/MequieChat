package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;

public class GetUserInfoHandler{

    private User currentUser;

    public GetUserInfoHandler(Session s) {
        currentUser = s.getUser();
    }

    public List<List<String>> getUserInfo() {
    	List<List<String>> groupsAndOwnedGroupsSeparated = new ArrayList<>(2);
    	
    	// inicializacao da lista de msgs
    	groupsAndOwnedGroupsSeparated.add(new ArrayList<>());
    	// inicializacao da lista de photos
    	groupsAndOwnedGroupsSeparated.add(new ArrayList<>());
    	
    	for (Group g : currentUser.getAllGroups()) {
    		groupsAndOwnedGroupsSeparated.get(0).add(g.toString());
    	}

    	for (Group g : currentUser.getGroupsWhoUserIsLeader()) {
    		groupsAndOwnedGroupsSeparated.get(1).add(g.toString());
    	}
    	
    	return groupsAndOwnedGroupsSeparated;
    }

}