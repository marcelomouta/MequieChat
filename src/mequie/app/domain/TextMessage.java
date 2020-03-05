package mequie.app.domain;

import java.util.List;

public class TextMessage extends Message {
	
	private String text;
	
	public TextMessage(String msgID, User sender, List<User> userNotSeenMsg, String text) {
		super(msgID, sender, userNotSeenMsg);
		this.text = text;
	}
	
	public String getInfo() {
		return text;
	}

	@Override
	public String toString() {
		return msgID + ":" + text;
	}

}
