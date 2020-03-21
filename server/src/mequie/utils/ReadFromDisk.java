package mequie.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadFromDisk {
	
	private Scanner sc;
	private FileInputStream in;
	
	/**
	 * Create a ReadFromDisk with a default flag: will use Scanner
	 */
	public ReadFromDisk(String fileLocation) throws IOException {
		this(fileLocation, true);
	}
	
	/**
	 * Create a ReadFromDisk with a flag. If flag == true it will use
	 * scanner otherwise will use FileInputStream
	 */
	public ReadFromDisk(String fileLocation, boolean flag) throws IOException {
		try {
			if (flag)
				sc = new Scanner(new File(fileLocation));
			else
				in = new FileInputStream(fileLocation);
		} catch (FileNotFoundException e) {
			File f = new File(fileLocation);
			f.getParentFile().mkdirs();
			f.createNewFile();
			if (flag)
				sc = new Scanner(f);
			else
				in = new FileInputStream(f);
		}
	}
	
	public String readLine() {
		return sc.nextLine();
	}
	
	public List<String> readAllLines() {
		List<String> lines = new ArrayList<>();
		while (sc.hasNextLine()) {
			lines.add(sc.nextLine());
		}
		return lines;
	}
	
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
