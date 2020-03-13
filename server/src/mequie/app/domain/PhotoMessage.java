package mequie.app.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class PhotoMessage extends Message {
	
	private File photo;
	
	public PhotoMessage(String msgID, User sender, List<User> userNotSeenMsg, byte[] bytePhoto) {
		super(msgID, sender, userNotSeenMsg);
		photo = new File("Data/" + msgID);
	}

	@Override
	public String toString() {
		return photo.toString();
	}

	@Override
	public String getInfo() {
		StringBuilder sb = new StringBuilder();
		try(Scanner sc = new Scanner(photo)) {
			if (sc.hasNext()) {
				sb.append(sc.next());
			}
		} catch (FileNotFoundException e) {
			// TODO 
		}
		return sb.toString();
	}

}
