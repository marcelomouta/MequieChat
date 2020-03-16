package mequie.app.network;
import java.util.ArrayList;

import mequieclient.app.network.NetworkMessage;

/**
 * Classe comum ao Servidor e Cliente para troca de pedido e reposta
 * Esta classe auto envia-se para a rede
 */
public class NetworkMessageResponse extends NetworkMessage {
	
	private String result;
	private ArrayList<String> info1;
	private ArrayList<? extends Object> info2;
	
	public NetworkMessageResponse(Opcode op, String result) {
		super(op);
		this.result = result;
	}
	
	public NetworkMessageResponse(Opcode op, String result, ArrayList<String> info1) {
		this(op, result);
		this.info1 = info1;
	}
	
	public NetworkMessageResponse(Opcode op, String result, ArrayList<String> info1, ArrayList<? extends Object> info2) {
		this(op, result);
		this.info1 = info1;
		this.info2 = info2;
	}

	public String getResult() {
		return result;
	}
	
	public ArrayList<String> getMsgs() {
		return info1;
	}
	
	public ArrayList<String> getMsg2() {
		return (ArrayList<String>) info2;
	}
	
	public ArrayList<byte[]> getPhotos() {
		return (ArrayList<byte[]>) info2;
	}
	
	public String toString() {
		return "--- NetworkMessageResponse ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult());
	}
}
