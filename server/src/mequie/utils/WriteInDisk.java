package mequie.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteInDisk {

	private File fileToWrite;
	
	public WriteInDisk(String fileLocation) throws IOException {
		fileToWrite = new File(fileLocation);
		fileToWrite.getParentFile().mkdirs();
	}
	
	public boolean saveSimpleString(String info) {
		try(FileWriter writer = new FileWriter(fileToWrite, true)) {
			writer.write(info);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
		
	public boolean saveStringSeparatedBy(String toSave, String sep) {
		try(FileWriter writer = new FileWriter(fileToWrite, true)) {
			writer.write(sep + toSave);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveTwoStringsSeparatedBy(String toSave1, String toSave2, String sep) {
		try(FileWriter writer = new FileWriter(fileToWrite, true)) {
			writer.write(toSave1 + sep + toSave2);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveListOfStringsSeparatedBy(List<String> itemsToSave, String sep) {
		StringBuilder sb = new StringBuilder();
		try(FileWriter writer = new FileWriter(fileToWrite, true)) {
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
	
	public String getFileLocation() throws IOException {
		return this.fileToWrite.getPath();
	}

	public boolean emptyFile() {
		try (FileWriter writer = new FileWriter(fileToWrite)) {
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}
