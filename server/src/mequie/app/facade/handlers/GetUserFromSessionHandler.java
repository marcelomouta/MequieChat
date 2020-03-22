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

	// user in the sesion
	private static User user;

	/**
	 * Authenticates the user inside the session and if he doesn't exist creates one
	 * @param session session with the user to authenticate inside
	 * @throws AuthenticationFailedException 
	 * @throws ErrorSavingInDiskException
	 */
	public static void authenticateSession(Session session) throws AuthenticationFailedException, ErrorSavingInDiskException {
		user =  UserCatalog.getInstance().getUserById(session.getUsername());

		if (user != null) {
			// ja existe utilizador com esse username
			if ( !user.getPassword().equals(session.getPassword()) )
				throw new AuthenticationFailedException();
		} else {
			CreateUserHandler  cuh = new CreateUserHandler();

			user = cuh.makeUser(session.getUsername(), session.getPassword());
			cuh.save();
		}
	}

	/**
	 * 
	 * @param session a session to get the user inside of it
	 * @return User inside the session
	 */
	public static User getUserFromSession(Session session) {

		user =  UserCatalog.getInstance().getUserById(session.getUsername());
		return user;
	}

	// This class represents a handler to create a user
	private static class CreateUserHandler {

		// user that is using this handler
		private User currentUser;
		
		public CreateUserHandler() {}

		/**
		 * 
		 * @param username username of the user to create 
		 * @param pass password of the user to create 
		 * @return user created
		 */
		public User makeUser(String username, String pass) {
			currentUser = UserCatalog.getInstance().createUser(username, pass);
			UserCatalog.getInstance().addUser(currentUser);
			return currentUser;
		}

	    /**
	     * Saves Makes the operation persistent on disk
	     * @throws ErrorSavingInDiskException
	     */
		public void save() throws ErrorSavingInDiskException {
			if ( !OperationsToDiskHandler.saveUserInDisk(currentUser) )
				throw new ErrorSavingInDiskException();
		}


	}
}
