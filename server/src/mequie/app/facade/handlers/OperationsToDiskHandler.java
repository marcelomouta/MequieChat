package mequie.app.facade.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.PhotoMessage;
import mequie.app.domain.TextMessage;
import mequie.app.domain.User;
import mequie.app.facade.exceptions.MequieException;
import mequie.utils.WriteInDisk;
import mequie.utils.Configuration;
import mequie.utils.ReadFromDisk;
import mequie.utils.ReadOperation;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class aims to make operations of any sort to the disk
*/
public class OperationsToDiskHandler {

	private static Object groupMutex = new Object();
	private static Object usersMutexes = new Object();
	// <groupID,mutex>
	private static Map<String, Object> messageInfoMutexes = new HashMap<>();
	private static Map<String, Object> textMessageMutexes = new HashMap<>();
	private static Map<String, Object> groupKeysLocationMutexes = new HashMap<>();
	// mutex for each file of the user in a group : <groupID,<userID,mutex>>
	private static Map<String, Map<String, Object>> groupUsersKeysLocationMutexes = new HashMap<>();

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

				synchronized(textMessageMutexes.get(g.getGroupID())) {
					// write text message content
					WriteInDisk writer = new WriteInDisk(Configuration.getTextMessagesPathName(g.getGroupID()));
					
					writer.saveEncryptedLine(m.getInfo());
				}

				return true;
			} catch (IOException | MequieException e) {
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

			WriteInDisk writer = new WriteInDisk(Configuration.getPhotoMsgPathName(g.getGroupID(), m.getMsgID()));
			writer.saveBytes(data);

			return true;
		} catch (IOException | MequieException e) {
			return false;
		}
	}

	/**
	 * Saves the message in disk
	 * @param m message to write in disk
	 * @param flag message type
	 * @throws MequieException 
	 */
	private static void saveMessageInfoInDisk(Message m, String flag, Group g) throws IOException, MequieException {
		synchronized(messageInfoMutexes.get(g.getGroupID())) {
			WriteInDisk writer = new WriteInDisk(Configuration.getMessageInfoPathName(g.getGroupID()));
			String unseenUsers = m.allHaveSeenMessage() ? "" : ":" + String.join(":", m.getUsersWhoNotReadMessages());
			String messageInfo = String.join(":", m.getMsgID(), flag, m.getKeyID()+"") + unseenUsers;
			writer.saveEncryptedLine(messageInfo);
		}
	}

	/**
	 * 
	 * @param u user
	 * @return true if it saved the user with success
	 *         false otherwise
	 */
	public static synchronized boolean saveUserInDisk(User u) {
		synchronized (usersMutexes) {
			try {
				WriteInDisk writer = new WriteInDisk(Configuration.getUsersPathName());
				
				String userLine = u.getUserID() + ":" + u.getPublicKey();
				return writer.saveEncryptedLine(userLine);
				
			} catch (IOException | MequieException e) {
				return false;
			}
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
				ReadFromDisk reader = new ReadFromDisk(Configuration.getGroupPathName(), ReadOperation.ENCRYPTEDFILE);
				List<String> lines = reader.readAllLines();
				lines.add(g.getGroupID() + ":" + g.getOwner().getUserID() + "\n");
				
				WriteInDisk writer = new WriteInDisk(Configuration.getGroupPathName());
				writer.saveEncryptedListOfStringsSeparatedBy(lines, "\n");
				return true;
			} catch (IOException | MequieException e) {
				return false;
			}
		}
	}

	/**
	 * Initialization of group mutexes
	 */
	public static void initializeGroupMutexes(Group g) {
		messageInfoMutexes.put(g.getGroupID(), new Object());
		textMessageMutexes.put(g.getGroupID(), new Object());
		groupKeysLocationMutexes.put(g.getGroupID(), new Object());
		groupUsersKeysLocationMutexes.put(g.getGroupID(), new HashMap<>());
		
		for (User u : g.getAllUsers()) {
			Map<String, Object> usersMutexes = groupUsersKeysLocationMutexes.get(g.getGroupID());
			usersMutexes.put(u.getUserID(), new Object());
		}
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
				ReadFromDisk reader = new ReadFromDisk(Configuration.getGroupPathName(), ReadOperation.ENCRYPTEDFILE);
				List<String> lines = reader.readAllLines();

				List<String> toWrite = new ArrayList<>();

				lines.stream().forEach(s -> {
					if ( s.split(":")[0].equals(g.getGroupID()) )
						toWrite.add(s + ":" + u.getUserID());
					else 
						toWrite.add(s);
				});

				WriteInDisk writer = new WriteInDisk(Configuration.getGroupPathName());
				writer.saveEncryptedListOfStringsSeparatedBy(toWrite, "\n");
				return true;
			} catch (IOException | MequieException e) {
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
				ReadFromDisk reader = new ReadFromDisk(Configuration.getGroupPathName(), ReadOperation.ENCRYPTEDFILE);
				List<String> lines = reader.readAllLines();

				List<String> toWrite = replaceStringInList(lines, g.getGroupID(), ":" + u.getUserID(), "");

				// depois escrever a alteracao ao grupo no disco
				WriteInDisk writer = new WriteInDisk(Configuration.getGroupPathName());
				writer.saveEncryptedListOfStringsSeparatedBy(toWrite, "\n");
				return true;
			} catch (IOException | MequieException e) {
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
			ReadFromDisk reader = new ReadFromDisk(path, ReadOperation.CLEARBYTES);
			byte[] buf = reader.readAllBytes();

			return buf;
		} catch (IOException | MequieException e) {
			return null;
		}
	}

	public static boolean updateSeenMessages(List<Message> toRemoveList, Group g, User u) {
		synchronized (messageInfoMutexes.get(g.getGroupID())) {
			try {
				ReadFromDisk reader = new ReadFromDisk(Configuration.getMessageInfoPathName(g.getGroupID()), ReadOperation.ENCRYPTEDLINES);
				List<String> lines = reader.readAllLines();
				
				for (Message toRemove : toRemoveList) {
					lines = replaceStringInList(lines, toRemove.getMsgID(), ":" + u.getUserID(), "");
				}
				
				// depois escrever a alteracao ao grupo no disco
				WriteInDisk writer = new WriteInDisk(Configuration.getMessageInfoPathName(g.getGroupID()));
				writer.emptyFile();
				writer.saveEncryptedLines(lines);
	
				return true;
			} catch (IOException | MequieException e) {
				return false;
			}
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
				writer = new WriteInDisk(Configuration.getPhotoMsgPathName(g.getGroupID(), photo.getMsgID()));
				writer.deleteFile();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static String saveUserCertificate(String username, byte[] cert) throws IOException {

			String path = Configuration.getUserCertPath(username);
			WriteInDisk writer = new WriteInDisk(path);
			writer.saveBytes(cert);

			return path;

	}
	
	/**
	 * Saves key with id given in the user key file in group
	 * @param id The id associated with the key
	 * @param key The key of the id
	 * @param u 
	 * @return The path
	 */
	public static boolean saveUserGroupKeyInDisk(byte[] key, Group g, User u, boolean isNewUser) {
		Map<String, Object> usersKeysMutexes = groupUsersKeysLocationMutexes.get(g.getGroupID());
		if (isNewUser)
			usersKeysMutexes.put(u.getUserID(), new Object());
		
		// add encrypted key to his userGroupKeys file
		try {
			int keyID = g.getCurrentKeyID();
					
			String currentUserGroupKeysPath = Configuration.getLocationUserKeysOfGroupPath(g.getGroupID(), g.getUserKeyFileName(u));
				
			synchronized(usersKeysMutexes.get(u.getUserID())) {
				WriteInDisk writerUserGroupKeys = new WriteInDisk(currentUserGroupKeysPath);
				
				//convert array of bytes to String
				String keyString = bytesToString(key);
				writerUserGroupKeys.saveTwoStringsSeparatedBy(keyID +  "", keyString, ":");
				writerUserGroupKeys.saveSimpleString("\n");
			}
			
			if (isNewUser) {
				synchronized (groupKeysLocationMutexes.get(g.getGroupID())) {
					// read all current lines of keyLocation.txt
					ReadFromDisk reader = new ReadFromDisk(Configuration.getLocationKeysOfGroupPath(g.getGroupID()), ReadOperation.ENCRYPTEDFILE);
					List<String> lines = reader.readAllLines();
					
					//update keyID
					if (!lines.isEmpty()) 
						lines.set(0, keyID+"");
					else
						lines.add(keyID+"");
					
					// "add" user:userGroupKeysFile to keyLocation.txt
					lines.add(u.getUserID() + ":" + g.getUserKeyFileName(u) + "\n");				
					
					WriteInDisk writer = new WriteInDisk(Configuration.getLocationKeysOfGroupPath(g.getGroupID()));
					writer.saveEncryptedListOfStringsSeparatedBy(lines, "\n");
				}
				
			}
			
			return true;
		} catch (IOException | MequieException e) {
			return false;
		}
	}


	public static boolean saveRemovedUserGroupKeyInDisk(byte[] key, Group g, User u, String removedUserKeyfile) {

		try {
			
			int keyID = g.getCurrentKeyID();
			
			synchronized (groupKeysLocationMutexes.get(g.getGroupID())) {
				// read all current lines of keyLocation.txt
				ReadFromDisk reader = new ReadFromDisk(Configuration.getLocationKeysOfGroupPath(g.getGroupID()), ReadOperation.ENCRYPTEDFILE);
				List<String> lines = reader.readAllLines();
	
				//update keyID
				lines.set(0, keyID+"");
	
				// remove user:userGroupKeysFile in keyLocation.txt
				lines.removeIf(s -> s.split(":")[0].equals(u.getUserID()));			
	
				WriteInDisk writer = new WriteInDisk(Configuration.getLocationKeysOfGroupPath(g.getGroupID()));
				writer.saveEncryptedListOfStringsSeparatedBy(lines, "\n");
			}
						
			//delete removed user keyfile 
			String removedUserGroupKeysPath = Configuration.getLocationUserKeysOfGroupPath(g.getGroupID(), removedUserKeyfile);
			
			synchronized (groupUsersKeysLocationMutexes.get(g.getGroupID()).get(u.getUserID())) {	
				WriteInDisk remover = new WriteInDisk(removedUserGroupKeysPath);
				remover.deleteFile();
			}

			return true;
		} catch (IOException | MequieException e) {
			return false;
		}
	}

	public static byte[] readLastUserKey(User u, Group g) {
		// add encrypted key to his userGroupKeys file
		try {
			String keyID = g.getCurrentKeyID() + "";
			
			String currentUserGroupKeysPath = Configuration.getLocationUserKeysOfGroupPath(g.getGroupID(), g.getUserKeyFileName(u));
			
			List<String> lines;
			
			synchronized (groupUsersKeysLocationMutexes.get(g.getGroupID()).get(u.getUserID())) {
				ReadFromDisk reader = new ReadFromDisk(currentUserGroupKeysPath, ReadOperation.PLAINTEXT);
				lines = reader.readAllLines();
			}
			
			String line = lines.stream().filter(s -> s.split(":")[0].equals(keyID)).findFirst().get();
			
			//convert array of bytes to String
			return stringToBytes(line.split(":")[1]);
			
		} catch (Exception e) {
			return null;
		}
	}

	public static HashMap<Integer, byte[]> getUserGroupKeys(User u, Group g) {
		try {
			String currentUserGroupKeysPath = Configuration.getLocationUserKeysOfGroupPath(g.getGroupID(), g.getUserKeyFileName(u));
			
			List<String> lines;
			
			synchronized (groupUsersKeysLocationMutexes.get(g.getGroupID()).get(u.getUserID())) {
				ReadFromDisk reader = new ReadFromDisk(currentUserGroupKeysPath, ReadOperation.PLAINTEXT);
				lines = reader.readAllLines();
			}
			
			HashMap<Integer, byte[]> userKeys = new HashMap<>();
			lines.forEach(l -> {
				String[] splittedLine = l.split(":");
				byte[] key = stringToBytes(splittedLine[1]);
				userKeys.put(Integer.parseInt(splittedLine[0]), key);
			});
			
			return userKeys;
			
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param key
	 * @return
	 */
	private static String bytesToString(byte[] bytes) {
		return DatatypeConverter.printBase64Binary(bytes);
	}

	/**
	 * @param s
	 * @return
	 */
	private static byte[] stringToBytes(String s) {
		return DatatypeConverter.parseBase64Binary(s);
	}

}
