package mequie.app.network;
import java.util.ArrayList;

import mequieClient.app.facade.exceptions.MequieException;
import mequieClient.app.network.NetworkMessage;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public class NetworkMessageResponse extends NetworkMessage {
	
	private String result;
	private ArrayList<String> msgs;
	private ArrayList<String> photos;
	
	public NetworkMessageResponse(Opcode op, String result) {
		super(op);
		this.result = result;
	}
	
	public NetworkMessageResponse(Opcode op, String result, ArrayList<String> msgs) {
		this(op, result);
		this.msgs = msgs;
	}
	
	public NetworkMessageResponse(Opcode op, String result, ArrayList<String> msgs, ArrayList<String> photos) {
		this(op, result);
		this.msgs = msgs;
		this.photos = photos;
	}

	public String getResult() {
		return result;
	}
	
	public ArrayList<String> getMsgs() {
		return msgs;
	}
	
	public ArrayList<String> getPhotos() {
		return photos;
	}
	
	public String toString() {
		return "--- NetworkMessageResponse ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult());
	}
}
