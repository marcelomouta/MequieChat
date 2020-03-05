package mequie.app;

import mequie.app.facade.Session;
import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CreateGroupHandler;
import mequie.app.facade.handlers.CreateUserHandler;
import mequie.app.facade.handlers.LoadingFromDiskHandler;
import mequie.app.facade.handlers.RemoveUserOfGroupHandler;

/**
 * This class is the System class
 */
public class Mequie {
	
	public CreateGroupHandler getCreateGroupHandler(Session s) {
		return new CreateGroupHandler(s);
	}
	
	public AddUserToGroupHandler getAddUserToGroupHandler(Session s) {
		return new AddUserToGroupHandler(s);
	}
	
	public CreateUserHandler getCreateUserHandler() {
		return new CreateUserHandler();
	}
	
	public RemoveUserOfGroupHandler getRemoveUserOfGroupHandler(Session s) {
		return new RemoveUserOfGroupHandler(s);
	}

}
