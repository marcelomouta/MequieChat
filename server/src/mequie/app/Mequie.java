package mequie.app;

import mequie.app.facade.handlers.AddUserToGroupHandler;
import mequie.app.facade.handlers.CollectMessagesHandler;
import mequie.app.facade.handlers.CreateGroupHandler;
import mequie.app.facade.handlers.GetGroupInfoHandler;
import mequie.app.facade.handlers.GetUserInfoHandler;
import mequie.app.facade.handlers.MessageHistoryOfGroupHandler;
import mequie.app.facade.handlers.RemoveUserOfGroupHandler;
import mequie.app.facade.handlers.SendPhotoMessageHandler;
import mequie.app.facade.handlers.SendTextMessageHandler;
import mequie.app.facade.Session;

/**
 * This class is the System class
 */
public class Mequie {
	
	/**
	 * 
	 * @return the handler to create a group
	 */
	public CreateGroupHandler getCreateGroupHandler(Session s) {
		return new CreateGroupHandler(s);
	}
	
	/**
	 * 
	 * @return the handler to add a user to a gropu
	 */
	public AddUserToGroupHandler getAddUserToGroupHandler(Session s) {
		return new AddUserToGroupHandler(s);
	}
	
	/**
	 * 
	 * @return the handler to remove a user from a group
	 */
	public RemoveUserOfGroupHandler getRemoveUserOfGroupHandler(Session s) {
		return new RemoveUserOfGroupHandler(s);
	}
	
	/**
	 * 
	 * @return the handler to collect the message of a group
	 */
	public CollectMessagesHandler getCollectMessagesHandler(Session s) {
		return new CollectMessagesHandler(s);
	}
	
	/**
	 * 
	 * @return the handler to get the information of a group
	 */
	public GetGroupInfoHandler getGetGroupInfoHandler(Session s) {
		return new GetGroupInfoHandler(s);
	}
	
	/**
	 * 
	 * @return the handler to get the information of an user
	 */
	public GetUserInfoHandler getGetUserInfoHandler(Session s) {
		return new GetUserInfoHandler(s);
	}
	
	/**
	 * 
	 * @return the handler to get the history of a group
	 */
	public MessageHistoryOfGroupHandler getMessageHistoryOfGroupHandler(Session s) {
		return new MessageHistoryOfGroupHandler(s);
	}
	
	/**
	 * 
	 * @return the handler to send a photo message in a group
	 */
	public SendPhotoMessageHandler getSendPhotoMessageHandler(Session s) {
		return new SendPhotoMessageHandler(s);
	}
	
	/**
	 * 
	 * @return the handler to send a text message in a group
	 */
	public SendTextMessageHandler getSendTextMessageHandler(Session s) {
		return new SendTextMessageHandler(s);
	}

}
