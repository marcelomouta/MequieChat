package mequieClient.app.facade;

import java.io.Serializable;

public class Session implements Serializable {
    
    private String username;
    private String password;

    public Session(String username, String password) {
    	this.username = username;
    	this.password = password;
    }
    
    public String getUsername() {
    	return this.username;
    }
    
    public String getPassword() {
    	return this.password;
    }

}