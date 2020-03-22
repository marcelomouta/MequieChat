package mequie.app.facade.handlers;

import mequie.app.domain.Group;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorCreatingGroupException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to create a group
*/
public class CreateGroupHandler{

	// user that is using this handler
	private User currentUser;
    // group that the user wants to add users
    private Group currentGroup;

    /**
     * @param s session to be used in this handler
     */
    public CreateGroupHandler(Session s) {
        currentUser = GetUserFromSessionHandler.getUserFromSession(s);
    }

    /**
     * Creates a new group
     * @param groupID id of the group to create
     */
    public void makeGrupByID(String groupID) {
    	currentGroup = currentUser.createGroup(groupID);
    }
    
    /**
     * Links current user to this group
     * @throws ErrorCreatingGroupException
     */
    public void groupAssociation() throws ErrorCreatingGroupException {
    	if ( !GroupCatalog.getInstance().addGroup(currentGroup) )
            throw new ErrorCreatingGroupException();
    	
    	currentUser.addGroupToOwnededGroups(currentGroup);
    	currentUser.addGroupToBelongedGroups(currentGroup);
    }
    
    /**
     * Saves Makes the operation persistent on disk
     * @throws ErrorSavingInDiskException
     */
    public void save() throws ErrorSavingInDiskException {
    	if ( !OperationsToDiskHandler.saveGroupInDisk(currentGroup) )
    		throw new ErrorSavingInDiskException();
    }

}