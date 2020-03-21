package mequie.utils;

public class Configuration {
	
	private Configuration() {}
	
	private static final String DATAPATH = "Data/";
	private static final String GROUPFILENAME = "group.txt";
	private static final String PASSWDFILE = "passwd.txt";
	private static final String MSG_INFO_FILENAME = "message_info.txt";
	private static final String TEXT_MSGS_FILENAME = "text_messages.txt";
	public static final String TXT_MSG_FLAG = "t";
	public static final String PHOTO_MSG_FLAG = "p";
	
	public static String getMessageInfoPathName(String groupID) {
		return DATAPATH + groupID + "/" + MSG_INFO_FILENAME;
	}
	
	public static String getTextMessagesPathName(String groupID) {
		return DATAPATH + groupID + "/" + TEXT_MSGS_FILENAME;
	}
	
	public static String getGroupPathName() {
		return DATAPATH + GROUPFILENAME;
	}
	
	public static String getPasswordPathName() {
		return DATAPATH + PASSWDFILE;
	}
}
