package mequie.app.domain;

import java.util.List;

public class TextMessage extends Message {
	
	private User sender;
	private String text;
	
	public TextMessage(String msgID, User sender, List<User> userNotSeenMsg, String text) {
		super(msgID, userNotSeenMsg);
		this.sender = sender;
		this.text = text;
	}
	
	/**
	 * 
	 * @return the sender of the message
	 */
	public User getSender() {
		return sender;
	}
	
	public String getInfo() {
		return toString();
	}

	@Override
	public String toString() {
		return getMsgID() + ":" + sender.toString() + ": " + text;
	}

}
