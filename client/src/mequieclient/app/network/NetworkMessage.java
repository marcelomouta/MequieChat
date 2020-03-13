package mequieclient.app.network;

import java.io.Serializable;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
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
		TEST // TODO Apgar mais tarde
	}
	
	private Opcode op;
	
	protected NetworkMessage(Opcode op) {
		super();
		this.op = op;
	}

	public Opcode getOp() {
		return op;
	}
	
	public abstract String toString();
	
//	public String toString() {
//		return "--- NetworkMessage ---\n" +
//			"Opcode = " + this.getOp().toString() + "\n" +
//			"CType = " + this.getType().toString() + "\n" +
//			"Arguments = " + Arrays.toString(this.getArguments().toArray()) + "\n" +
//			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult());
//		
//	}
}
