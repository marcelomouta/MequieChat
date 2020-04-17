package mequie.app.facade.handlers;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.AuthenticationFailedException;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;

/**
* @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
* 
* This class represents a handler to get a user from a session
*/
public class GetUserFromSessionHandler {

	private GetUserFromSessionHandler() {}
	
	/**
	 * Checks if a given user exists
	 * @param userID The username of the user
	 * @return true if the user exist on the system, false otherwise
	 */
	public static boolean userExists(String userID) {
		return UserCatalog.getInstance().getUserById(userID) != null;
	}

	/**
	 * Authenticates the user inside the session and if he doesn't exist creates one
	 * @param session session with the user to authenticate inside
	 * @throws AuthenticationFailedException 
	 * @throws ErrorSavingInDiskException
	 */
	public static void authenticateSession(Session session) throws AuthenticationFailedException, ErrorSavingInDiskException {
		User user =  UserCatalog.getInstance().getUserById(session.getUsername());

		if (user != null) {
			// ja existe utilizador com esse username
			if ( !user.getPassword().equals(session.getPublicKey()) )
				throw new AuthenticationFailedException();
			// nao é preciso fazer pois o utilizador já foi
			// autenticado com sucesso com o seu certificado
			// foi visto que era ele por ter mandado corretamente o nonce
			
		} else {
			CreateUserHandler  cuh = new CreateUserHandler();

			cuh.createNewUser(session.getUsername(), session.getPublicKey());
		}
	}

	/**
	 * 
	 * @param session a session to get the user inside of it
	 * @return User inside the session
	 */
	public static User getUserFromSession(Session session) {
		return UserCatalog.getInstance().getUserById(session.getUsername());
	}

	// This class represents a handler to create a user
	private static class CreateUserHandler {
		
		public CreateUserHandler() {}

		/**
		 * Creates a new User and saves it on the disk
		 * @param username username of the user to create 
		 * @param pass password of the user to create 
	     * @throws ErrorSavingInDiskException
		 */
		public void createNewUser(String username, String password) throws ErrorSavingInDiskException {
			User currentUser = UserCatalog.getInstance().createUser(username, password);
			UserCatalog.getInstance().addUser(currentUser);
			
			if ( !OperationsToDiskHandler.saveUserInDisk(currentUser) )
				throw new ErrorSavingInDiskException();
			
		}

	}
}
