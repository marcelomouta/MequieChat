package mequie.app.domain;

import mequie.app.domain.Group;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa um utilizador do sistema.
 */
public class User {

	private String userID;
    private String password;

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

	public Group createGroup(String groupID) {
		return new Group(groupID, this);
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
