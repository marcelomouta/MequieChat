package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;

public class MessageHistoryOfGroupHandler{

    private User currentUser;

    public MessageHistoryOfGroupHandler(Session s) {
        currentUser = s.getUser();
    }

    // mensagens vistas por todos do grupo
    public void indicateGroupID(String groupID) {
    	//TODO
    }

}