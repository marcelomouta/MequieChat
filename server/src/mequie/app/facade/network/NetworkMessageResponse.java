package mequie.app.facade.network;

import java.util.ArrayList;

/**
 * Network response message from the server containing results of the client command
 */
@SuppressWarnings("serial")
public class NetworkMessageResponse extends NetworkMessage {
	
	private String result;
	private ArrayList<String> info1;
	private ArrayList<? extends Object> info2;
	
	/** 
	 * Response message from the server
	 * @param op operation code
	 * @param result command result on the server
	 */
	public NetworkMessageResponse(Opcode op, String result) {
		super(op);
		this.result = result;
	}
	
	
	/** 
	 * Response message from the server
	 * @param op operation code
	 * @param result command result on the server
	 * @param info1 string list containing information from the operation
	 */
	public NetworkMessageResponse(Opcode op, String result, ArrayList<String> info1) {
		this(op, result);
		this.info1 = info1;
	}
	
	
	/**
	 * Response message from the server
	 * @param op operation code
	 * @param result command result on the server
	 * @param info1 string list containing information from the operation (ex: list of text messages on collect)
	 * @param info2 extra information list that might not be a string, depending on the operation (ex: list of photos on collect)
	 */
	public NetworkMessageResponse(Opcode op, String result, ArrayList<String> info1, ArrayList<? extends Object> info2) {
		this(op, result);
		this.info1 = info1;
		this.info2 = info2;
	}

	public String getResult() {
		return result;
	}
	
	
	/**
	 * @return list of messages from info1
	 */
	public ArrayList<String> getMsgs() {
		return info1;
	}
	
	
	/**
	 * @return list of extra information string list from info 2 (ex: owning groups in uinfo)
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getMsg2() {
		return (ArrayList<String>) info2;
	}
	
	
	/**
	 * @return list of photos in bytes from collect
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<byte[]> getPhotos() {
		return (ArrayList<byte[]>) info2;
	}
	
	public String toString() {
		return "--- NetworkMessageResponse ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult());
	}
}
