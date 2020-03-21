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
	private String password;
	// groups of this user
	private Set<Group> groups = new HashSet<>();
	// groups owned by this user
	private Set<Group> groupsOwned = new HashSet<>();

	// Safe manipulation locks
	// locks for groups safe manipulation
	ReadWriteLock lock = new ReentrantReadWriteLock();
	Lock groupsWriteLock = lock.writeLock();
	Lock groupsReadLock = lock.readLock();
	// No needed locks for owned groups safe manipulation
	// because it only add when the message is added. Then
	// only read operations are executed.

	/**
	 * 
	 * @param username the username of user
	 * @param pass the password of the user
	 */
	public User(String username, String pass) {
		this.userID = username; this.password = pass;
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
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @param groupID a string representing the id of the group
	 * @return a group with groupID id and this user as owner
	 */
	public Group createGroup(String groupID) {
		Group g = new Group(groupID, this);

		// add to groups of this user
		groups.add(g);

		// add to groups owned by this user
		groupsOwned.add(g);

		return g;
	}

	/**
	 * 
	 * @param g the group to be added
	 * @return true if group was successfully added to groups of the user
	 */
	public boolean addGroupToBelongedGroups(Group g) {
		groupsReadLock.lock();
		try {
			return groups.add(g);
		} finally {
			groupsReadLock.unlock();
		}
	}

	/**
	 * 
	 * @param g the group to be added
	 * @return true if group was successfully removed from groups of the user
	 */
	public boolean removeGroupFromBelongedGroups(Group g) {
		groupsReadLock.lock();
		try {
			return groups.remove(g);
		} finally {
			groupsReadLock.unlock();
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
