package mequie.app.facade.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mequie.app.domain.Group;
import mequie.app.domain.Message;
import mequie.app.domain.User;
import mequie.app.domain.catalogs.GroupCatalog;
import mequie.app.facade.Session;
import mequie.app.facade.exceptions.ErrorSavingInDiskException;
import mequie.app.facade.exceptions.NotExistingGroupException;
import mequie.utils.WriteInDisk;

public class SendTextMessageHandler{

    private User currentUser;
    private Group currentGroup;
    private Message currentMsg;

    public SendTextMessageHandler(Session s) {
        currentUser = s.getUser();
    }
    
    public void getGroupByID(String groupID) throws NotExistingGroupException {
    	currentGroup = GroupCatalog.getInstance().getGroupByID(groupID);
    	if  (currentGroup == null) 
            throw new NotExistingGroupException();
    }
    
    public void createMessage(String text) {
    	currentMsg = currentGroup.createMessage(text, currentUser);
    }

    public void sendMessageToGroup() {
    	currentGroup.saveMessage(currentMsg);
    }
    
    public void save() throws ErrorSavingInDiskException {
    	try {
			WriteInDisk writer = new WriteInDisk("Data/" + currentGroup.getGoupID() + "_msgs.txt");
			List<String> stringsToWrite = new ArrayList<String>();
			stringsToWrite.add(currentMsg.getMsgID());
			stringsToWrite.add(currentMsg.getSender().getUserID());
			stringsToWrite.add(currentMsg.getInfo());
			
			writer.saveListOfStringsSeparatedBy(stringsToWrite, ":");
		} catch (IOException e) {
			throw new ErrorSavingInDiskException();
		}
    }

}