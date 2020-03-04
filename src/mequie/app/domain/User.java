package mequie.app.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    
    private List<Group> groups = new ArrayList<>();

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

	public List<Group> getAllGroups() {
		return groups;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User){
			User other = (User) obj;
			return other.userID.equals(this.userID)
					&& other.password.equals(this.password);
		}
		return false;
	}

}
