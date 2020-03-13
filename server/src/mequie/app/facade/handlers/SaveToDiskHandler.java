package mequie.app.facade.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.utils.WriteInDisk;

public class SaveToDiskHandler {
	
	private SaveToDiskHandler() {}
	
	public static boolean saveTextMessageInDisk(Message m, Group g) {
		try {
			// prepare
			List<String> stringsToWrite = new ArrayList<String>();
			stringsToWrite.add(m.getMsgID());
			stringsToWrite.add(m.getSender().getUserID());
			stringsToWrite.add(m.getInfo());
			
			// save
			WriteInDisk writer = new WriteInDisk("Data/" + g.getGoupID() + "_msgs.txt");			
			writer.saveListOfStringsSeparatedBy(stringsToWrite, ":");
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean savePhotoMessageInDisk(Message m, Group g) {
		try {
			WriteInDisk writer = new WriteInDisk("Data/" + g.getGoupID() + "_" + m.getMsgID() + ".txt");		
			writer.saveSimpleString(m.getInfo()); //esta a escrever a path e nao o conteudo em si REVER
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveUserInDisk(User u) {
		try {
			WriteInDisk write = new WriteInDisk("Data/passwd.txt");
			write.saveTwoStringsSeparatedBy(u.getUserID(), u.getPassword(), ":");
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveGroupInDisk(Group g) {
		try {
			WriteInDisk write = new WriteInDisk("Data/group.txt");
			write.saveTwoStringsSeparatedBy(g.getGoupID(), g.getOwner().getUserID(), ":");
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveUserToGroupInDisk(User u, Group g) {
		try {
			WriteInDisk write = new WriteInDisk("Data/group.txt");
			// ir buscar toda a info e reescrever toda mas com a correcao
			write.saveStringSeparatedBy(u.getUserID(), ":");
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean saveRemoveUserFromGroup(User u, Group g) {
		try {
    		// ler as linhas e ver o grupo que foi alterado e remover do disco
    		// depois escrever a alteracao ao grupo no disco
			WriteInDisk write = new WriteInDisk("Data/group");
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
