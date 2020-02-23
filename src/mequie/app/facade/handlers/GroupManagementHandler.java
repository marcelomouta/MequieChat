package mequie.app.facade.handlers;

import java.util.List;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.domain.Group;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.*;


public class GroupManagementHandler{

    private User currentUser;

    public GroupManagementHandler(Session s) {
        //currentUser = (User) s.getUser();
    }

    public void createNewGroup(String groupID) throws ErrorCreatingGroupException {
        Group currentGroup = currentUser.createGroup(groupID);

        if ( !GroupCatalog.getInstance().addGroup(currentGroup) )
            throw new ErrorCreatingGroupException();
    }

    public void addUserToGroup(String userID, String groupID) throws ErrorAddingUserToGroupException, NotExistingGroupException, NotExistingUserException {
        User userToAdd = UserCatalog.getInstance().getUserById(userID);
        if (userToAdd == null)
            throw new NotExistingUserException();

        Group currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
        if  (currentGroup == null) 
            throw new NotExistingGroupException();
        
        if ( !currentGroup.addUserByID(userToAdd) )
            throw new ErrorAddingUserToGroupException();
    }

    // Exception Exception ... eh mau? Eh devido ao remove do set
    public void removeUserOfGroup(String userID, String groupID) throws ErrorRemovingUserOfGroupException, NotExistingGroupException, NotExistingUserException, Exception {
        User userToRemove = UserCatalog.getInstance().getUserById(userID);
        if (userToRemove == null)
            throw new NotExistingUserException();
        
            Group currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
        if  (currentGroup == null) 
            throw new NotExistingGroupException();

        if ( !currentGroup.removeUserByID(userToRemove) )
            throw new ErrorRemovingUserOfGroupException();
    }

    // mostrar dono do grupo, numero de utilizadores (se for o dono do grupo, os utilizadores tmb)
    public String getGroupInfo(String groupID) throws NotExistingGroupException {
        Group currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
        if  (currentGroup == null) 
            throw new NotExistingGroupException();

        User owner = currentGroup.getOwner();
        int user_num = currentGroup.getNumberOfUsers();
        List<User> users = null;
        if (currentUser.equals(owner))
            users = currentGroup.getAllUsers();
          
        return null; // falta empactorar e enviar a info
        
    }
}