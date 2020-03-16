package mequie.app.facade.handlers;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.UserCatalog;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.AuthenticationFailedException;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;

public class GetUserFromSessionHandler {
	
	private GetUserFromSessionHandler() {}
	
	private static User user;
	
	public static void authenticateSession(Session possibleSession) throws AuthenticationFailedException {
		user =  UserCatalog.getInstance().getUserById(possibleSession.getUsername());
		
		if (user != null) {
			// ja existe utilizador com esse username
			if ( !user.getPassword().equals(possibleSession.getPassword()) )
				throw new AuthenticationFailedException();
		}
	}
	
	public static User getUserFromSession(Session session) {
    	// Ainda nao existe utilizador
    	if (user == null) {
    		CreateUserHandler  cuh = new CreateUserHandler();
    		
    		user = cuh.makeUser(session.getUsername(), session.getPassword());
    		// cuh.save();
    		
    	}
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
			if ( !SaveToDiskHandler.saveUserInDisk(currentUser) )
				throw new ErrorSavingInDiskException();
		}

	}

}
