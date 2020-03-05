package mequie.app.domain;

import java.io.File;

public class PhotoMessage extends Message {
	
	private File photo;
	
	public PhotoMessage(String msgID, User sender, byte[] bytePhoto) {
		super(msgID, sender);
		photo = new File("Data/" + msgID);
	}

	@Override
	public String toString() {
		return null;
	}

}
