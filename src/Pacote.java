import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/*
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 */
public class Pacote implements Serializable {
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
