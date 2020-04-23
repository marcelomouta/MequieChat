package mequie.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
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
import java.security.cert.CertificateFactory;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
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
	private static final String USER_CERTIFICATES_PATH = "PubKeys/";

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
			
	        return wrapKey(groupKey, pk);

	        
		} catch (Exception e) {
			throw new MequieException("ERROR could not generate and wrap new user group key");
		}

 
	}

	/**
	 * @param groupKey
	 * @param pk
	 * @return
	 * @throws Exception
	 */
	private static byte[] wrapKey(SecretKey groupKey, PublicKey pk) throws Exception {
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.WRAP_MODE, pk);

		return c.wrap(groupKey);
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

	public static ArrayList<SimpleEntry<String, byte[]>> generateAndWrapUsersGroupKeys(List<String> groupMembers) throws MequieException {
		
		try {
			ArrayList<SimpleEntry<String, byte[]>> wrappedKeys = new ArrayList<>();
			
			SecretKey groupKey = generateNewSecretKey();
			
			for (String user : groupMembers) {
				PublicKey pk = loadUserCertificate(user).getPublicKey();
				
				byte[] wrappedKey = wrapKey(groupKey, pk);
				
				wrappedKeys.add(new SimpleEntry<>(user, wrappedKey));

			}
			
			return wrappedKeys;
	        
		} catch (Exception e) {
			throw new MequieException("ERROR could not generate and wrap the users group key");
		}
	}
	
	/**
	 * Load the certificate of a user from a file
	 * @param certPath The path of the certificate file
	 * @return certificate from certPath
	 * @throws MequieException 
	 */
	public static Certificate loadUserCertificate(String user) throws MequieException {
		try(FileInputStream in1 = new FileInputStream(USER_CERTIFICATES_PATH + user + ".cert")) {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return cf.generateCertificate(in1);
		} catch (IOException | CertificateException e) {
			throw new MequieException("ERROR loading user certificate");
		}
	}

	public static byte[] encryptMessage(byte[] bytes, byte[] encryptedKey) throws MequieException {
		try {
			SecretKey key = unwrapKey(encryptedKey);
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);
			
			return c.doFinal(bytes);
			
		} catch (Exception e) {
			throw new MequieException("ERROR encrypting message");
		}
		
	}

	
	private static SecretKey unwrapKey(byte[] wrappedKey) throws Exception {
		PrivateKey pk = getPrivateKey();
		Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.UNWRAP_MODE, pk);
        
        return (SecretKey) c.unwrap(wrappedKey, ALGORITHM, Cipher.SECRET_KEY);

	}

	/**
	 * Decrypts a given encrypted message with its wrapped key
	 * @param encryptedMsg 
	 * @param wrappedKey
	 * @return decrypted message
	 * @throws MequieException
	 */
	public static byte[] decryptMessage(byte[] encryptedMsg, byte[] wrappedKey) throws MequieException {
		try {
			SecretKey key = unwrapKey(wrappedKey);
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key);
			
			return c.doFinal(encryptedMsg);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MequieException("ERROR decrypting message");
			
		}
	}


}
