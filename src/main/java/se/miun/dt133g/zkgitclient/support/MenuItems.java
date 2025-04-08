package se.miun.dt133g.zkgitclient.support;

/**
 * Utility class that defines constants used for formatting and displaying menu items,
 * status messages, headers, prompts, and other user interface-related elements.
 * This class cannot be instantiated as it only contains static final constants
 * and its constructor is private to prevent instantiation.
 * @author Leif Rogell
 */
public final class MenuItems {

    private MenuItems() {
        throw new IllegalStateException("Utility class");
    }

    // Formatting
    public static final int COLUMN_WIDTH_STATUS = 28;

    public static final int COLUMN_WIDTH_ACCOUNT_DATA = 20;

    public static final int COLUMN_WIDTH_MENU = 4;

    public static final int COLUMN_WIDTH_REPONAME = 35;

    public static final int COLUMN_WIDTH_SIZE = 12;

    public static final String FORMAT_REPO_LIST = "%%-%ds%%-%ds%%-%ds%%s%n";

    public static final String FORMAT_USER_LIST = "%%-%ds%%-%ds%%s%n";

    public static final String FORMAT_ACCOUNT_DATA = "%%-%ds%%s%n";

    public static final String FORMAT_DISPLAY_STATUS = "%%-%ds%%s%n";

    public static final String FORMAT_DISPLAY_MENU = "%%-%ds%%s%n";

    // Headers
    public static final String HEADER_ZKGIT = AppConfig.NEW_LINE.repeat(3)
        +  "    _/_/_/_/_/  _/    _/        _/_/_/  _/    _/\n"
        +   "         _/    _/  _/        _/            _/_/_/_/\n"
        + "      _/      _/_/          _/  _/_/  _/    _/\n"
        + "   _/        _/  _/        _/    _/  _/    _/\n"
        + "_/_/_/_/_/  _/    _/        _/_/_/  _/      _/_/"
        + AppConfig.NEW_LINE;

    public static final String HEADER_STATUS = AppConfig.NEW_LINE.repeat(2)
        + "*** STATUS ***" + AppConfig.NEW_LINE;

    public static final String HEADER_MENU = AppConfig.NEW_LINE.repeat(2)
        + "*** MENU ***" + AppConfig.NEW_LINE;

    public static final String HEADER_ACCOUNT_DATA = AppConfig.NEW_LINE.repeat(2)
        + "*** ACCOUNT DATA ***" + AppConfig.NEW_LINE;

    public static final String HEADER_USER_LIST = AppConfig.NEW_LINE.repeat(2)
        + "*** USER LIST ***" + AppConfig.NEW_LINE;

    public static final String HEADER_REPO_LIST = AppConfig.NEW_LINE.repeat(2)
        + "*** REPO LIST ***" + AppConfig.NEW_LINE;

    public static final String HEADER_NEW_USER = AppConfig.NEW_LINE.repeat(2)
        + "*** NEW USER ***" + AppConfig.NEW_LINE;

    public static final String HEADER_EXPORT_KEYS = AppConfig.NEW_LINE.repeat(2)
        + "*** EXPORT ENCRYPTION KEYS ***" + AppConfig.NEW_LINE;

    // Infos
    public static final String INFO_EXPORT_KEYS = AppConfig.NEW_LINE
        + "Due to zero-knowledge encryption, ZK Git does not offer"
        + AppConfig.NEW_LINE
        + "a password reset function. To ensure you can recover your"
        + AppConfig.NEW_LINE
        + "data in case of password loss, you must export and save your"
        + AppConfig.NEW_LINE
        + "private encryption keys. Please store your encryption keys"
        + AppConfig.NEW_LINE
        + "securely, as anyone with access to them could potentially"
        + AppConfig.NEW_LINE
        + "decrypt your data." + AppConfig.NEW_LINE;

    public static final String INFO_UNDO = AppConfig.NEW_LINE + "### WARNING: This action cannot be undone. ###";

    public static final String INFO_ACCESS_CODE = AppConfig.NEW_LINE.repeat(2)
        + "Give this access code and username to the new user and"
        + AppConfig.NEW_LINE
        + "go to www.zkgit.net/createnewuser.html to generate new"
        + AppConfig.NEW_LINE
        + "private encryption keys and a personal password."
        + AppConfig.NEW_LINE.repeat(2)
        + "### Warning: This access code cannot be displayed again."
        + AppConfig.NEW_LINE
        + "If it is lost before the encryption keys has been generated,"
        + AppConfig.NEW_LINE
        + "delete the user and create a new one. ###" + AppConfig.NEW_LINE;

    // Menu prompt
    public static final String PROMPT = "Enter your choice: ";

    public static final String PROMPT_CONFIRM = "Do you want to proceed? (Y)es/(N)o: ";

    // Menu numbers and letters
    public static final String ITEM_ONE = "1.";

    public static final String ITEM_TWO = "2.";

    public static final String ITEM_THREE = "3.";

    public static final String ITEM_FOUR = "4.";

    public static final String ITEM_FIVE = "5.";

    public static final String ITEM_SIX = "6.";

    public static final String ITEM_SEVEN = "7.";

    public static final String ITEM_EIGHT = "8.";

    public static final String ITEM_NINE = "9.";

    public static final String ITEM_TEN = "10.";

    public static final String ITEM_ELEVEN = "11.";

    public static final String ITEM_TWELVE = "12.";

    public static final String ITEM_THIRTEEN = "13.";

    public static final String ITEM_FOURTEEN = "14.";

    public static final String ITEM_FIFTEEN = "15.";

    public static final String ITEM_ZERO = "0.";

    public static final String ITEM_E = "E.";

    // Menu choices
    public static final String CHOICE_ONE = "1";

    public static final String CHOICE_TWO = "2";

    public static final String CHOICE_THREE = "3";

    public static final String CHOICE_FOUR = "4";

    public static final String CHOICE_FIVE = "5";

    public static final String CHOICE_SIX = "6";

    public static final String CHOICE_SEVEN = "7";

    public static final String CHOICE_EIGHT = "8";

    public static final String CHOICE_NINE = "9";

    public static final String CHOICE_TEN = "10";

    public static final String CHOICE_ELEVEN = "11";

    public static final String CHOICE_TWELVE = "12";

    public static final String CHOICE_THIRTEEN = "13";

    public static final String CHOICE_FOURTEEN = "14";

    public static final String CHOICE_FIFTEEN = "15";

    public static final String CHOICE_ZERO = "0";

    public static final String CHOICE_E = "E";

    // Menu items
    public static final String ITEM_LOGIN = "Login";

    public static final String ITEM_LOGOUT = "Logout";

    public static final String ITEM_STATUS = "View Status";

    public static final String ITEM_ACCOUNT_DATA = "View Account Data";

    public static final String ITEM_CHANGE_PORT = "Change Port";

    public static final String ITEM_LIST_USER = "List Users";

    public static final String ITEM_ADD_USER = "Create New User";

    public static final String ITEM_DELETE_USER = "Delete User";

    public static final String ITEM_LIST_REPOS = "List Repos";

    public static final String ITEM_MODIFY_USER = "Modify User Role";

    public static final String ITEM_CHANGE_PASSWORD = "Change Password";

    public static final String ITEM_DELETE_REPO = "Delete Repo";

    public static final String ITEM_CLOSE = "Close Application";

    public static final String ITEM_STOP_EXIT = "Stop & Exit";

    public static final String ITEM_EXPORT_KEYS = "Export Encryption Keys";

    public static final String ITEM_LIST_PAYMENTS = "List Payments";

    public static final String ITEM_EXPORT_INVOICE = "Export Invoice";

    public static final String ITEM_CHOICE = "Enter your choice: ";

    public static final String ITEM_RETURN = "Return";

    public static final String ITEM_EXPORT_RSA = "Export Private RSA Key";

    public static final String ITEM_EXPORT_AES = "Export AES Key";

    // Status messages
    public static final String STATUS_INVALID_CHOICE = "Invalid selection";

    public static final String STATUS_ACCOUNT_IS_EXPIRED = "Account has expired - please renew subscription";

    public static final String STATUS_CONNECTION = "Internet Connection:";

    public static final String STATUS_ZKGIT_CONNECTION = "ZK Git Server Connection:";

    public static final String STATUS_IP_VERSION = "IP Version:";

    public static final String STATUS_GIT_SOCKET = "Git (localhost) Port:";

    public static final String STATUS_LOGIN = "Active Login:";

    public static final String STATUS_ACCOUNT_NUMBER = "AccountNr:";

    public static final String STATUS_USERNAME = "Username:";

    public static final String STATUS_AVAILABLE = "Available";

    public static final String STATUS_NOT_AVAILABLE = "Not Available";

    public static final String STATUS_USERS = "Users:";

    public static final String STATUS_STORAGE = "Storage:";

    public static final String STATUS_NUM_REPOS = "Repos:";

    public static final String STATUS_EXP_DATE = "Expiration date:";

    public static final String STATUS_NONE = "None";

    public static final String STATUS_IPV6 = "IPv6";

    public static final String STATUS_IPV4 = "IPv4";

    public static final String STATUS_YES = "Yes";

    public static final String STATUS_NO = "No";

    public static final String STATUS_EXIT = "Exiting...";

    public static final String STATUS_BACKGROUND = "Passing to background...";

    // Titles
    public static final String TITLE_USERNAME = "Username";

    public static final String TITLE_ROLE = "Role";

    public static final String TITLE_ACCESS_CODE = "Access Code";

    public static final String TITLE_REPONAME = "Repo";

    public static final String TITLE_SIZE = "Size (MB)";

    public static final String TITLE_LAST_PUSH = "Last Committer (Owner)";

    public static final String TITLE_SEPARATOR = "---";

    // Other
    public static final String OF_SEPARATOR = " of ";
}
