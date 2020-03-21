package mequie.app.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class PhotoMessage extends Message {
	
	private String path;
	
	public PhotoMessage(String msgID, List<User> userNotSeenMsg, String path) {
		super(msgID, userNotSeenMsg);
		this.path = path;
	}

	@Override
	public String toString() {
		return path;
	}

	@Override
	public String getInfo() {
		return toString();
	}

}
