package mequie.app.facade.handlers;

import java.io.IOException;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;
import mequie.app.facade.exceptions.ErrorSavingGroupInDiskException;
import mequie.utils.WriteInDisk;

public class CreateGroupHandler{

    private User currentUser;
    private Group currentGroup;

    public CreateGroupHandler(Session s) {
        currentUser = s.getUser();
    }

    public void makeGrupByID(String groupID) {
    	currentGroup = currentUser.createGroup(groupID);
    }
    
    public void addGroup() throws ErrorCreatingGroupException {
    	if ( !GroupCatalog.getInstance().addGroup(currentGroup) )
            throw new ErrorCreatingGroupException();
    }
    
    public void save() throws ErrorSavingGroupInDiskException {
    	try {
			WriteInDisk write = new WriteInDisk("Data/group.txt");
			write.saveStringSeparatedBy(currentGroup.getGoupID(), ":");
			write.saveStringSeparatedBy(currentUser.getUserID(), ":");
		} catch (IOException e) {
			throw new ErrorSavingGroupInDiskException();
		}
    }

}