package mequie.utils.encryption;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import mequie.app.facade.exceptions.MequieException;

public class Encrypt extends Encryption {

	/**
	 * Encrypt a String with the system secret key
	 * 
	 * @param toEncrypt The string to encrypt
	 * @return the new string encrypted with the system secret key
	 */
	public static String encryptString(String toEncrypt) {
		//byte[] b = toEncrypt.getBytes(Charset.forName("UTF-8"));
		return null;
	}

	/**
	 * Encrypt a file with the system secret key
	 * 
	 * @param file The file to encrypt
	 * @throws MequieException
	 */
	public static void encryptFile(String file) throws MequieException {

		try {
			//2. cifrar dados (a.txt -> a.cif)
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);

			FileInputStream fis;
			FileOutputStream fos;
			CipherOutputStream cos;

			fis = new FileInputStream(file);
			fos = new FileOutputStream(file + ".cif");

			cos = new CipherOutputStream(fos, c);
			byte[] b = new byte[16];
			int i = fis.read(b);
			while (i != -1) {
				cos.write(b, 0, i);
				i = fis.read(b);
			}

			cos.close();
			fis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new MequieException("ERROR encrypting the file");
		}
		
	}

}
