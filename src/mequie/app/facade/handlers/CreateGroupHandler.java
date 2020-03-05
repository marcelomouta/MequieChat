package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;

public class CreateGroupHandler{

    private User currentUser;
    private Group currentGroup;

    public CreateGroupHandler(Session s) {
        currentUser = s.getUser();
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