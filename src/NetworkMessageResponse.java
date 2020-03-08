import mequie.app.facade.exceptions.MequieException;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public class NetworkMessageResponse extends NetworkMessage {
	
	private String result;
	private MequieException exception;
	
	public NetworkMessageResponse(Opcode op, String result, MequieException exception) {
		super(op);
		this.result = result;
		this.exception = exception;
	}
	public String getResult() {
		return result;
	}
	public MequieException getException() {
		return exception;
	}
	
	public String toString() {
		return "--- NetworkMessageResponse ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult()) + "\n" +
			"Exception = " + this.getException().getMessage();
	}
}
