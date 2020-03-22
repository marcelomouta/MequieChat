package mequie.app.facade.network;

import mequie.app.facade.exceptions.MequieException;
import mequie.app.facade.network.NetworkMessage;

/**
 * Network error message from the server containing an exception
 */
@SuppressWarnings("serial")
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
