package se.miun.dt133g.zkgitclient.support;

public final class AppConfig {

    private AppConfig() {
        throw new IllegalStateException("Utility class");
    }

    // Server & connection configuration
    public static final String DOMAIN_PREFIX = "https://";
    
    public static final String API_DOMAIN = "api.zkgit.net";

    public static final String API_PORT = "11443";

    public static final String URI_PATH = "/zkgitserver-1.0.0/zkgit";

    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";

    public static final String REQUEST_TYPE_POST = "POST";
    
    public static final String REQUEST_TYPE_GET = "GET";

    public static final String REQUEST_POST_END = "\r\n";

    public static final int GIT_PORT = 10101;

    public static final int MAX_PORT_NUMBER = 65535;

    public static final int MAX_RAND_BYTE = 65536;

    public static final String CONTENT_DISPOSITION_NAME = "Content-Disposition: form-data; name=\"";

    public static final String CONTENT_DISPOSITION_FILENAME = "Content-Disposition: form-data; name=\"file\"; filename=\"";

    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data; boundary=";

    public static final String CONTENT_TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary";

    public static final String CONTENT_TYPE_PLAIN = "Content-Type: text/plain; charset=UTF-8";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String CONTENT_TYPE2 = "Content-Type: ";

    // Path configuration
    public static final String JAVA_TMP = "java.io.tmpdir";

    public static final String ZIP_SUFFIX = ".zip";

    public static final String PARTS_SUFFIX = "_parts";

    public static final String PART_SUFFIX = ".part";

    public static final String TMP_PREFIX = "zkgit-tmp-";

    // Commands
    public static final String COMMAND_KEY = "COMMAND";

    public static final String COMMAND_SUCCESS = "SUCCESS";

    public static final String COMMAND_EXIT = "EXIT";
    
    public static final String COMMAND_GET_TOTP = "GET_TOTP";

    public static final String COMMAND_REQUEST_LOGIN = "LOGIN";

    public static final String COMMAND_GENERATE_USER = "NEW_USER";

    public static final String COMMAND_REQUEST_LOGOUT = "LOGOUT";

    public static final String COMMAND_CHANGE_PASSWORD = "CHANGE_PASSWORD";

    public static final String COMMAND_VALIDATE_TOTP = "VALIDATE_TOTP";

    public static final String COMMAND_VALIDATE_ACCESS_TOKEN = "VALIDATE_ACCESS_TOKEN";

    public static final String COMMAND_DECRYPT_ACCESS_TOKEN = "DECRYPT_ACCESS_TOKEN";

    public static final String COMMAND_DECRYPT_ACCESS_CODE = "DECRYPT_ACCESS_CODE";

    public static final String COMMAND_REQUEST_AES_KEY = "GET_AES_KEY";

    public static final String COMMAND_DECRYPT_AES_KEY = "DECRYPT_AES_KEY";

    public static final String COMMAND_LOGIN_STATUS = "STATUS";

    public static final String COMMAND_SEND_REPO_FILE = "SEND";

    public static final String COMMAND_REQUEST_REPO_FILE = "REQUEST";

    public static final String COMMAND_TRANSFER_REPO = "TRANSFER_REPO";

    public static final String COMMAND_REQUEST_ACCOUNT_DATA = "ACCOUNT_DATA";

    public static final String COMMAND_REQUEST_USER_LIST = "USER_LIST";

    public static final String COMMAND_REQUEST_REPO_LIST = "REPO_LIST";

    public static final String COMMAND_CLEAN = "CLEAN";

    public static final String COMMAND_ASSEMBLY_REPO = "ASSEMBLY_REPO";

    public static final String COMMAND_RESEND_LAST_PACKAGE = "RESEND";

    public static final String COMMAND_REQUEST_USER_DELETE = "DELETE_USER";

    public static final String COMMAND_REQUEST_USER_PRIV_CHANGE = "USER_PRIV_CHANGE";


    // Repo transfer
    public static final String ENC_FILE_NAME = "ENC_FILE_NAME";

    public static final String NUM_CHUNKS = "NUM_CHUNKS";

    public static final int CHUNK_SIZE = 1048576; // 1 MB = 1024 * 1024 KB

    public static final String FILE_READ_MODE = "r";

    public static final int NUM_RETRIES = 5;

    public static final String FILE_SHA256_HASH= "fileHash";

    public static final String REPO_SIGNATURE = "repohash";

    // User Credentials Name
    public static final String CREDENTIAL_ACCOUNT_NR = "accountnr";

    public static final String CREDENTIAL_TOTP_TOKEN = "totpToken";

    public static final String CREDENTIAL_USERNAME = "username";

    public static final String CREDENTIAL_ENC_ACCESS_TOKEN = "encAccessToken";

    public static final String CREDENTIAL_ACCESS_TOKEN = "accessToken";

    public static final String CREDENTIAL_CURRENT_TIMESTAMP = "timestamp";

    public static final String CREDENTIAL_NEW_USERNAME = "newUsername";

    public static final String CREDENTIAL_ENC_PRIV_RSA_KEY = "encPrivRsaJson";

    public static final String CREDENTIAL_ENC_AES_KEY = "encAesKey";

    public static final String CREDENTIAL_AES_KEY = "aesKey";

    public static final String CREDENTIAL_USER_MODIFY = "userToModify";

    public static final String CREDENTIAL_NEW_ENC_PRIV_RSA = "newEncPrivRsaJson";

    public static final String CREDENTIAL_NEW_ENC_AES = "newEncAesKey";

    public static final String CREDENTIAL_NEW_PUB_RSA = "newPubRsaJson";

    public static final String CREDENTIAL_NEW_TOTP_SECRET = "newTotpSecret";


    // Database Fields
    public static final String DB_SALT = "salt";

    public static final String DB_IV = "iv";

    public static final String DB_ENC_PRIV_RSA_KEY = "ciphertext";

    public static final String DB_TOTP_SECRET = "totpsecret";

    public static final String DB_ENC_PRIV_RSA_JSON = "encprivrsa";

    public static final String DB_IPV4 = "ipv4";

    public static final String DB_IPV6 = "ipv6";

    public static final String DB_REPO_MAX_USERS = "maxusers";

    public static final String DB_REPO_TOTAL_USERS = "totalusers";

    public static final String DB_REPO_MAX_STORAGE = "maxstorage";

    public static final String DB_REPO_TOTAL_SIZE = "totalsize";

    public static final String DB_REPO_TOTAL_REPOS = "totalrepos";

    public static final String DB_REPO_EXP_DATE = "expdate";

    // Crypto
    public static final String CRYPTO_PBKDF2_METHOD = "PBKDF2WithHmacSHA512";

    public static final String CRYPTO_PBKDF2 = "PBKDF2";

    public static final String CRYPTO_SSL = "SSL";

    public static final int CRYPTO_PBKDF2_ITERATIONS = 100000;

    public static final int CRYPTO_PBKDF2_KEY_LENGTH = 256;

    public static final String CRYPTO_RSA = "RSA";

    public static final String CRYPTO_RSA_OAEP = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";

    public static final String CRYPTO_AES = "AES";

    public static final String CRYPTO_RSA_SIGNATURE_ALGORITHM = "SHA512withRSA";

    public static final String CRYPTO_RSA_SIGNATURE = "RSA_SIGN";

    public static final String CRYPTO_AES_FILE = "AES_FILE";

    public static final String CRYPTO_AES_GCM = "AES/GCM/NoPadding";

    public static final int CRYPTO_AES_TAG_LENGTH = 128;

    public static final int CRYPTO_TOKEN_LENGTH = 64;

    public static final int CRYPTO_RSA_KEY_LENGTH = 4096;

    public static final int CRYPTO_SALT_LENGTH = 32;

    public static final int CRYPTO_IV_LENGTH = 12;

    public static final String CRYPTO_BASE32_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    
    public static final int CRYPTO_TOTP_SECRET_LENGTH = 24;

    public static final String CRYPTO_SHA_512 = "SHA-512";

    public static final String CRYPTO_SHA_256 = "SHA-256";

    public static final String CRYPTO_SHA_256_FILE = "SHA-256_FILE";

    public static final String CRYPTO_MGF1 = "MGF1";

    public static final String RSA_N = "n";

    public static final String RSA_D = "d";

    public static final String RSA_E = "e";

    public static final String RSA_P = "p";

    public static final String RSA_Q = "q";

    public static final String RSA_DP = "dp";

    public static final String RSA_DQ = "dq";

    public static final String RSA_QI = "qi";

    // Status messages
    public static final String STATUS_GENERATING_KEYS = "Generating new encryption keys...";

    public static final String STATUS_CREATING_USER = "Creating user on server...";
    
    public static final String STATUS_LOGGED_IN = "Logged in";

    public static final String STATUS_REPO_UPTODATE = "upToDate";

    public static final String STATUS_NOT_LOGGED_IN = "Not logged in";

    public static final String STATUS_AUTHORIZATION_VALID = "VALID_TOKEN";

    public static final String STATUS_ACCOUNT_EXPIRED = "ACCOUNT_EXPIRED";

    public static final String STATUS_NOT_ADMIN = "This function requires admin privileges.";

    // Info messages & Input config
    public static final String INFO_ENTER_ACCOUNT_NUMBER = "Enter account number (25 digits) or '0' to exit: ";

    public static final String INFO_INVALID_ACCOUNT_NUMBER_INPUT = "Invalid account number. It must be exactly 25 digits.";

    public static final String INFO_ENTER_TOTP = "Enter 2FA Token (6 digits) or '0' to exit: ";

    public static final String INFO_INVALID_TOTP_INPUT = "Invalid 2FA Token. It must be exactly 6 digits.";

    public static final String INFO_ENTER_USERNAME = "Enter username or '0' to exit: ";

    public static final String INFO_ENTER_PORT_NUMBER = "Enter the port number (0 to exit): ";

    public static final String INFO_INVALID_PORT_NUMBER = "Invalid port number. Please enter a number between 1 and 65535.";

    public static final String INFO_INVALID_USERNAME = "Invalid username. It cannot be empty or longer than 50 characters. Usernames consists of only characters.";

    public static final String INFO_ENTER_PASSWORD = "Enter password or '0' to exit: ";

    public static final String INFO_INVALID_PASSWORD = "Invalid password. It is between 12 and 30 characters, and consists of letters, digits and special characters.";

    public static final String INFO_FILE_SAVED = "File saved to: ";

    public static final String INFO_PORT_SET = "Port set to: ";

    public static final int MAX_NUM_INPUT_ATTEMPTS = 3;

    // Errors
    public static final String ERROR_KEY = "ERROR";

    public static final String WARNING_KEY = "WARNING";

    public static final String ERROR_MYSQL_DRIVER = "JDBC Driver Missing";

    public static final String ERROR_GENERATE_RSA_KEYS = "ERROR: Generating RSA Keys";

    public static final String ERROR_MYSQL_EXCEPTION = "MySql Exception";

    public static final String ERROR_INVALID_ACCOUNTNR_USERNAME = "Invalid Accountnr and/or Username";

    public static final String ERROR_INVALID_COMMAND = "Command not found";

    public static final String ERROR_CONNECTION = "Connection failure";

    public static final String ERROR_CREDENTIALS_MISSING = "No user credentials found - please log in again";

    public static final String ERROR_ACCOUNT_NUMBER_INPUT = "Error submitting account number: ";

    public static final String ERROR_USERNAME_INPUT = "Error submitting username: ";

    public static final String ERROR_PORT_NOT_FREE = "Port not free. Please enter another port number.";

    public static final String ERROR_INVALID_PORT = "Invalid input. Please enter a valid integer.";

    public static final String ERROR_NO_FREE_PORT = "Git port already occupied. Either another application is using the same port or another instance of ZK Git is already running." + System.lineSeparator();

    public static final String ERROR_SOCKET_INTERRUPT = "Git connection was interrupted. Please restart the client.";

    public static final String ERROR_TOTP_INPUT = "Error submitting totp: ";

    public static final String ERROR_PASSWORD_INPUT = "Error submitting password: ";

    public static final String ERROR_FAILED_SAVE_FILE = "Failed to save file: ";

    public static final String ERROR_CREATE_DIR = "Failed to create directory: ";

    public static final String ERROR_READ_FILE = "Error reading file: ";

    public static final String ERROR_WRITE_FILE = "Error writing file: ";

    public static final String ERROR_UNKNOWN_ACCOUNTNR_USERNAME = "Unknown AccountNr and/or Username";

    public static final String ERROR_INVALID_TOTP_TOKEN = "Invalid TOTP token";

    public static final String ERROR_INVALID_PASSWORD = "Incorrect password";

    public static final String ERROR_DECRYPTION = "Error decryption";

    // Regexes
    public static final String REGEX_ACCOUNT_NUMBER = "\\d{25}";

    public static final String REGEX_USERNAME = "[a-zA-Z]{1,50}";

    public static final String REGEX_TOTP = "\\d{6}";

    public static final String REGEX_COMMAND_RESPONSE = "^\\s*(\\w+)\\s*=\\s*(.*)\\s*$";

    public static final String REGEX_PASSWORD = "[a-zA-Z0-9!@#$%^&*()_+\\-={}:;\"'<>,.?/]{1,30}";

    // File names
    public static final String FILE_PRIV_RSA = "privRsaKey";

    public static final String FILE_AES = "aesKey";

    public static final String FILE_JSON = ".json";

    //  Other
    public static final String NEW_LINE = System.lineSeparator();

    public static final String SPACE_SEPARATOR = " ";

    public static final String COMMA_SEPARATOR = ",";

    public static final String COLON_SEPARATOR = ":";

    public static final String SEMICOLON_SEPARATOR = ";";

    public static final String EQUAL_SEPARATOR = "=";

    public static final String DASH_SEPARATOR = "-";

    public static final String SLASH_SEPARATOR = "/";

    public static final String FORWARD_BRACKET = "(";

    public static final String BACKWARD_BRACKET = ")";

    public static final String FORWARD_SQUARE_BRACKET = "[";

    public static final String BACKWARD_SQUARE_BRACKET = "]";

    public static final String FORWARD_CURLY_BRACKET = "{";

    public static final String BACKWARD_CURLY_BRACKET = "}";

    public static final String AND_SEPARATOR = "&";

    public static final String NONE = "";

    public static final String ZERO_STRING = "0";

    public static final String UNDERSCORE = "_";

    public static final char NULL_TERMINATOR = '\0';

    public static final String HASH = "#";

    public static final int MB = 1048576;

    public static final int FOUR_KB = 4096;

    public static final int ONE_SECOND = 1000;

    public static final int FIVE_SECONDS = 5000;

    public static final int TEN_SECONDS = 10000;

    public static final int TWENTY_MINUTES = 1200000;

    public static final int HEX_FF = 0xFF;
}
