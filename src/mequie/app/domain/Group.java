package mequie.app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa um video. Um video tanto pode ser uma Stream como um Clip.
 */
public class Group {

	private String id;
	private User owner;
	
	//private List<User> users = new ArrayList<>();
	private Set<User> users = new HashSet<>();
	
	/**
	 * 
	 * @param owner - dono do video
	 * <p>
	 * Define o dono do video
	 */
	public Group(String id, User owner) {
		this.id = id;
		this.owner = owner;
		this.users.add(owner);
	}

	public String getGoupID() {
		return this.id;
	}

	public User getOwner() {
		return this.owner;
	}

	public int getNumberOfUsers() {
		return this.users.size();
	}

	public List<User> getAllUsers() {
		return new ArrayList<>(this.users);
	}

	public boolean addUserByID(User userToAdd) throws Exception {
		boolean doneCorrectly = this.users.add(userToAdd);
		
		if (!doneCorrectly) return false;
		
		doneCorrectly = userToAdd.addGroupToBelongedGroups(this);
		
		// reverter o processo
		if (!doneCorrectly) removeUserByID(userToAdd);

		return doneCorrectly;
	}

	public boolean removeUserByID(User userToRemove) throws Exception{
		boolean doneCorrectly = this.users.remove(userToRemove);
		
		if (!doneCorrectly) return false;
		
		doneCorrectly = userToRemove.removeGroupFromBelongedGroups(this);
		
		// reverter o processo
		if (!doneCorrectly) this.users.add(userToRemove);
		
		return doneCorrectly;
	}
	
	@Override
	public String toString() {
		return id + ":" + users.toString();
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
