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
    
    private List<Group> groupsOfCurrentUser = new ArrayList<>();

    public GetUserInfoHandler(Session s) {
        currentUser = s.getUser();
    }

    public void getUserInfo() {
    	groupsOfCurrentUser = currentUser.getAllGroups();
    }
    
    // mostrar grupos a que pertence e de que eh dono?! Como organizar info?!

}