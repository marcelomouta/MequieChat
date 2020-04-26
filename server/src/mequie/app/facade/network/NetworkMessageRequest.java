package mequie.app.facade.network;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Network message with client command request to the server
 */
@SuppressWarnings("serial")
public class NetworkMessageRequest extends NetworkMessage {
	
	private ArrayList<String> arguments; // Tem de ser ArrayList porque List nao eh Serializable
	private byte[] photo;
	/**
	 * List with entries of <userID,userWrappedKey> for each user in the group
	 */
	private ArrayList<SimpleEntry<String,byte[]>> usersGroupKeys;
	
	public NetworkMessageRequest(Opcode op, ArrayList<String> arguments) {
		super(op);
		this.arguments = arguments;
	}
	
	public NetworkMessageRequest(Opcode op, ArrayList<String> arguments, byte[] photo) {
		super(op);
		this.arguments = arguments;
		this.photo = photo;
	}
	
	/**
	 * @param op
	 * @param arguments
	 * @param usersGroupKeys
	 */
	public NetworkMessageRequest(Opcode op, ArrayList<String> arguments, ArrayList<SimpleEntry<String,byte[]>> usersGroupKeys) {
		super(op);
		this.arguments = arguments;
		this.usersGroupKeys = usersGroupKeys;
	}

	public byte[] getPhoto() {
		return this.photo;
	}
	
	public ArrayList<String> getArguments() {
		return arguments;
	}
	
	/**
	 * @return the usersGroupKeys
	 */
	public ArrayList<SimpleEntry<String,byte[]>> getUsersGroupKeys() {
		return usersGroupKeys;
	}

	public String toString() {
		return "--- NetworkMessageRequest ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Arguments = " + Arrays.toString(this.getArguments().toArray());	
	}
}
