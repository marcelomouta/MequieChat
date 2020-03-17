package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequieclient.app.facade.Session;

public class GetUserInfoHandler{

    private User currentUser;

    public GetUserInfoHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    public List<List<String>> getUserInfo() {
    	List<List<String>> groupsAndOwnedGroupsSeparated = new ArrayList<>(2);
    	
    	// inicializacao da lista de grupos
    	groupsAndOwnedGroupsSeparated.add(new ArrayList<>());
    	// inicializacao da lista de grupos de que eh dono
    	groupsAndOwnedGroupsSeparated.add(new ArrayList<>());
    	
    	for (Group g : currentUser.getAllGroups()) {
    		groupsAndOwnedGroupsSeparated.get(0).add(g.getGoupID());
    	}

    	for (Group g : currentUser.getGroupsWhoUserIsLeader()) {
    		groupsAndOwnedGroupsSeparated.get(1).add(g.getGoupID());
    	}
    	
    	return groupsAndOwnedGroupsSeparated;
    }

}