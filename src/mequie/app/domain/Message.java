package mequie.app.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 *  
 * Esta classe representa um utilizador do sistema.
 */
public abstract class Message {

	protected User sender;
	protected String msgID;
	
	protected Set<User> usersWhoNotReadMessages;

    /**
     * 
     * @param msgID - id da mensagem
     */
	protected Message(String msgID, User sender, List<User> userNotSeenMsg) {
		this.msgID = msgID;
		this.sender = sender;
		this.usersWhoNotReadMessages = new HashSet<>(userNotSeenMsg);
	}

	/**
	 * 
	 * @return o username do utilizador
	 */
	public String getMsgID() {
		return msgID;
	}
	
	public User getSender() {
		return sender;
	}
	
	/**
	 * 
	 * @return true if user not read the msg
	 */
	public boolean userHasReadMessage(User u) {
		return !usersWhoNotReadMessages.contains(u);
	}
	
	/**
	 * 
	 * user u read message
	 */
	public void messageReadByUser(User u) {
		usersWhoNotReadMessages.remove(u);
	}
	
	public abstract String getInfo();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msgID == null) ? 0 : msgID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (msgID == null) {
			if (other.msgID != null)
				return false;
		} else if (!msgID.equals(other.msgID))
			return false;
		return true;
	}

	@Override
	public abstract String toString();
}
