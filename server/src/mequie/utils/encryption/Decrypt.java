package mequie.utils.encryption;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import mequie.app.facade.exceptions.MequieException;

public class Decrypt extends Encryption {

	/**
	 * Dencrypt a String with the system secret key
	 * 
	 * @param toDecrypt The string to dencrypt
	 * @return the new string encrypted with the system secret key
	 */
	public static String decryptString(String toDecrypt) {
		return null;
	}

	/**
	 * Dencrypt a file with the system secret key
	 * 
	 * @param file The file to dencrypt
	 * @throws MequieException
	 */
	public static void decryptFile(String file) throws MequieException {

		try {
			//2. decifrar os dados
			Cipher d = Cipher.getInstance(ALGORITHM);
			d.init(Cipher.DECRYPT_MODE, key);    //SecretKeySpec é subclasse de secretKey

			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(file + "_decifrado.txt");

			CipherInputStream cis = new CipherInputStream(fis, d);

			byte[] b = new byte[16];
			int i = cis.read(b);
			while (i != -1) {
				fos.write(b, 0, i);
				i = cis.read(b);
			}

			cis.close();
			fos.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MequieException("ERROR encrypting the file");
		}
	}

}

