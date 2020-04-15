package mequie.utils.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

import mequie.app.facade.exceptions.MequieException;

public class Encryption {

	/**
	 * The algorithm used to encrypt and decrypt
	 */
	public static final String ALGORITHM = "AES";

	/**
	 * The file ENCRYPTED with the Secret Key (secalhar mover para config)
	 */
	private static final String FILEWITHSECRETKEY = "Data/a.key";
	
	/**
	 * The name of the server RSA key used to encrypt the secret key
	 */
	private static final String RSA_KEY_NAME = "keyRSA";

	/**
	 * KeyStore path
	 */
	private static String keystore;

	/**
	 * The password fot he keystore
	 */
	private static String passwordKeystore;

	/**
	 * The Secret key in memory for faster operations
	 */
	protected static Key key;

	/**
	 * Load a Secret Key to use in encrypt/decrypt processes
	 * If there is already a Secret key it will be loaded, 
	 * otherwise it will be generated a new random secret key
	 * 
	 * @throws MequieException
	 */
	public static void loadSecretKey(String ks, String passwdKeystore) throws MequieException {
		keystore = ks;
		passwordKeystore = passwdKeystore;
		
		// no Mequie não vai ser key == null mas sim se existe o ficheiro blah.key (que
		// irá estar cifrado com a chave publica do servidor ISTO PARA que quem possa 
		// decifra-la seja unicamente o Servido
		if ( !(new File(FILEWITHSECRETKEY).exists()) ) {
			if (!generateRandomSecretKey())
				throw new MequieException("ERROR generating new System Secret key.");

			encryptSecretKey();
		} 
		else { // if exists only loads to memory
			loadExistingSecretKey();
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
	 * Encrypt the Secret key with the public key of the server
	 * BECAUSE with this ONLY the Server can decrypt the secret
	 * key because only the server has the private key necessary
	 * to decrypt the Secret Key
	 * 
	 * @throws MequieException
	 */
	private static void encryptSecretKey() throws MequieException {

		try {

			//3. obter chave publica para cifrar a chave secreta
			//3.1 obter chave publica da keystore
			FileInputStream kfile = new FileInputStream(keystore);  //keystore
			KeyStore kstore = KeyStore.getInstance("JKS");
			kstore.load(kfile, passwordKeystore.toCharArray());           //password
			Certificate cert = kstore.getCertificate(RSA_KEY_NAME);  //alias do utilizador
			PublicKey ku = cert.getPublicKey();

			//3.2 cifrar chave secreta com chave publica

			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.WRAP_MODE, ku);

			byte[] wrappedKey = c.wrap(key);


			// escreve wrapped key em ficheiro (a.key)
			FileOutputStream kos = new FileOutputStream(FILEWITHSECRETKEY);
			ObjectOutputStream oos = new ObjectOutputStream(kos);
			oos.writeObject(wrappedKey);
			oos.close();
			kos.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new MequieException("ERROR encrypting the System secret key file. Try again.");
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

			//1. decifrar a chave secreta
			//1.1 ler wrapped key do ficheiro (a.key)
			FileInputStream kis = new FileInputStream(FILEWITHSECRETKEY);
			ObjectInputStream ois = new ObjectInputStream(kis);

			byte[] wrappedKey = (byte[]) ois.readObject(); // lido do ficheiro

			ois.close();
			kis.close();

			//1.2 obter chave privada
			FileInputStream kfile = new FileInputStream(keystore);  //keystore
			KeyStore kstore = KeyStore.getInstance("JKS");
			kstore.load(kfile, passwordKeystore.toCharArray());
			Key kr = kstore.getKey(RSA_KEY_NAME, passwordKeystore.toCharArray());

			//1.3 fazer unwrap da chave secreta
			Cipher c = Cipher.getInstance("RSA");
			c.init(Cipher.UNWRAP_MODE, kr);
			key = c.unwrap(wrappedKey, "AES", Cipher.SECRET_KEY);

		} catch (Exception e) {
			e.printStackTrace();
			throw new MequieException("ERROR encrypting the System secret key file. Try again.");
		}
	}

	/**
	 * 
	 * @return the Secret key used in the system
	 */
	public static Key getKey() {
		return key;
	}

	public static CipherInputStream getCipherInputStream(FileInputStream in) throws MequieException {
		try {
			Cipher d = Cipher.getInstance(Encryption.ALGORITHM);
			d.init(Cipher.DECRYPT_MODE, key);
			return new CipherInputStream(in, d);
		}catch (Exception e) {
			e.printStackTrace();
			throw new MequieException("ERROR getting a Cipher Input Stream");
		}
	}

	public static CipherOutputStream getCipherOutputStream(FileOutputStream fos) throws MequieException {
		try {
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);
			return new CipherOutputStream(fos, c);
		}catch (Exception e) {
			e.printStackTrace();
			throw new MequieException("ERROR getting a Cipher Input Stream");
		}
	}
	
	public static void closeCipherStreams() {
		
	}
}
