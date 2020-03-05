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
}
