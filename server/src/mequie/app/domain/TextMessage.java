package mequie.app.domain;

import java.util.List;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 *  
 * This class represents a text message sent by an user.
 */
public class TextMessage extends Message {
	// generic data about the text message
	private User sender;
	private String text;
	
	/**
	 * Creates a TextMessage
	 * @param msgID the id of the message
	 * @param sender the user who sent the message
	 * @param userNotSeenMsg the list of users who have not seen the message
	 * @param text the data text of the message
	 */
	public TextMessage(String msgID, int keyID, User sender, List<User> userNotSeenMsg, String text) {
		super(msgID, keyID, userNotSeenMsg);
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
	
	/**
	 * 
	 * @return toString of the file (msgID:sender:text)
	 */
	@Override
	public String getInfo() {
		return toString();
	}

	@Override
	public String toString() {
		return getMsgID() + ":" + sender.toString() + ":" + text;
	}

}
