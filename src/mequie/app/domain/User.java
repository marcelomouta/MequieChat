package mequie.app.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mequie.app.domain.Group;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa um utilizador do sistema.
 */
public class User implements Serializable{

	private String userID;
    private String password;
    
    private Set<Group> groups = new HashSet<>();

    /**
     * 
     * @param username - nome do utilizador
     * @param pass - password do utilizador
     */
	public User(String username, String pass) {
		this.userID = username; this.password = pass;
	}

	/**
	 * 
	 * @return o username do utilizador
	 */
	public String getUserID() {
		return userID;
	}
	
	/**
	 * 
	 * @return a password do utilizador
	 */
	public String getPassword() {
		return password;
	}

	public Group createGroup(String groupID) {
		Group g = new Group(groupID, this);
		groups.add(g);
		return g;
	}
	
	public boolean addGroupToBelongedGroups(Group g) {
		return groups.add(g);
	}
	
	public boolean removeGroupFromBelongedGroups(Group g) {
		return groups.remove(g);
	}

	public List<Group> getAllGroups() {
		return new ArrayList<>(groups);
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
