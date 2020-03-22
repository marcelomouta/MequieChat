package mequieclient.app.network;

import mequieclient.app.facade.exceptions.MequieException;

/**
 * Network message describing an error (common to client and server)
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
