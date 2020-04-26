package mequie.app.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import mequie.app.domain.Group;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an user of the sistem.
 */
public class User {
	// generic data about the user
	private String userID;
	private String publicKey;
	// groups of this user
	private Set<Group> groups = new HashSet<>();
	// groups owned by this user
	private Set<Group> groupsOwned = new HashSet<>();

	// Safe manipulation locks
	// locks for groups safe manipulation
	private ReadWriteLock lock1 = new ReentrantReadWriteLock();
	private Lock groupsWriteLock = lock1.writeLock();
	private Lock groupsReadLock = lock1.readLock();
	// locks for owned groups safe manipulation
	private ReadWriteLock lock2 = new ReentrantReadWriteLock();
	private Lock ownedGroupsWriteLock = lock2.writeLock();
	private Lock ownedGroupsReadLock = lock2.readLock();

	/**
	 * 
	 * @param username the username of user
	 * @param pass the password of the user
	 */
	public User(String username, String publickey) {
		this.userID = username; this.publicKey = publickey;
	}

	/**
	 * 
	 * @return the username of user
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * 
	 * @return the password of the user
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * 
	 * @param groupID a string representing the id of the group
	 * @param ownerKeysPath 
	 * @return a group with groupID id and this user as owner
	 */
	public Group createGroup(String groupID) {
		return new Group(groupID, this);
	}

	/**
	 * 
	 * @param g the group to be added
	 * @return true if group was successfully added to owned groups of the user
	 */
	public boolean addGroupToOwnededGroups(Group g) {
		ownedGroupsWriteLock.lock();
		try {
			return groupsOwned.add(g);
		} finally {
			ownedGroupsWriteLock.unlock();
		}
	}

	/**
	 * 
	 * @param g the group to be added
	 * @return true if group was successfully added to groups of the user
	 */
	public boolean addGroupToBelongedGroups(Group g) {
		groupsWriteLock.lock();
		try {
			return groups.add(g);
		} finally {
			groupsWriteLock.unlock();
		}
	}

	/**
	 * 
	 * @param g the group to be added
	 * @return true if group was successfully removed from groups of the user
	 */
	public boolean removeGroupFromBelongedGroups(Group g) {
		groupsWriteLock.lock();
		try {
			return groups.remove(g);
		} finally {
			groupsWriteLock.unlock();
		}
	}

	/**
	 *
	 * @return a list of groups the user belongs to
	 */
	public List<Group> getAllGroups() {
		groupsReadLock.lock();
		try {
			return new ArrayList<>(groups);
		} finally {
			groupsReadLock.unlock();
		}
	}

	/**
	 *
	 * @return a list of groups the user owns to
	 */
	public List<Group> getGroupsWhoUserIsLeader() {
		return new ArrayList<>(groupsOwned);
	}

	@Override
	public String toString() {
		return this.userID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
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
		User other = (User) obj;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}

}
