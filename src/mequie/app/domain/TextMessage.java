package mequie.app.domain;

public class TextMessage extends Message {
	
	private String text;
	
	public TextMessage(String msgID, User sender, String text) {
		super(msgID, sender);
		this.text = text;
	}

	@Override
	public String toString() {
		return msgID + ":" + text;
	}

}
