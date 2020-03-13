package mequie.app.facade.handlers;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.UserCatalog;
import mequieclient.app.facade.exceptions.ErrorSavingInDiskException;

public class CreateUserHandler {
	
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
