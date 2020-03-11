package mequie.app.network;
import java.util.ArrayList;

import mequie.app.facade.exceptions.MequieException;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public class NetworkMessageResponse extends NetworkMessage {
	
	private String result;
	private ArrayList<String> photos;
	
	public NetworkMessageResponse(Opcode op, String result) {
		super(op);
		this.result = result;
	}
	
	public NetworkMessageResponse(Opcode op, String msgs, ArrayList<String> photos) {
		this(op, msgs);
		this.photos = photos;
	}
	public String getResult() {
		return result;
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
