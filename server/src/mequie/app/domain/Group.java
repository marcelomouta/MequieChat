package mequie.app.domain;

import java.util.ArrayList;
import java.util.List;
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
	// all the users of this group
	private Set<User> users = new HashSet<>();
	// the messages (text or photos) that were not seen by all users
	private List<Message> messages = new ArrayList<>();
	// the messages (only text) that were seen by all users
	private List<Message> history = new ArrayList<>();

	// number to generate the id of new messages
	private int msgNumberID = 0;

	// Safe manipulation locks
	// locks for users safe manipulation
	ReadWriteLock lock1 = new ReentrantReadWriteLock();
	Lock usersWriteLock = lock1.writeLock();
	Lock usersReadLock = lock1.readLock();
	// locks for messages safe manipulation
	ReadWriteLock lock2 = new ReentrantReadWriteLock();
	Lock messagesWriteLock = lock2.writeLock();
	Lock messagesReadLock = lock2.readLock();
	// locks for history safe manipulation
	ReadWriteLock lock3 = new ReentrantReadWriteLock();
	Lock historyWriteLock = lock3.writeLock();
	Lock historyReadLock = lock3.readLock();

	/**
	 * 
	 * @param id the id of the group
	 * @param owner the owner of the group
	 */
	public Group(String id, User owner) {
		this.id = id;
		this.owner = owner;
		this.users.add(owner);
	}

	/**
	 * 
	 * @return the groupID of the group
	 */
	public String getGoupID() {
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

	/**
	 * 
	 * @return a list of all users of the group
	 */
	public List<User> getAllUsers() {
		usersReadLock.lock();
		try {
			return new ArrayList<>(this.users);
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
			return history;
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
			return users.contains(u);
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
	public boolean addUserByID(User userToAdd) throws Exception {
		boolean doneCorrectly = false;

		// add user to users group
		usersWriteLock.lock();
		try {
			doneCorrectly = this.users.add(userToAdd);
		} finally {
			usersWriteLock.unlock();
		}

		if (!doneCorrectly) return false;

		doneCorrectly = userToAdd.addGroupToBelongedGroups(this);

		// rollback the process
		if (!doneCorrectly) removeUserByID(userToAdd);

		return doneCorrectly;
	}

	/**
	 * 
	 * @param userToRemove the user to remove from this group
	 * @return true if the user was successfully removed from this group
	 */
	public boolean removeUserByID(User userToRemove) throws Exception{
		if (userToRemove.equals(owner))
			return false;

		boolean doneCorrectly = false;

		// remove user from users group
		usersWriteLock.lock();
		try {
			doneCorrectly = this.users.remove(userToRemove);
		} finally {
			usersWriteLock.unlock();
		}
		if (!doneCorrectly) return false;

		doneCorrectly = userToRemove.removeGroupFromBelongedGroups(this);

		// rollback the process
		if (!doneCorrectly) this.users.add(userToRemove);

		return doneCorrectly;
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
			return new TextMessage(getGoupID() + generateMsgID(), sender, new ArrayList<>(users), text);
		} finally {
			usersReadLock.unlock();
		}
	}

	/**
	 * 
	 * @return a new PhotoMessage
	 */
	public PhotoMessage createPhotoMessage() {
		String id = getGoupID() + generateMsgID();

		usersReadLock.lock();
		try {
			return new PhotoMessage(id, new ArrayList<>(users));
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
