import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/*
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 */
public class Packet implements Serializable {
	enum Opcode {
		ADD_USER_TO_GROUP,
		COLLECT_NOT_VIEWED_MESSAGES_OF_GROUP,
		CREATE_GROUP,
		GET_USER_INFO,
		MESSAGE_HISTORY_OF_GROUP,
		SEND_PHOTO_MESSAGE,
		SEND_TEXT_MESSAGE
	}
	
	enum CType {
		NORMAL,
		ERROR
	}
	
	private Opcode op;
	private CType type;
	private List<?> arguments;
	private Optional<?> result;
	
	// PEDIDO
	public Packet(Opcode op, CType type, List<?> arguments) {
		super();
		this.op = op;
		this.type = type;
		this.arguments = arguments;
		this.result = Optional.empty();
	}
	
	//RESPOSTA
	public Packet(Opcode op, CType type, Optional<?> result) {
		super();
		this.op = op;
		this.type = type;
		this.arguments = null;
		this.result = result;
	}
	
	public Opcode getOp() {
		return op;
	}
	public CType getType() {
		return type;
	}
	public List<?> getArguments() {
		return arguments;
	}
	public Optional<?> getResult() {
		return result;
	}
}
