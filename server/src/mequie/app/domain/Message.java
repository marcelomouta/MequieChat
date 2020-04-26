package mequie.app.domain;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 *  
 * This class represents a message sent by an user.
 */
public abstract class Message {
	// generic data about the message
	private String msgID;
	private int keyID;
	// Set of users that not read the message
	protected Set<User> usersWhoNotReadMessages;
	
	// Safe manipulation locks
	ReadWriteLock lock = new ReentrantReadWriteLock();
	Lock writeLock = lock.writeLock();
	Lock readLock = lock.readLock();

    /**
     *
     * @param msgID the id of the message
     * @param keyID the id of the key used to encrypt the message
     */
	protected Message(String msgID, int keyID, List<User> userNotSeenMsg) {
		this.msgID = msgID;
		this.keyID = keyID;
		this.usersWhoNotReadMessages = new HashSet<>(userNotSeenMsg);
	}

	/**
	 *
	 * @return the messageID of the message
	 */
	public String getMsgID() {
		return msgID;
	}


	/**
	 * @return the keyID
	 */
	public int getKeyID() {
		return keyID;
	}

	/**
	 *
	 * @return list of ids from users that didnt read this message
	 */
	public List<String> getUsersWhoNotReadMessages() {
		readLock.lock();
		try {
		return usersWhoNotReadMessages.stream()
									  .map(User::getUserID)
									  .collect(Collectors.toList());
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 
	 * @return true if user not read the msg
	 */
	public boolean userHasReadMessage(User u) {
		readLock.lock();
		try {
			return !usersWhoNotReadMessages.contains(u);
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * 
	 * @return true if all members of group read the message
	 */
	public boolean allHaveSeenMessage() {
		readLock.lock();
		try {
		return usersWhoNotReadMessages.isEmpty();
		} finally {
			readLock.unlock();
		}
	}
	
	/**
	 * 
	 * @param u the user read message
	 */
	public void messageReadByUser(User u) {
		writeLock.lock();
		try {
			usersWhoNotReadMessages.remove(u);
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 *
	 * @return info about the message
	 */
	public abstract String getInfo();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msgID == null) ? 0 : msgID.hashCode());
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
		Message other = (Message) obj;
		if (msgID == null) {
			if (other.msgID != null)
				return false;
		} else if (!msgID.equals(other.msgID))
			return false;
		return true;
	}

	@Override
	public abstract String toString();
}
