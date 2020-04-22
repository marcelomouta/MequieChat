package mequie.app.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents a chatting group between users.
 * Can be sent text messages or photos by the users.
 * When a user is added it will only possible see the history of messages (of this
 * group) - a message goes to history (only text messages) when seen by all users.
 */
public class Group {
	// generic data about the group
	private String id;
	private User owner;
	// map with the users of this group and the name of their key files
	private Map<User,String> users = new HashMap<>();
	// the ID of the current key being used to encrypt group messages
	private int currentKeyID;
	// the messages (text or photos) that were not seen by all users
	private List<Message> messages = new ArrayList<>();
	// the messages (only text) that were seen by all users
	private List<Message> history = new ArrayList<>();

	// number to generate the id of new messages
	private int msgNumberID = 0;

	// Safe manipulation locks
	// locks for users safe manipulation
	private ReadWriteLock lock1 = new ReentrantReadWriteLock();
	private Lock usersWriteLock = lock1.writeLock();
	private Lock usersReadLock = lock1.readLock();
	// locks for messages safe manipulation
	private ReadWriteLock lock2 = new ReentrantReadWriteLock();
	private Lock messagesWriteLock = lock2.writeLock();
	private Lock messagesReadLock = lock2.readLock();
	// locks for history safe manipulation
	private ReadWriteLock lock3 = new ReentrantReadWriteLock();
	private Lock historyWriteLock = lock3.writeLock();
	private Lock historyReadLock = lock3.readLock();

	/**
	 * 
	 * @param id the id of the group
	 * @param owner the owner of the group
	 */
	public Group(String id, User owner) {
		this.id = id;
		this.owner = owner;
		this.setCurrentKeyID(0);
		this.users.put(owner, this.currentKeyID + "");
	}

	/**
	 * 
	 * @return the groupID of the group
	 */
	public String getGroupID() {
		return this.id;
	}

	/**
	 * 
	 * @return the owner of the group
	 */
	public User getOwner() {
		return this.owner;
	}

	/**
	 * 
	 * @return the number of users in the group
	 */
	public int getNumberOfUsers() {
		return this.users.size();
	}
	
	public String getUserKeyFileName(User u) {
		return this.users.get(u);
	}

	/**
	 * @return the currentKeyID
	 */
	public int getCurrentKeyID() {
		return currentKeyID;
	}

	/**
	 * @param currentKeyID the currentKeyID to set
	 */
	public void setCurrentKeyID(int currentKeyID) {
		this.currentKeyID = currentKeyID;
	}

	/**
	 * 
	 * @return a list of all users of the group
	 */
	public List<User> getAllUsers() {
		usersReadLock.lock();
		try {
			return new ArrayList<>(this.users.keySet());
		} finally {
			usersReadLock.unlock();
		}
	}

	/**
	 * 
	 * @return a list of all messages of history
	 */
	public List<Message> getHistory() {
		historyReadLock.lock();
		try {
			return new ArrayList<>(history);
		} finally {
			historyReadLock.unlock();
		}
	}

	/**
	 * 
	 * @param u the user to check 
	 * @return true if the users is a group member
	 */
	public boolean isUserOfGroup(User u) {
		usersReadLock.lock();
		try {
			return users.containsKey(u);
		} finally {
			usersReadLock.unlock();
		}
	}

	/**
	 * Set the id message generator to a number
	 * Used when the group is loaded and already exists messages
	 * @param msgNumberID the id 
	 */
	public void setMsgNumberID(int msgNumberID) {
		this.msgNumberID = msgNumberID;
	}	

	/**
	 * 
	 * @param userToAdd the user to add to this group
	 * @return true if the user was successfully added to this group
	 */
	public boolean addUserByID(User userToAdd, String userKeysPath){
		boolean doneCorrectly = false;

		// add user to users group
		usersWriteLock.lock();
		try {
			if (!users.containsKey(userToAdd)) {
				this.users.put(userToAdd, userKeysPath);
				doneCorrectly = true;
			}
				
		} finally {
			usersWriteLock.unlock();
		}

		if (!doneCorrectly) return false;

		doneCorrectly = userToAdd.addGroupToBelongedGroups(this);

		// rollback the process
		if (!doneCorrectly)
			removeUserByID(userToAdd);
		else
			currentKeyID++;

		return doneCorrectly;
	}

	/**
	 * 
	 * @param userToRemove the user to remove from this group
	 * @return true if the user was successfully removed from this group
	 */
	public String removeUserByID(User userToRemove) {
		if (userToRemove.equals(owner))
			return null;

		String removedUserKeyfile = null;

		// remove user from users group
		usersWriteLock.lock();
		try {
			removedUserKeyfile = this.users.remove(userToRemove);
		} finally {
			usersWriteLock.unlock();
		}
		if (removedUserKeyfile == null) return null;

		boolean doneCorrectly = userToRemove.removeGroupFromBelongedGroups(this);

		// rollback the process
		if (!doneCorrectly) {
			usersWriteLock.lock();
			try {
				this.users.put(userToRemove, removedUserKeyfile);
			} finally {
				usersWriteLock.unlock();
			}
		} else
			currentKeyID++;
		
		return removedUserKeyfile;
	}

	/**
	 * Save a message to the group (send message to group)
	 * @param msg the message to be saved
	 */
	public void saveMessage(Message msg) {
		messagesWriteLock.lock();
		try {
			messages.add(msg);
		} finally {
			messagesWriteLock.unlock();
		}
	}

	/**
	 * Move a message to history
	 * @param msg the message to move to history
	 */
	public void moveToHistory(Message msg) {
		// add to history
		historyWriteLock.lock();
		try {
			if (msg instanceof TextMessage) {
				this.history.add(msg);
			}
		} finally {
			historyWriteLock.unlock();
		}

		// remove from messages
		messagesWriteLock.lock();
		try {
			this.messages.remove(msg);
		} finally {
			messagesWriteLock.unlock();
		}
	}

	/**
	 * 
	 * @param text the message texts
	 * @param sender the message sender
	 * @return a new TextMessage
	 */
	public TextMessage createTextMessage(String text, User sender) {
		usersReadLock.lock();
		try {
			return new TextMessage(getGroupID() + generateMsgID(), currentKeyID, sender, new ArrayList<>(users.keySet()), text);
		} finally {
			usersReadLock.unlock();
		}
	}

	/**
	 * 
	 * @return a new PhotoMessage
	 */
	public PhotoMessage createPhotoMessage() {
		String id = getGroupID() + generateMsgID();

		usersReadLock.lock();
		try {
			return new PhotoMessage(id, currentKeyID, new ArrayList<>(users.keySet()));
		} finally {
			usersReadLock.unlock();
		}
	}

	/**
	 * 
	 * @param u the user to get the messages
	 * @return  a list of messages that was not seen by the user u
	 */
	public List<Message> collectMessagesUnseenByUser(User u) {
		List<Message> msgs = new ArrayList<>();

		messagesReadLock.lock();
		try {
			for (Message m : messages) {
				// if user has not read the message
				if (!m.userHasReadMessage(u)) {
					m.messageReadByUser(u);
					msgs.add(m);
				}
			}
		} finally {
			messagesReadLock.unlock();
		}
		return msgs;
	}

	/**
	 * 
	 * @return a new distinct msgID
	 */
	private String generateMsgID() {
		return "" + msgNumberID++;
	}

	@Override
	public String toString() {
		usersReadLock.lock();
		try {
			return id + ":" + users.toString();
		} finally {
			usersReadLock.unlock();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
