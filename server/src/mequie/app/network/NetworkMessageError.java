package mequie.app.network;
import mequieclient.app.facade.exceptions.MequieException;
import mequieclient.app.network.NetworkMessage;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public class NetworkMessageError extends NetworkMessage {
	
	private MequieException exception;
	
	public NetworkMessageError(Opcode op, MequieException exception) {
		super(op);
		this.exception = exception;
	}
	public MequieException getException() {
		return exception;
	}
	
	public String toString() {
		return "--- NetworkMessageError ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Exception = " + this.getException().getMessage();
	}
}
