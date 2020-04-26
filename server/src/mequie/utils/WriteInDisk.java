package mequie.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.crypto.CipherOutputStream;

import mequie.app.facade.exceptions.MequieException;

public class WriteInDisk {

	private File fileToWrite;
	
	/**
	 * Create a WriteInDisk to write in a location
	 * @param fileLocation the location to write
	 */
	public WriteInDisk(String fileLocation) throws IOException {
		fileToWrite = new File(fileLocation);
		fileToWrite.getParentFile().mkdirs();
	}
	
	/**
	 * Write a simple string in the file
	 * @param info the string to write in the file
	 * @return true if the string was successfully written in the file
	 */
	public boolean saveSimpleString(String info) {
		try(FileWriter writer = new FileWriter(fileToWrite, true)) {
			writer.write(info);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Write an array of bytes in the file
	 * @param data the array of bytes to write in the file
	 * @return true if the bytes was successfully written in the file
	 */
	public boolean saveBytes(byte[] data) {
		try(FileOutputStream writer = new FileOutputStream(fileToWrite)) {
			writer.write(data);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Write two strings separated by other string in the file
	 * Example: (string1 + sep + string2)
	 * @param toSave1 first string to write in the file
	 * @param toSave2 second string to write in the file
	 * @param sep the string separator
	 * @return true if the two strings were successfully written in the file separated by sep
	 */
	public boolean saveTwoStringsSeparatedBy(String toSave1, String toSave2, String sep) {
		try(FileWriter writer = new FileWriter(fileToWrite, true)) {
			writer.write(toSave1 + sep + toSave2);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean saveEncryptedListOfStringsSeparatedBy(List<String> itemsToSave, String sep) throws MequieException {
		StringBuilder sb = new StringBuilder();
		try (CipherOutputStream cos = Encryption.getCipherOutputStream(new FileOutputStream(fileToWrite))){
			for (String item : itemsToSave) {
				sb.append(item + sep);
			}
			sb.deleteCharAt(sb.length() - 1); //remove the last ':'
			cos.write(sb.toString().getBytes());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Delete the file in disk
	 * @return true if the file was successfully deleted from disk
	 */
	public boolean deleteFile() {
		return this.fileToWrite.delete();
	}
	
	/**
	 * 
	 * @return the file location in disk
	 */
	public String getFileLocation() {
		return this.fileToWrite.getPath();
	}
	
	/**
	 * Check if the file is empty
	 * @return true if the file is empty 
	 */
	public boolean emptyFile() {
		try (FileWriter writer = new FileWriter(fileToWrite)) {
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean saveEncryptedLine(String line) throws MequieException {
		try(FileWriter writer = new FileWriter(fileToWrite, true)) {
			String encryptedLine = Encryption.encryptString(line) + "\n";
			writer.write(encryptedLine);
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}

	public boolean saveEncryptedLines(List<String> linesToSave) throws MequieException {
		StringBuilder sb = new StringBuilder();
		try(FileWriter writer = new FileWriter(fileToWrite, true)) {
			for (String line : linesToSave) {
				String encryptedLine = Encryption.encryptString(line) + "\n";
				sb.append(encryptedLine);
			}
			
			writer.write(sb.toString());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
}
