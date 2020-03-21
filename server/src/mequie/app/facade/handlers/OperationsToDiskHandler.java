package mequie.app.facade.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.PhotoMessage;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.utils.WriteInDisk;
import mequie.utils.Configuration;
import mequie.utils.ReadFromDisk;

public class OperationsToDiskHandler {

	private OperationsToDiskHandler() {}

	public static boolean saveTextMessageInDisk(TextMessage m, Group g) {
		try {
			
			// write in messageInfo file
			saveMessageInfoInDisk(m, Configuration.TXT_MSG_FLAG ,g);
			
			// write text message content
			WriteInDisk writer = new WriteInDisk(Configuration.getTextMessagesPathName(g.getGoupID()));
			String messageContent = m.getInfo() + "\n";
			writer.saveSimpleString(messageContent);
			

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean savePhotoMessageInDisk(byte[] data, PhotoMessage m, Group g) {
		//TODO
		try {
			// write in messageInfo file
			saveMessageInfoInDisk(m, Configuration.PHOTO_MSG_FLAG ,g);
			
			WriteInDisk writer = new WriteInDisk(Configuration.getPhotoMsgPathName(g.getGoupID(), m.getMsgID()));
			writer.saveBytes(data);

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * @param m message to write in disk
	 * @param flag message type
	 */
	private static void saveMessageInfoInDisk(Message m, String flag, Group g) throws IOException {
		
		WriteInDisk writer = new WriteInDisk(Configuration.getMessageInfoPathName(g.getGoupID()));
		String unseenUsers = m.allHaveSeenMessage() ? "" : ":" + String.join(":", m.getUsersWhoNotReadMessages());
		String messageInfo = String.join(":", m.getMsgID(), flag) + unseenUsers + "\n";
		writer.saveSimpleString(messageInfo);
	}
	
	public static boolean saveUserInDisk(User u) {
		try {
			WriteInDisk write = new WriteInDisk(Configuration.getPasswordPathName());
			write.saveTwoStringsSeparatedBy(u.getUserID(), u.getPassword() + "\n", ":");

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean saveGroupInDisk(Group g) {
		try {
			WriteInDisk write = new WriteInDisk(Configuration.getGroupPathName());
			write.saveTwoStringsSeparatedBy(g.getGoupID(), g.getOwner().getUserID() + "\n", ":");

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean saveUserToGroupInDisk(User u, Group g) {
		try {
			// ler as linhas e ver o grupo que foi alterado e adicionar ao grupo
			ReadFromDisk reader = new ReadFromDisk(Configuration.getGroupPathName());
			List<String> lines = reader.readAllLines();

			List<String> toWrite = new ArrayList<>();

			lines.stream().forEach(s -> {
				if ( s.split(":")[0].equals(g.getGoupID()) )
					toWrite.add(s + ":" + u.getUserID());
				else 
					toWrite.add(s);
			});

			WriteInDisk writer = new WriteInDisk(Configuration.getGroupPathName());
			writer.emptyFile();
			writer.saveListOfStringsSeparatedBy(toWrite, "\n");
			writer.saveSimpleString("\n");

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean saveRemoveUserFromGroup(User u, Group g) {
		try {
			// ler as linhas e ver o grupo que foi alterado e remover do disco
			ReadFromDisk reader = new ReadFromDisk(Configuration.getGroupPathName());
			List<String> lines = reader.readAllLines();

			List<String> toWrite = new ArrayList<>();

			lines.stream().forEach(s -> {
				if ( s.split(":")[0].equals(g.getGoupID()) )
					toWrite.add(s.replaceAll(":" + u.getUserID(), ""));
				else
					toWrite.add(s);
			});

			// depois escrever a alteracao ao grupo no disco
			WriteInDisk writer = new WriteInDisk(Configuration.getGroupPathName());
			writer.emptyFile();
			writer.saveListOfStringsSeparatedBy(toWrite, "\n");
			writer.saveSimpleString("\n");

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static byte[] getFileContent(String path) {
		try {
			ReadFromDisk reader = new ReadFromDisk(path, false);
			byte[] buf = reader.readAllBytes();

			return buf;
		} catch (IOException e) {
			return null;
		}
	}

	public static boolean updateSeenMessages(List<Message> toRemoveList, Group g) {
		//TODO
		try {
			for (Message toRemove : toRemoveList) {
				if (toRemove instanceof TextMessage) {
					ReadFromDisk reader = new ReadFromDisk("Data/" + g.getGoupID() + "/messages.txt");
					List<String> lines = reader.readAllLines();

					 List<String> toWrite = lines.stream().filter(s -> !s.split(":")[0].equals(toRemove.getMsgID()))
							 								.collect(Collectors.toList());

					// depois escrever a alteracao ao grupo no disco
					WriteInDisk writer = new WriteInDisk(Configuration.getGroupPathName());
					writer.emptyFile();
					writer.saveListOfStringsSeparatedBy(toWrite, "\n");

				} else if (toRemove instanceof PhotoMessage) {
					// ir buscar os bytes
					WriteInDisk writer = new WriteInDisk(Configuration.getPhotoMsgPathName(g.getGoupID(), toRemove.getMsgID()));
					if (!writer.deleteFile())
						return false;
				}
			}
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
