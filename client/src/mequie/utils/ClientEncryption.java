package mequie.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import mequie.app.facade.exceptions.MequieException;

public class ClientEncryption {
	

	private static final String SIGNATURE_ALGORYTHM = "MD5withRSA";

	/**
	 * The algorithm used to encrypt and decrypt
	 */
	public static final String ALGORITHM = "AES";
	
	/**
	 * The name of the server RSA key used to encrypt the secret key
	 */
	private static final String RSA_KEY_NAME = "keyRSA";

	/**
	 * The type of the keystore
	 */
	private static final String STORETYPE = "JKS";

	/**
	 * KeyStore path
	 */
	private static String keystore;

	/**
	 * The password for the keystore
	 */
	private static String keystorePassword;

	public static void loadKeys(String ks, String ksPswd) {
		keystore = ks;
		keystorePassword = ksPswd;
		
	}

	public ClientEncryption(String keystore, String keystorePassword) {
		this.keystore = keystore;
		this.keystorePassword = keystorePassword;
	}
	
	public static byte[] signsNonce(long nonce) throws MequieException {
		try {
			
			PrivateKey kr = getPrivateKey();
			Signature s = Signature.getInstance(SIGNATURE_ALGORYTHM);
			
			s.initSign(kr);
			
			ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
			byte[] buf = buffer.putLong(nonce).array();
			s.update(buf);
			return s.sign();
		
		} catch (Exception e) {
			throw new MequieException("ERROR signing the nonce received from the server");
		}
	}

	/**
	 * @return the users private key
	 * @throws Exception
	 */
	private static PrivateKey getPrivateKey() throws Exception {

		try (FileInputStream kfile = new FileInputStream(keystore)) {
			KeyStore kstore = loadKeystore(kfile);
			PrivateKey pk = (PrivateKey) kstore.getKey(RSA_KEY_NAME, keystorePassword.toCharArray());
			
			kfile.close();
			
			return pk;
			
		} catch (Exception e) { throw e; }
		
	}

	public static Certificate getCertificate() throws Exception {
		try (FileInputStream kfile = new FileInputStream(keystore)) {
			KeyStore kstore = loadKeystore(kfile);
			Certificate cert = kstore.getCertificate(RSA_KEY_NAME);
						
			return cert;
		} catch (Exception e) { throw e; }
		
	}

	/**
	 * Loads the user keystore
	 * @param kfile inputstream for the store
	 * @return
	 * @throws Exception
	 */
	private static KeyStore loadKeystore(FileInputStream kfile)	throws Exception {
		KeyStore kstore = KeyStore.getInstance(STORETYPE);
		kstore.load(kfile, keystorePassword.toCharArray());
		return kstore;
	}
	
	/**
	 * Generate new group key and wrap it with this users public key
	 * @return Entry with userID and wrapped key
	 * @throws MequieException 
	 */
	public static byte[] generateAndWrapNewUserGroupKey() throws MequieException {
		 
        try {
			SecretKey groupKey = generateNewSecretKey();
			PublicKey pk = getCertificate().getPublicKey();
			
	        Cipher c = Cipher.getInstance("RSA");
	        c.init(Cipher.WRAP_MODE, pk);

	        return c.wrap(groupKey);

	        
		} catch (Exception e) {
			throw new MequieException("ERROR could not generate and wrap new user group key");
		}

 
	}

	/**
	 * @return 
	 * @throws NoSuchAlgorithmException
	 */
	private static SecretKey generateNewSecretKey() throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        return kg.generateKey();
	}


}
