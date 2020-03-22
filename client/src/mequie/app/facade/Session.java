package mequie.app.facade;

import java.io.Serializable;

/**
 * Current session containing user credentials
 */
public class Session implements Serializable {
    // generic information about the session of a client
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
    
    /**
     * 
     * @return the username of the user client
     */
    public String getUsername() {
    	return this.username;
    }
    
    /**
     * 
     * @return the password of the user client
     */
    public String getPassword() {
    	return this.password;
    }

}