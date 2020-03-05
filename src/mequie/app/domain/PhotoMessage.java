package mequie.app.domain;

import java.io.File;
import java.util.List;

public class PhotoMessage extends Message {
	
	private File photo;
	
	public PhotoMessage(String msgID, User sender, List<User> userNotSeenMsg, byte[] bytePhoto) {
		super(msgID, sender, userNotSeenMsg);
		photo = new File("Data/" + msgID);
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public String getInfo() {
		return photo.toString();
	}

}
