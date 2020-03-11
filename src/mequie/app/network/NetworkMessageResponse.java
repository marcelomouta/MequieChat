package mequie.app.network;
import mequie.app.facade.exceptions.MequieException;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public class NetworkMessageResponse extends NetworkMessage {
	
	private String result;
	
	public NetworkMessageResponse(Opcode op, String result) {
		super(op);
		this.result = result;
	}
	public String getResult() {
		return result;
	}
	
	public String toString() {
		return "--- NetworkMessageResponse ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult());
	}
}
