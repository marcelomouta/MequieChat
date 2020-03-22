package mequieclient.app.facade;

import java.io.Serializable;

/**
 * Current session containing user credentials
 */
public class Session implements Serializable {
    
    private String username;
    private String password;

    /**
     * Session containing user credentials
     * @param username
     * @param password
     */
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