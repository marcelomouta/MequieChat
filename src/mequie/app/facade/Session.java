package mequie.app.facade;

import java.io.Serializable;

import mequie.app.domain.User;

public class Session implements Serializable {

    private User currentUser;

    public Session(String username, String password) {
        this.currentUser = new User(username, password);
    }

    public User getUser() {
        return this.currentUser;
    }

}