package mequie.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteInDisk {

	private String fileLocation;
	
	public WriteInDisk(String fileLocation) throws IOException {
		this.fileLocation = fileLocation;
	}
	
	public boolean saveStringSeparatedBy(String toSave, String sep) {
		try(FileWriter writer = new FileWriter(fileLocation, true)) {
			writer.write(sep + toSave);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveTwoStringsSeparatedBy(String toSave1, String toSave2, String sep) {
		try(FileWriter writer = new FileWriter(fileLocation, true)) {
			writer.write(toSave1 + sep + toSave2);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveListOfStringsSeparatedBy(List<String> itemsToSave, String sep) {
		StringBuilder sb = new StringBuilder();
		try(FileWriter writer = new FileWriter(fileLocation, true)) {
			for (String item : itemsToSave) {
				sb.append(item + sep);
			}
			sb.deleteCharAt(sb.length() - 1); //remove the last ':'
			
			writer.write(sb.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	
}
