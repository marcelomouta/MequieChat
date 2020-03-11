package mequie.app.network;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import mequie.app.facade.exceptions.MequieException;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public abstract class NetworkMessage implements Serializable{


	public enum Opcode {
		ADD_USER_TO_GROUP,
		COLLECT_NOT_VIEWED_MESSAGES_OF_GROUP,
		CREATE_GROUP,
		GET_USER_INFO,
		MESSAGE_HISTORY_OF_GROUP,
		SEND_PHOTO_MESSAGE,
		SEND_TEXT_MESSAGE,
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
