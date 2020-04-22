package mequie.app.facade.handlers;

import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.facade.Session;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to get user information
*/
public class GetUserInfoHandler{

	// user that is using this handler
	private User currentUser;

    /**
     * @param s session to be used in this handler
     */
    public GetUserInfoHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    /**
     * 
     * @return list with a list with the user's groups and a list with groups that the user is owner
     */
    public List<List<String>> getUserInfo() {
    	List<List<String>> groupsAndOwnedGroupsSeparated = new ArrayList<>(2);
    	
    	// inicializacao da lista de grupos
    	groupsAndOwnedGroupsSeparated.add(new ArrayList<>());
    	// inicializacao da lista de grupos de que eh dono
    	groupsAndOwnedGroupsSeparated.add(new ArrayList<>());
    	
    	for (Group g : currentUser.getAllGroups()) {
    		groupsAndOwnedGroupsSeparated.get(0).add(g.getGroupID());
    	}

    	for (Group g : currentUser.getGroupsWhoUserIsLeader()) {
    		groupsAndOwnedGroupsSeparated.get(1).add(g.getGroupID());
    	}
    	
    	return groupsAndOwnedGroupsSeparated;
    }

}