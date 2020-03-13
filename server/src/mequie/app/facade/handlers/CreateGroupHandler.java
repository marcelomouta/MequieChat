package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.ErrorCreatingGroupException;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;

public class CreateGroupHandler{

    private User currentUser;
    private Group currentGroup;

    public CreateGroupHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    public void makeGrupByID(String groupID) {
    	currentGroup = currentUser.createGroup(groupID);
    }
    
    public void groupAssociation() throws ErrorCreatingGroupException {
    	if ( !GroupCatalog.getInstance().addGroup(currentGroup) )
            throw new ErrorCreatingGroupException();
    }
    
    public void save() throws ErrorSavingInDiskException {
    	if ( !SaveToDiskHandler.saveGroupInDisk(currentGroup) )
    		throw new ErrorSavingInDiskException();
    }

}