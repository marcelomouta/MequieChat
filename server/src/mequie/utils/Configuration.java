package mequie.utils;

public class Configuration {
	
	private Configuration() {}
	
	private static final String DATAPATH = "Data/";
	private static final String GROUPFILENAME = "group.txt";
	private static final String PASSWDFILE = "passwd.txt";
	private static final String MSG_USERS_FILENAME = "messages_users.txt";
	private static final String MSG_CONTENTS_FILENAME = "messages_contents.txt";
	
	public static String getMessageUsersPathName(String groupID) {
		return DATAPATH + groupID + MSG_USERS_FILENAME;
	}
	
	public static String getMessageContentsPathName(String groupID) {
		return DATAPATH + groupID + MSG_CONTENTS_FILENAME;
	}
	
	public static String getGroupPathName() {
		return DATAPATH + GROUPFILENAME;
	}
	
	public static String getPasswordPathName() {
		return DATAPATH + PASSWDFILE;
	}
}
