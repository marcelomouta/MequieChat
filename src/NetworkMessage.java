import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
	
	private NetworkMessage(Opcode op, CType type, ArrayList<String> arguments, String result) {
		super();
		this.op = op;
		this.type = type;
		this.arguments = arguments;
		this.result = result;
	}
	
	// PEDIDO factoryMethod
	public static NetworkMessage ofRequest(Opcode op, CType type, ArrayList<String> arguments) {
		return new NetworkMessage(op, type, arguments, "");
	}
	
	// RESPOSTA factoryMethod
	public static NetworkMessage ofResponse(Opcode op, CType type, String result) {
		return new NetworkMessage(op, type, null, result);
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
	
	public String toString() {
		return "--- NetworkMessage ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"CType = " + this.getType().toString() + "\n" +
			"Arguments = " + Arrays.toString(this.getArguments().toArray()) + "\n" +
			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult());
		
	}
}
