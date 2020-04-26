package mequie.app.domain;

import java.util.List;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 *  
 * This class represents a photo message sent by an user.
 */
public class PhotoMessage extends Message {
	
	/**
	 * Creates a TextMessage
	 * @param msgID the id of the message - the name of the file in disk
	 * @param userNotSeenMsg the list of users who have not seen the message
	 */
	public PhotoMessage(String msgID, int keyID, List<User> userNotSeenMsg) {
		super(msgID, keyID, userNotSeenMsg);
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
		return this.getMsgID();
	}

}
