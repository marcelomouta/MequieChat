package mequie.app.facade.handlers;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.UserCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.AuthenticationFailedException;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;

public class GetUserFromSessionHandler {

	private GetUserFromSessionHandler() {}

	private static User user;

	public static void authenticateSession(Session possibleSession) throws AuthenticationFailedException, ErrorSavingInDiskException {
		user =  UserCatalog.getInstance().getUserById(possibleSession.getUsername());

		if (user != null) {
			// ja existe utilizador com esse username
			if ( !user.getPassword().equals(possibleSession.getPassword()) )
				throw new AuthenticationFailedException();
		} else {
			CreateUserHandler  cuh = new CreateUserHandler();

			user = cuh.makeUser(possibleSession.getUsername(), possibleSession.getPassword());
			cuh.save();
		}
	}

	public static User getUserFromSession(Session session) {

		user =  UserCatalog.getInstance().getUserById(session.getUsername());
		return user;
	}

	private static class CreateUserHandler {

		private User currentUser;

		public CreateUserHandler() {}

		public User makeUser(String username, String pass) {
			currentUser = UserCatalog.getInstance().createUser(username, pass);
			UserCatalog.getInstance().addUser(currentUser);
			return currentUser;
		}

		public void save() throws ErrorSavingInDiskException {
			if ( !OperationsToDiskHandler.saveUserInDisk(currentUser) )
				throw new ErrorSavingInDiskException();
		}


	}
}
