package mequie.app.domain.catalogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mequie.app.domain.User;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents a Group Catalog that contains all users
 * of the system
 */
public class UserCatalog {
    private static UserCatalog INSTANCE;
    
    // all users of thhe system
    private Set<User> users = new HashSet<>();

    private UserCatalog(){}

    /**
     * 
     * @retrun the instance of this class
     */
    public static UserCatalog getInstance(){
		if(INSTANCE == null){
			INSTANCE = new UserCatalog();
		}
		return INSTANCE;
    }
    
    /**
     * 
     * @param username The username of the user
     * @param publicKey The path of the users public key
     * @return new User with username username and password passwd
     */
    public User createUser(String username, String publicKey) {
		return new User(username, publicKey);
    }
    
    /**
     * 
     * @param user the user to be added
     * @return true if user was successfully added to the catalog
     */
    public boolean addUser(User u) {
        return users.add(u);
    }

    /**
     * 
     * @param userID the userID of the possible user
     * @return the user if a user with userID id exists or null
     */
    public User getUserById(String userID) {
        for (User u : users) {
            if (u.getUserID().equals(userID)) {
                return u;
            }
        }

        return null;
    }
    
    /**
     * 
     * @return a list of all users
     */
    public List<User> getAllUsers() {
    	return new ArrayList<>(users);
    }
}