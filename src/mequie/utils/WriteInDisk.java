package mequie.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteInDisk {

	private FileWriter writer;
	
	public WriteInDisk(String fileLocation) throws IOException {
		writer = new FileWriter(fileLocation, true);
	}
	
	public boolean saveStringSeparatedBy(String toSave, String sep) {
		StringBuilder sb = new StringBuilder();
		try {
			writer.write(":" + toSave);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveTwoStringsSeparatedBy(String toSave1, String toSave2, String sep) {
		StringBuilder sb = new StringBuilder();
		try {
			writer.write(toSave1 + ":" + toSave2);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveListOfStringsSeparatedBy(List<String> itemsToSave, String sep) {
		StringBuilder sb = new StringBuilder();
		try {
			for (String item : itemsToSave) {
				sb.append(item + ":");
			}
			sb.deleteCharAt(sb.length() - 1); //remove the last ':'
			
			writer.write(sb.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
}
