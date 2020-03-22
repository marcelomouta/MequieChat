package mequieclient.app.network;

import java.io.Serializable;

/**
 * Common class to client and server to be sent through the network
 */
public abstract class NetworkMessage implements Serializable{


	public enum Opcode {
		CREATE_GROUP,
		ADD_USER_TO_GROUP,
		REMOVE_USER_FROM_GROUP,
		GET_GROUP_INFO,
		GET_USER_INFO,
		SEND_TEXT_MESSAGE,
		SEND_PHOTO_MESSAGE,
		COLLECT_NOT_VIEWED_MESSAGES_OF_GROUP,
		MESSAGE_HISTORY_OF_GROUP,
		AUTH
	}
	
	private Opcode op;
	
	/** Network Message with current operation 
	 * @param op operation code
	 */
	protected NetworkMessage(Opcode op) {
		super();
		this.op = op;
	}

	public Opcode getOp() {
		return op;
	}
	
	public abstract String toString();
	
}
