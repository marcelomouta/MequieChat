package mequie.app.facade;

import java.io.Serializable;
import java.security.cert.Certificate;

/**
 * Current session of a client containing client credentials
 */
@SuppressWarnings("serial")
public class Session implements Serializable {
    // generic information about the session of a client
    private String username;
    
    // used in authentication 
    private long nonce;
    // signed nonce
    private byte[] signature;
    private boolean unknownUserFlag;
    private Certificate userCertificate;
    

    /**
     * Session containing userID
     * @param username
     */
    public Session(String username) {
    	this.username = username;
    }
    
    /**
     * 
     * @return the username of the user client
     */
    public String getUsername() {
    	return this.username;
    }
    
    /**
	 * @return the nonce
	 */
	public long getNonce() {
		return nonce;
	}

	/**
	 * @return the unknownUserFlag
	 */
	public boolean isUnknownUserFlag() {
		return unknownUserFlag;
	}

	/**
	 * @param nonce the nonce to set
	 */
	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	/**
	 * @param unknownUserFlag the unknownUserFlag to set
	 */
	public void setUnknownUserFlag(boolean unknownUserFlag) {
		this.unknownUserFlag = unknownUserFlag;
	}

	/**
	 * @return the signature
	 */
	public byte[] getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	/**
	 * @return the userCertificate
	 */
	public Certificate getUserCertificate() {
		return userCertificate;
	}

	/**
	 * @param userCertificate the userCertificate to set
	 */
	public void setUserCertificate(Certificate userCertificate) {
		this.userCertificate = userCertificate;
	}

}