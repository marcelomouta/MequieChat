package mequie.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteInDisk {

	private FileWriter writer;
	
	public WriteInDisk(String fileLocation) throws IOException {
		writer = new FileWriter(fileLocation, true);
	}
	
	public boolean saveStringSeparatedBy(String toSave) {
		try {
			writer.write(toSave);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveListOfStringsSeparatedBy(List<String> itemsToSave, String sep) {
		try {
			for (String item : itemsToSave) {
				writer.write(sep);
				writer.write(item);
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
}
