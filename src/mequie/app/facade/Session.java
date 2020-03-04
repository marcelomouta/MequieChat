package mequie.app.facade;

import java.io.Serializable;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.UserCatalog;

public class Session implements Serializable {

    private User currentUser;
    private UserCatalog userCat = UserCatalog.getInstance();

    public Session(String username, String password) {
    	this.currentUser = this.userCat.getUserById(username);
    	// se o user ainda nao foi criado, cria-se esse user
    	if(this.currentUser == null) {
            this.currentUser = this.userCat.createUser(username, password);
            this.userCat.addUser(this.currentUser);
    	}
    }

    public User getUser() {
        return this.currentUser;
    }

}