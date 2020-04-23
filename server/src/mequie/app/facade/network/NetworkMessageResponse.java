package mequie.app.facade.network;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Network response message from the server containing results of the client command
 */
@SuppressWarnings("serial")
public class NetworkMessageResponse extends NetworkMessage {
	
	private String result;
	private ArrayList<? extends Object> info1;
	private ArrayList<? extends Object> info2;
	private HashMap<Integer, byte[]> userKeys;
	
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
	public NetworkMessageResponse(Opcode op, String result, ArrayList<? extends Object> info1, ArrayList<? extends Object> info2) {
		this(op, result);
		this.info1 = info1;
		this.info2 = info2;
	}
	
	public NetworkMessageResponse(Opcode op, String result, ArrayList<SimpleEntry<Integer, String>> msgs,
			HashMap<Integer, byte[]> userKeys) {
		this(op, result);
		this.info1 = msgs;
		this.userKeys = userKeys;
	}

	/**
	 * @param op
	 * @param result
	 * @param msgs
	 * @param photos
	 * @param userKeys
	 */
	public NetworkMessageResponse(Opcode op, String result, ArrayList<SimpleEntry<Integer, String>> msgs, ArrayList<SimpleEntry<Integer, byte[]>> photos,
			HashMap<Integer, byte[]> userKeys) {
		this(op, result, msgs, photos);
		this.userKeys = userKeys;
	}




	public String getResult() {
		return result;
	}
	
	
	/**
	 * @return list of messages from info1
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getMsgs() {
		return (ArrayList<String>) info1;
	}
	
	
	/**
	 * @return list of extra information string list from info 2 (ex: owning groups in uinfo)
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> getMsg2() {
		return (ArrayList<String>) info2;
	}
	
	/**
	 * @return list of messages from collect with its keyID
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<SimpleEntry<Integer, String>> getTextMsgs() {
		return (ArrayList<SimpleEntry<Integer, String>>) info1;
	}
	
	
	/**
	 * @return list of photos in bytes from collect with its respective keyID
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<SimpleEntry<Integer, byte[]>> getPhotos() {
		return (ArrayList<SimpleEntry<Integer, byte[]>>) info2;
	}
	
	/**
	 * @return the key of the group encrypted
	 */
	public byte[] getKeyOfGroup() {
		return (byte[]) info2.get(0);
	}
	
	/**
	 * @return the userKeys
	 */
	public HashMap<Integer, byte[]> getUserKeys() {
		return userKeys;
	}
	
	
	public String toString() {
		return "--- NetworkMessageResponse ---\n" +
			"Opcode = " + this.getOp().toString() + "\n" +
			"Result = " + (this.getResult().equals("") ? "\"\"" : this.getResult());
	}
}
