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
 * Esta classe representa o Catalogo de Users que contem todos 
 * os users do sistema
 */
public class UserCatalog {
    private static UserCatalog INSTANCE;
    private Set<User> users = new HashSet<>();

    private UserCatalog(){
    	// TODO Apagar isto qd tiver leitura do disco a funcionar. Só foi feito por motivo de teste
    	this.users.add(new User("user01", "passwd"));
    }

    public static UserCatalog getInstance(){
		if(INSTANCE == null){
			INSTANCE = new UserCatalog();
		}
		return INSTANCE;
    }
    
    public User createUser(String username, String password) {
		return new User(username, password);
    }
    
    public boolean addUser(User u) {
        return users.add(u);
    }

    public User getUserById(String userID) {
        for (User u : users) {
            if (u.getUserID().equals(userID)) {
                return u;
            }
        }

        return null;
    }
    
    public List<User> getAllUsers() {
    	return new ArrayList<>(users);
    }
}