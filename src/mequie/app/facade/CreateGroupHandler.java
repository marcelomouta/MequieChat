package mequie.app.facade;

import mequie.app.domain.User;
import mequie.app.facade.Session;

public class CreateGroupHandler{

    private User currentUser;

    public CreateGroupHandler(Session s) {
        currentUser = (User) s.getUser();
    }

    public CreateNewGroup(String groupID) {
        
    }
}