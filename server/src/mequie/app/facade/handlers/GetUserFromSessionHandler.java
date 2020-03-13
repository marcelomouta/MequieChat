package mequie.app.facade.handlers;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.handlers.CreateUserHandler;
import mequieclient.app.facade.Session;
import mequieclient.app.facade.exceptions.AuthenticationFailedException;

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

}
