package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;

public class CreateGroupHandler{

    private User currentUser;

    public CreateGroupHandler(Session s) {
        currentUser = s.getUser();
    }

    public void indicateGrupID(String groupID) throws ErrorCreatingGroupException {
        Group currentGroup = currentUser.createGroup(groupID);

        if ( !GroupCatalog.getInstance().addGroup(currentGroup) )
            throw new ErrorCreatingGroupException();
    }

}