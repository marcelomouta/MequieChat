package mequie.app.network;
import java.util.ArrayList;
import java.util.Arrays;

import mequie.app.facade.exceptions.MequieException;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public class NetworkMessageRequest extends NetworkMessage {
	
	private ArrayList<String> arguments; // Tem de ser ArrayList porque List nao eh Serializable
	
	public NetworkMessageRequest(Opcode op, ArrayList<String> arguments) {
		super(op);
		this.arguments = arguments;
	}
	public ArrayList<String> getArguments() {
		return arguments;
	}
	
	public String toString() {
		return "--- NetworkMessageRequest ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Arguments = " + Arrays.toString(this.getArguments().toArray());	
	}
}
