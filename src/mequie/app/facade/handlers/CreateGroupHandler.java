package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;

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
    
    public void saveGroup() {
    	// chamar o handler de escrever e escrever em disco no ficheiro group.txt
    }

}