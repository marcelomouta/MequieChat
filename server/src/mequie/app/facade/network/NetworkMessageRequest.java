package mequie.app.facade.network;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Network message with client command request to the server
 */
@SuppressWarnings("serial")
public class NetworkMessageRequest extends NetworkMessage {
	
	private ArrayList<String> arguments; // Tem de ser ArrayList porque List nao eh Serializable
	private byte[] photo;
	
	public NetworkMessageRequest(Opcode op, ArrayList<String> arguments) {
		super(op);
		this.arguments = arguments;
	}
	
	public NetworkMessageRequest(Opcode op, ArrayList<String> arguments, byte[] photo) {
		super(op);
		this.arguments = arguments;
		this.photo = photo;
	}
	
	public byte[] getPhoto() {
		return this.photo;
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
