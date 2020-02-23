package mequie.app.facade.handlers;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.domain.Group;
import mequie.app.facade.Session;

public class UserManagementHandler {

    private User currentUser;

    public UserManagementHandler(Session s) {
        //TODO
        //this.currentUser = (User) s.getUser();
    }

    // mostrar grupos a que pertence e de que eh dono
    public String getUserInfo() {
        return null;
    }

    public void sendTextMessage(String groupID, String message) {

    }

    public void sendPhotoMessage(String groupID, String Photo) {

    }

    public void collectNotViewedMessagesOfGroup(String groupID) {

    }

    // mensagens vistas por todos do grupo
    public void messageHistoryOfGroup(String groupID) {

    }

}