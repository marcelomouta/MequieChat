package mequie.app.facade;

import java.io.Serializable;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.UserCatalog;

public class Session implements Serializable {

    private User currentUser;

    public Session(String username, String password) {
    	UserCatalog userCat = UserCatalog.getInstance();
    	this.currentUser = userCat.getUserById(username);
    	// se o user ainda nao foi criado, cria-se esse user
    	if(this.currentUser == null) {
            this.currentUser = userCat.createUser(username, password);
            userCat.addUser(this.currentUser);
    	}
    }

    public User getUser() {
        return this.currentUser;
    }

}