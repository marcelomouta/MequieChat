package mequie.app.facade;

import java.io.Serializable;

/**
 * Current session of a client containing client credentials
 */
@SuppressWarnings("serial")
public class Session implements Serializable {
    // generic information about the session of a client
    private String username;
    private byte[] publicKey;

    /**
     * Session containing user credentials
     * @param username
     * @param password
     */
    public Session(String username, byte[] publicKey) {
    	this.username = username;
    	this.publicKey = publicKey;
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
    public byte[] getPublicKey() {
    	return this.publicKey;
    }

}