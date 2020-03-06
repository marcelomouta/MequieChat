import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import mequie.app.facade.exceptions.MequieException;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public class NetworkMessage implements Serializable{


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
	
	public enum CType {
		NORMAL,
		ERROR
	}
	
	private Opcode op;
	private CType type;
	private ArrayList<String> arguments; // Tem de ser ArrayList porque List nao eh Serializable
	private String result;
	private MequieException exception;
	
	private NetworkMessage(Opcode op, CType type, ArrayList<String> arguments, String result, MequieException exception) {
		super();
		this.op = op;
		this.type = type;
		this.arguments = arguments;
		this.result = result;
		this.exception = exception;
	}
	
	// PEDIDO factoryMethod
	public static NetworkMessage ofRequest(Opcode op, ArrayList<String> arguments) {
		return new NetworkMessage(op, CType.NORMAL, arguments, "", null);
	}
	
	// RESPOSTA factoryMethod
	public static NetworkMessage ofResponse(Opcode op, String result) {
		return new NetworkMessage(op, CType.NORMAL, null, result, null);
	}
	
	// ERRO VINDO DO SERVIDOR factoryMethod
	public static NetworkMessage ofError(Opcode op, MequieException exception) {
		return new NetworkMessage(op, CType.ERROR, null, "", exception);
	}

	public Opcode getOp() {
		return op;
	}
	public CType getType() {
		return type;
	}
	public ArrayList<String> getArguments() {
		return arguments;
	}
	public String getResult() {
		return result;
	}
	public MequieException getException() {
		return exception;
	}
	
	public String toString() {
		return "--- NetworkMessage ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"CType = " + this.getType().toString() + "\n" +
			"Arguments = " + Arrays.toString(this.getArguments().toArray()) + "\n" +
			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult());
		
	}
}
