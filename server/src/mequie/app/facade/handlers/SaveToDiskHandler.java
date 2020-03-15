package mequie.app.facade.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.utils.WriteInDisk;
import mequie.utils.ReadFromDisk;

public class SaveToDiskHandler {
	
	private static final String GROUPFILE = "Data/group.txt";
	private static final String PASSWDFILE = "Data/passwd.txt";
	
	private SaveToDiskHandler() {}
	
	public static boolean saveTextMessageInDisk(Message m, Group g) {
		try {
			// prepare
			List<String> stringsToWrite = new ArrayList<>();
			stringsToWrite.add(m.getMsgID());
			stringsToWrite.add(m.getSender().getUserID());
			stringsToWrite.add(m.getInfo() + "\n");
			
			// save
			WriteInDisk writer = new WriteInDisk("Data/" + g.getGoupID() + "/messages.txt");			
			writer.saveListOfStringsSeparatedBy(stringsToWrite, ":");
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean savePhotoMessageInDisk(Message m, Group g) {
		try {
			WriteInDisk writer = new WriteInDisk("Data/" + g.getGoupID() + "/" + m.getMsgID() + ".txt");		
			writer.saveSimpleString(m.getInfo()); //esta a escrever a path e nao o conteudo em si REVER
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveUserInDisk(User u) {
		try {
			WriteInDisk write = new WriteInDisk(PASSWDFILE);
			write.saveTwoStringsSeparatedBy(u.getUserID(), u.getPassword() + "\n", ":");
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveGroupInDisk(Group g) {
		try {
			WriteInDisk write = new WriteInDisk(GROUPFILE);
			write.saveTwoStringsSeparatedBy(g.getGoupID(), g.getOwner().getUserID() + "\n", ":");
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveUserToGroupInDisk(User u, Group g) {
		try {
			// ler as linhas e ver o grupo que foi alterado e adicionar ao grupo
    		ReadFromDisk reader = new ReadFromDisk(GROUPFILE);
			List<String> lines = reader.readAllLines();

			List<String> toWrite = new ArrayList<>();
			
			lines.stream().forEach(s -> {
				if ( s.split(":")[0].equals(g.getGoupID()) )
					toWrite.add(s + ":" + u.getUserID());
				else 
					toWrite.add(s);
			});
			
			WriteInDisk writer = new WriteInDisk(GROUPFILE);
			writer.emptyFile();
			writer.saveListOfStringsSeparatedBy(toWrite, "\n");
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveRemoveUserFromGroup(User u, Group g) {
		try {
    		// ler as linhas e ver o grupo que foi alterado e remover do disco
    		ReadFromDisk reader = new ReadFromDisk(GROUPFILE);
			List<String> lines = reader.readAllLines();
			
			List<String> toWrite = new ArrayList<>();
			
			lines.stream().forEach(s -> {
				if ( s.split(":")[0].equals(g.getGoupID()) )
					toWrite.add(s.replaceAll(u.getUserID() + ":", ""));
				else
					toWrite.add(s);
			});	
			
			// depois escrever a alteracao ao grupo no disco
			WriteInDisk writer = new WriteInDisk(GROUPFILE);
			writer.emptyFile();
			writer.saveListOfStringsSeparatedBy(toWrite, "\n");
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
