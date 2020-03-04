package mequie.app;

import mequie.app.facade.handlers.authenticateHandler;

/**
 * This class is the System class
 */
public class Mequie {
	
	public authenticateHandler getauthenticateHandler() {
		return new authenticateHandler();
	}

}
