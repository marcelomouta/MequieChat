package mequie.app.facade.handlers;

import mequie.app.domain.User;
import mequie.app.domain.catalogs.UserCatalog;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;

public class CreateUserHandler {
	
	private User currentUser;
	
	public CreateUserHandler() {}
	
	public void makeUser(String username, String pass) {
		currentUser = UserCatalog.getInstance().createUser(username, pass);
	}
	
	public void associaUser() {
		UserCatalog.getInstance().addUser(currentUser);
	}
	
	public void save() throws ErrorSavingInDiskException {
		if ( !SaveToDiskHandler.saveUserInDisk(currentUser) )
			throw new ErrorSavingInDiskException();
	}

}
