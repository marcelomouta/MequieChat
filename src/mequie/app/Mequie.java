package mequie.app;

import mequie.app.facade.handlers.LoadingFromDiskHandler;

/**
 * This class is the System class
 */
public class Mequie {
	
	public LoadingFromDiskHandler getauthenticateHandler() {
		return LoadingFromDiskHandler.getInstance();
	}

}
