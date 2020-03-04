package mequie.app.domain;
/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 *  
 * Esta classe representa um utilizador do sistema.
 */
public class Message {

	private String msgID;
	private String text;

    /**
     * 
     * @param msgID - id da mensagem
     */
	public Message(String msgID, String text) {
		this.msgID = msgID;
	}

	/**
	 * 
	 * @return o username do utilizador
	 */
	public String getUserID() {
		return msgID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Message){
			Message other = (Message) obj;
			return other.msgID.equals(this.msgID);
		}
		return false;
	}

}
