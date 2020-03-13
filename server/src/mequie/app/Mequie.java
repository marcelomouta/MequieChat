package mequie.app;

import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CollectMessagesHandler;
import mequie.app.facade.handlers.CreateGroupHandler;
import mequie.app.facade.handlers.CreateUserHandler;
import mequie.app.facade.handlers.GetGroupInfoHandler;
import mequie.app.facade.handlers.GetUserInfoHandler;
import mequie.app.facade.handlers.MessageHistoryOfGroupHandler;
import mequie.app.facade.handlers.RemoveUserOfGroupHandler;
import mequie.app.facade.handlers.SendPhotoMessageHandler;
import mequie.app.facade.handlers.SendTextMessageHandler;
import mequieClient.app.facade.Session;

/**
 * This class is the System class
 */
public class Mequie {
	
	public CreateGroupHandler getCreateGroupHandler(Session s) {
		return new CreateGroupHandler(s);
	}
	
	public AddUserToGroupHandler getAddUserToGroupHandler(Session s) {
		return new AddUserToGroupHandler(s);
	}
	
	public CreateUserHandler getCreateUserHandler() {
		return new CreateUserHandler();
	}
	
	public RemoveUserOfGroupHandler getRemoveUserOfGroupHandler(Session s) {
		return new RemoveUserOfGroupHandler(s);
	}
	
	public CollectMessagesHandler getCollectMessagesHandler(Session s) {
		return new CollectMessagesHandler(s);
	}
	
	public GetGroupInfoHandler getGetGroupInfoHandler(Session s) {
		return new GetGroupInfoHandler(s);
	}
	
	public GetUserInfoHandler getGetUserInfoHandler(Session s) {
		return new GetUserInfoHandler(s);
	}
	
	public MessageHistoryOfGroupHandler getMessageHistoryOfGroupHandler(Session s) {
		return new MessageHistoryOfGroupHandler(s);
	}
	
	public SendPhotoMessageHandler getSendPhotoMessageHandler(Session s) {
		return new SendPhotoMessageHandler(s);
	}
	
	public SendTextMessageHandler getSendTextMessageHandler(Session s) {
		return new SendTextMessageHandler(s);
	}

}
