package mequie.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.CipherInputStream;

import mequie.app.facade.exceptions.MequieException;

public class ReadFromDisk {

	private Scanner sc;
	private FileInputStream in;

	/**
	 * Create a ReadFromDisk with a default flag: will use Scanner
	 */
	public ReadFromDisk(String fileLocation) throws IOException, MequieException {
		this(fileLocation, ReadOperation.PLAINTEXT);
	}

	/**
	 * Create a ReadFromDisk with a flag. If flag == true it will use
	 * scanner otherwise will use FileInputStream
	 * @throws MequieException 
	 */
	public ReadFromDisk(String fileLocation, ReadOperation flag) throws IOException, MequieException {
		try {
			if (flag.equals(ReadOperation.PLAINTEXT))
				sc = new Scanner(new File(fileLocation));
			else
				in = new FileInputStream(fileLocation);

			if (flag.equals(ReadOperation.ENCRYPTEDFILE)) {
				CipherInputStream cis = Encryption.getCipherInputStream(this.in);
				sc = new Scanner(cis);
			}
		} catch (FileNotFoundException e) { //if file doesn't exists creates it
			File f = new File(fileLocation);
			f.getParentFile().mkdirs();
			f.createNewFile();

			if (flag.equals(ReadOperation.PLAINTEXT))
				sc = new Scanner(f);
			else
				in = new FileInputStream(f);

			if (flag.equals(ReadOperation.ENCRYPTEDFILE)) {
				CipherInputStream cis = Encryption.getCipherInputStream(this.in);
				sc = new Scanner(cis);
			}
		}
	}

	/**
	 * Check if the file has one more line
	 * @return true if the file has a next line to read
	 */
	public boolean hasNextLine() {
		return sc.hasNextLine();
	}

	/**
	 * Read the next line
	 * @return the next line of the file
	 */
	public String readLine() {
		return sc.nextLine();
	}

	/**
	 * Read all line of the file
	 * @return a list with all lines of the file
	 */
	public List<String> readAllLines() {
		List<String> lines = new ArrayList<>();
		while ( hasNextLine() ) {
			lines.add( readLine() );
		}
		return lines;
	}

	/**
	 * Read all bytes of the file
	 * @return all bytes of the file
	 */
	public byte[] readAllBytes() {
		try {
			byte[] buf = new byte[in.available()];
			in.read(buf);

			return buf;
		} catch (IOException e) {
			return null;
		}
	}

}
