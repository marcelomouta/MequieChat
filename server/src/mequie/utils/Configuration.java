package mequie.utils;

public class Configuration {
	
	private Configuration() {}
	
	private static final String DATAPATH = "Data/";
	private static final String CERTSPATH = "PubKeys/";
	private static final String GROUPFILENAME = "group.txt";
	private static final String GROUPLOCATIONKEYS_FILENAME = "keysLocation.txt";
	private static final String USERSFILE = "users.txt";
	private static final String MSG_INFO_FILENAME = "message_info.txt";
	private static final String TEXT_MSGS_FILENAME = "text_messages.txt";
	public static final String TXT_MSG_FLAG = "t";
	public static final String PHOTO_MSG_FLAG = "p";
	
	/**
	 * @return current group path in disk
	 */
	public static String getGroupPath(String groupID) {
		return DATAPATH + groupID + "/";
	}
	
	/**
	 * 
	 * @param groupID The group
	 * @return the location of the file with all locations of user keys
	 */
	public static String getLocationKeysOfGroupPath(String groupID) {
		return getGroupPath(groupID) + GROUPLOCATIONKEYS_FILENAME;
	}
	
	/**
	 * @param userID The id of the user
	 * @return the user cert path
	 */
	public static String getUserCertPath(String userID) {
		return CERTSPATH + userID + ".cert";
	}

	/**
	 * @return current group message info file path in disk
	 */
	public static String getMessageInfoPathName(String groupID) {
		return getGroupPath(groupID) + MSG_INFO_FILENAME;
	}

	/**
	 * @return current group text messages file path in disk
	 */
	public static String getTextMessagesPathName(String groupID) {
		return getGroupPath(groupID) + TEXT_MSGS_FILENAME;
	}
	
	/**
	 * @param groupID group id to which this photo belongs
	 * @param photoID photo id
	 * @return current photo file path in disk
	 */
	public static String getPhotoMsgPathName(String groupID, String photoID) {
		return getGroupPath(groupID) + photoID;
	}

	/**
	 * @return group file containing all group data in disk
	 */
	public static String getGroupPathName() {
		return DATAPATH + GROUPFILENAME;
	}
	
	/**
	 * @return users file containing all user data in disk
	 */
	public static String getUsersPathName() {
		return DATAPATH + USERSFILE;
	}
	
	/**
	 * 
	 * @param groupID
	 * @param userID
	 * @return path of user encrypted keys within the group
	 */
	public static String getLocationUserKeysOfGroupPath(String groupID, String userKeyID) {
		return getGroupPath(groupID) + userKeyID;
	}
	
}
