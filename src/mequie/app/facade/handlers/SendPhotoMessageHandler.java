package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorAddingUserToGroupException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.app.facade.exceptions.NotExistingUserException;

public class SendPhotoMessageHandler{

    private User currentUser;

    public SendPhotoMessageHandler(Session s) {
        currentUser = s.getUser();
    }

    public void indicateGroupIDAndMessage(String groupID, String Photo) {
    	//TODO
    }

}