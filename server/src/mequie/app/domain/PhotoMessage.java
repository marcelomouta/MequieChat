package mequie.app.domain;

import java.util.List;

public class PhotoMessage extends Message {
	
	public PhotoMessage(String msgID, List<User> userNotSeenMsg) {
		super(msgID, userNotSeenMsg);
	}

	@Override
	public String toString() {
		return this.getMsgID();
	}

	@Override
	public String getInfo() {
		return toString();
	}

}
