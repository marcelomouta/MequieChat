package mequie.app.facade.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.PhotoMessage;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.utils.WriteInDisk;
import mequie.utils.Configuration;
import mequie.utils.ReadFromDisk;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class aims to make operations of any sort to the disk
*/
public class OperationsToDiskHandler {

	private static Object groupMutex = new Object();
	private static Map<String, Object> messageInfoMutexes = new HashMap<>();
	private static Map<String, Object> textMessageMutexes = new HashMap<>();

	private OperationsToDiskHandler() {}

	/**
	 * 
	 * @param m text message
	 * @param g group to save it to
	 * @return true if it saved the text message with success
	 *         false otherwise
	 */
	public static boolean saveTextMessageInDisk(TextMessage m, Group g) {
			try {
				// write in messageInfo file
				saveMessageInfoInDisk(m, Configuration.TXT_MSG_FLAG ,g);

				synchronized(textMessageMutexes.get(g.getGoupID())) {
					// write text message content
					WriteInDisk writer = new WriteInDisk(Configuration.getTextMessagesPathName(g.getGoupID()));
					String messageContent = m.getInfo() + "\n";
					writer.saveSimpleString(messageContent);
				}

				return true;
			} catch (IOException e) {
				return false;
			}
	}
	
	/**
	 * 
	 * @param data photo bytes array
	 * @param g group to save it to
	 * @return true if it saved the photo with success
	 *         false otherwise
	 */
	public static boolean savePhotoMessageInDisk(byte[] data, PhotoMessage m, Group g) {
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
	 * Saves the message in disk
	 * @param m message to write in disk
	 * @param flag message type
	 */
	private static void saveMessageInfoInDisk(Message m, String flag, Group g) throws IOException {
		synchronized(messageInfoMutexes.get(g.getGoupID())) {
			WriteInDisk writer = new WriteInDisk(Configuration.getMessageInfoPathName(g.getGoupID()));
			String unseenUsers = m.allHaveSeenMessage() ? "" : ":" + String.join(":", m.getUsersWhoNotReadMessages());
			String messageInfo = String.join(":", m.getMsgID(), flag) + unseenUsers + "\n";
			writer.saveSimpleString(messageInfo);
		}
	}

	/**
	 * 
	 * @param u user
	 * @return true if it saved the user with success
	 *         false otherwise
	 */
	public static synchronized boolean saveUserInDisk(User u) {
		try {
			WriteInDisk write = new WriteInDisk(Configuration.getPasswordPathName());
			write.saveTwoStringsSeparatedBy(u.getUserID(), u.getPassword() + "\n", ":");

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 
	 * @param g group
	 * @return true if it saved the group with success
	 *         false otherwise
	 */
	public static boolean saveGroupInDisk(Group g) {
		initializeGroupMutexes(g);
		
		synchronized(groupMutex) {
			try {
				WriteInDisk write = new WriteInDisk(Configuration.getGroupPathName());
				write.saveTwoStringsSeparatedBy(g.getGoupID(), g.getOwner().getUserID() + "\n", ":");
				return true;
			} catch (IOException e) {
				return false;
			}
		}
	}

	/**
	 * Initialization of group mutexes
	 */
	public static void initializeGroupMutexes(Group g) {
		messageInfoMutexes.put(g.getGoupID(), new Object());
		textMessageMutexes.put(g.getGoupID(), new Object());
	}

	/**
	 * 
	 * @param u user
	 * @param g group
	 * @return true if it saved the user to group with success
	 *         false otherwise
	 */
	public static boolean saveUserToGroupInDisk(User u, Group g) {
		synchronized(groupMutex) {
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
	}

	/**
	 * 
	 * @param u user
	 * @param g group
	 * @return true if it saved the removal of the user to group with success
	 *         false otherwise
	 */
	public static boolean saveRemoveUserFromGroup(User u, Group g) {
		synchronized(groupMutex) {
			try {
				// ler as linhas e ver o grupo que foi alterado e remover do disco
				ReadFromDisk reader = new ReadFromDisk(Configuration.getGroupPathName());
				List<String> lines = reader.readAllLines();

				List<String> toWrite = replaceStringInList(lines, g.getGoupID(), ":" + u.getUserID(), "");

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
	}

	/**
	 * 
	 * @param path path to the content
	 * @return array of bytes of the content read in that path
	 */
	public static byte[] getFileContent(String path) {
		try {
			ReadFromDisk reader = new ReadFromDisk(path, false);
			byte[] buf = reader.readAllBytes();

			return buf;
		} catch (IOException e) {
			return null;
		}
	}

	public static boolean updateSeenMessages(List<Message> toRemoveList, Group g, User u) {
		//TODO
		try {
			for (Message toRemove : toRemoveList) {
				ReadFromDisk reader = new ReadFromDisk(Configuration.getMessageInfoPathName(g.getGoupID()));
				List<String> lines = reader.readAllLines();

				List<String> toWrite = replaceStringInList(lines, toRemove.getMsgID(), ":" + u.getUserID(), "");

				// depois escrever a alteracao ao grupo no disco
				WriteInDisk writer = new WriteInDisk(Configuration.getMessageInfoPathName(g.getGoupID()));
				writer.emptyFile();
				writer.saveListOfStringsSeparatedBy(toWrite, "\n");
				writer.saveSimpleString("\n");
			}

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 
	 * @param lines uma lista de Strings a verificar
	 * @param eq o primeiro elemento da lista a encontrar
	 * @param toReplace a String a fazer replace
	 * @param newString a nova String para fazer o replace
	 * @return a nova lista de Strings que ira ter substituido a String toReplace por
	 * 			newString SE o primeiro elemento da String for igual ah String eq
	 */
	private static List<String> replaceStringInList(List<String> lines, String eq, String toReplace, String newString) {
		List<String> toWrite = new ArrayList<>();

		lines.stream().forEach(s -> {
			if ( s.split(":")[0].equals(eq) )
				toWrite.add(s.replaceAll(toReplace, newString));
			else
				toWrite.add(s);
		});

		return toWrite;
	}

	public static boolean removeSeenPhotos(List<PhotoMessage> photosToRemove, Group g) {
		try {
			WriteInDisk writer;
			for (PhotoMessage photo : photosToRemove) {
				writer = new WriteInDisk(Configuration.getPhotoMsgPathName(g.getGoupID(), photo.getMsgID()));
				writer.deleteFile();
			}
			return true;
		} catch (IOException e) {
			return false;
		}

	}

}
