package mequie.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadFromDisk {
	
	private Scanner sc;
	
	public ReadFromDisk(String fileLocation) throws FileNotFoundException {
		sc = new Scanner(new File(fileLocation));
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

}
