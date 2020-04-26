package mequie.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import mequie.app.facade.exceptions.MequieException;

public class Encryption {

	private Encryption() { }
	
	/**
	 * The algorythm used on the signature
	 */
	private static final String SIGNATURE_ALGORYTHM = "MD5withRSA";

	/**
	 * The algorithm used to encrypt and decrypt
	 */
	public static final String ALGORITHM = "AES";
	
	/**
	 * The alias of the secret key used to encrypt server files
	 */
	private static final String SECRET_KEY_ALIAS = "serverFilesKey";
	
	/**
	 * The type of the keystore
	 */
	private static final String KEYSTORE_TYPE = "JCEKS";

	/**
	 * Server KeyStore
	 */
	private static KeyStore keystore;
	
	/**
	 * The path of the keystore
	 */
	private static String keystorePath;

	/**
	 * The password for the keystore
	 */
	private static char[] psswdArray;

	/**
	 * The Secret key in memory for faster operations
	 */
	protected static SecretKey key;

	/**
	 * Load a Secret Key to use in encrypt/decrypt processes
	 * If there is already a Secret key it will be loaded, 
	 * otherwise it will be generated a new random secret key
	 * 
	 * @throws MequieException
	 */
	public static void loadSecretKey(String ks, String passwdKeystore) throws MequieException {
		keystorePath = ks;
		psswdArray = passwdKeystore.toCharArray();
		loadKeyStore();
		
		try {
			if ( !keystore.containsAlias(SECRET_KEY_ALIAS) ) {
				if (!generateRandomSecretKey())
					throw new MequieException("ERROR generating new System Secret key.");

				saveSecretKey();
			
			} else { // if exists only loads to memory
				loadExistingSecretKey();
			}
		} catch (KeyStoreException e) {
			// will not get here because keystore is always loaded first
		}
	}

	private static void loadKeyStore() throws MequieException {
		try (FileInputStream kfile = new FileInputStream(keystorePath)) {
			KeyStore kstore = KeyStore.getInstance(KEYSTORE_TYPE);
			kstore.load(kfile, psswdArray);
			keystore = kstore;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new MequieException("ERROR loading keystore");
		}  
	}

	/**
	 * Generate a Random secret key to encrypt the files
	 * or strings needed
	 * 
	 * @return true if it was successfully created, false otherwise
	 */
	private static boolean generateRandomSecretKey() {

		//1. gerar uma chave secreta aleatoria para utilizar ALGORITHM
		KeyGenerator kg;
		try {

			kg = KeyGenerator.getInstance(ALGORITHM);
			kg.init(128);
			key = kg.generateKey();

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Saves the system secret key used for file encryption in the server's keystore
	 * 
	 * @throws MequieException
	 */
	private static void saveSecretKey() throws MequieException {

		try {
			KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(key);
			KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection(psswdArray);
			keystore.setEntry(SECRET_KEY_ALIAS, secret, password);
			
			try (OutputStream os = new FileOutputStream(keystorePath)) {
	            keystore.store(os, psswdArray);
			}

		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new MequieException("ERROR saving the System secret key in the keystore. Try again.");
		}
	}


	/**
	 * Decrypt the Secret key with the private key of the server
	 * BECAUSE it was encrypted with the public key of the server
	 * to prevent the other entities knowing the Secret key (only
	 * the server has the private key which is the only pair of
	 * the public key used to encrypt)
	 * 
	 * @throws MequieException
	 */
	private static void loadExistingSecretKey() throws MequieException {

		try {

			key = (SecretKey) keystore.getKey(SECRET_KEY_ALIAS, psswdArray);

		} catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
			throw new MequieException("ERROR loeading the System secret key from the keystore. Try again.");
		}
	}

	public static CipherInputStream getCipherInputStream(FileInputStream in) throws MequieException {
		try {
			Cipher d = Cipher.getInstance(ALGORITHM);
			d.init(Cipher.DECRYPT_MODE, key);
			return new CipherInputStream(in, d);
		}catch (Exception e) {
			throw new MequieException("ERROR getting a Cipher Input Stream");
		}
	}

	public static CipherOutputStream getCipherOutputStream(FileOutputStream fos) throws MequieException {
		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);
			return new CipherOutputStream(fos, c);
		}catch (Exception e) {
			throw new MequieException("ERROR getting a Cipher Output Stream");
		}
	}

	/**
	 * Load the certificate of a user from a file
	 * @param certPath The path of the certificate file
	 * @return certificate from certPath
	 * @throws MequieException 
	 */
	public static Certificate loadUserCertificate(String certPath) throws MequieException {
		try(FileInputStream in1 = new FileInputStream(certPath)) {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return cf.generateCertificate(in1);
		} catch (IOException | CertificateException e) {
			throw new MequieException("ERROR loading user certificate");
		}
	}
	
	/**
	 * Verifies if the given nonce signature with the users public key 
	 * @param nonce Nonce generated by the server
	 * @param signature Signature of the nonce sent by the user
	 * @param cert Users public key certificate
	 * @return true if the nonce was signed by the user, false otherwise
	 */
	public static boolean verifyNonceSignature(long nonce, byte[] signature, Certificate cert) {
		
		try {
			PublicKey pk = cert.getPublicKey();
			Signature s = Signature.getInstance(SIGNATURE_ALGORYTHM);
			//converts long into byte[]
			byte[] nonceBytes = ByteBuffer.allocate(Long.BYTES).putLong(nonce).array();
			
			s.initVerify(pk);		
			s.update(nonceBytes);
			return s.verify(signature);

		} catch(NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			return false;
		}
	}

	public static String encryptString(String s) throws MequieException {
		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptedString = c.doFinal(s.getBytes());
					
			return convertBytesToString(encryptedString);
		}catch (Exception e) {
			throw new MequieException("ERROR encrypting a string");
		}
	}

	/**
	 * @param encryptedString
	 * @return
	 */
	private static String convertBytesToString(byte[] encryptedString) {
		return DatatypeConverter.printBase64Binary(encryptedString);
	}

	/**
	 * @param s
	 * @return
	 */
	private static byte[] convertStringToBytes(String s) {
		return DatatypeConverter.parseBase64Binary(s);
	}

	public static String decryptString(String s) throws MequieException {
		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedString = c.doFinal(convertStringToBytes(s));
					
			return new String(decryptedString);
		}catch (Exception e) {
			e.printStackTrace();
			throw new MequieException("ERROR decrypting a string");
		}
	}
}
