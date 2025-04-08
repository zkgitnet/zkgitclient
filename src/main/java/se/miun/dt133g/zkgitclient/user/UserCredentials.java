package se.miun.dt133g.zkgitclient.user;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Singleton class to manage user credentials.
 * Includes sensitive data such as encryption keys, passwords, and tokens.
 * @author Leif Rogell
 */
public final class UserCredentials {

    private static UserCredentials INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private String accountNumber;
    private String totpToken;
    private String nonce;
    private String username;
    private String userToModify;
    private char[] password;

    private String encAesKey;
    private String encPrivRsaJson;
    private String encPrivRsa;
    private char[] privRsa;
    private char[] aesKeyJson;
    private byte[] salt;
    private byte[] iv;
    private byte[] pbkdf2Hash;
    private byte[] aesKey;

    private String encAccessToken;
    private String accessToken;

    /**
     * Private constructor to prevent instantiation.
     */
    private UserCredentials() { }

    /**
     * Returns the singleton instance of UserCredentials.
     * @return the UserCredentials instance.
     */
    public static UserCredentials getInstance() {
        if (INSTANCE == null) {
            synchronized (UserCredentials.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserCredentials();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Sets the account number of the user.
     * @param accountNumber the account number.
     */
    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Sets the nonce for the user.
     * @param nonce the nonce.
     */
    public void setNonce(final String nonce) {
        this.nonce = nonce;
    }

    /**
     * Sets the user to modify (for user-specific operations).
     * @param userToModify the user to modify.
     */
    public void setUserToModify(final String userToModify) {
        this.userToModify = userToModify;
    }

    /**
     * Sets the encrypted private RSA key in JSON format and parses necessary data (salt, IV).
     * @param encPrivRsaJson the encrypted private RSA key in JSON format.
     */
    public void setEncPrivRsaJson(final String encPrivRsaJson) {
        this.encPrivRsaJson = AppConfig.FORWARD_CURLY_BRACKET
            + encPrivRsaJson.replace(AppConfig.SEMICOLON_SEPARATOR, AppConfig.COMMA_SEPARATOR)
            + AppConfig.BACKWARD_CURLY_BRACKET;
        JSONObject jsonObject = new JSONObject(this.encPrivRsaJson);
        JSONArray saltArray = jsonObject.getJSONArray(AppConfig.DB_SALT);
        JSONArray ivArray = jsonObject.getJSONArray(AppConfig.DB_IV);

        salt = convertJsonArrayToByteArray(saltArray);
        iv = convertJsonArrayToByteArray(ivArray);

        encPrivRsa = jsonObject.getString(AppConfig.DB_ENC_PRIV_RSA_KEY);
    }

    /**
     * Converts a JSON array to a byte array.
     * @param jsonArray the JSON array to convert.
     * @return the resulting byte array.
     */
    public byte[] convertJsonArrayToByteArray(final JSONArray jsonArray) {
        byte[] byteArray = new byte[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            byteArray[i] = (byte) jsonArray.getInt(i);
            jsonArray.put(i, 0);
        }
        return byteArray;
    }

    /**
     * Sets the salt used for key derivation.
     * @param jsonArray the salt as a JSON array.
     */
    public void setSalt(final JSONArray jsonArray) {
        this.salt = convertJsonArrayToByteArray(jsonArray);
    }

    /**
     * Sets the IV (Initialization Vector) used for encryption.
     * @param jsonArray the IV as a JSON array.
     */
    public void setIv(final JSONArray jsonArray) {
        this.iv = convertJsonArrayToByteArray(jsonArray);
    }

    /**
     * Sets the IV from a string representation of an array.
     * @param ivArray the IV as a string representation.
     */
    public void setIv(final String ivArray) {
        JSONArray jsonArray = new JSONArray(ivArray.replace(";", ","));
        setIv(jsonArray);
    }

    /**
     * Sets the encrypted AES key for the user.
     * @param encAesKey the encrypted AES key.
     */
    public void setEncAesKey(final String encAesKey) {
        this.encAesKey = encAesKey;
    }

    /**
     * Sets the AES key from a JSON string and decodes it into a byte array.
     * @param aesKeyJson the AES key in JSON format.
     */
    public void setAesKey(final String aesKeyJson) {
        this.aesKeyJson = aesKeyJson.toCharArray();

        JSONObject jsonObject = new JSONObject(aesKeyJson);

        JSONObject aesKeyObject = jsonObject.getJSONObject(AppConfig.CREDENTIAL_AES_KEY);
        this.aesKey = Base64.getUrlDecoder().decode(aesKeyObject.getString("k"));

        JSONArray ivArray = jsonObject.getJSONArray(AppConfig.DB_IV);

        iv = new byte[ivArray.length()];
        for (int i = 0; i < ivArray.length(); i++) {
            iv[i] = (byte) ivArray.getInt(i);
            ivArray.put(i, 0);
        }
    }

    /**
     * Sets the PBKDF2 hash.
     * @param pbkdf2Hash the PBKDF2 hash.
     */
    public void setPbkdf2Hash(final byte[] pbkdf2Hash) {
        this.pbkdf2Hash = pbkdf2Hash;
    }

    /**
     * Sets the TOTP (Time-Based One-Time Password) token.
     * @param totpToken the TOTP token.
     */
    public void setTotpToken(final String totpToken) {
        this.totpToken = totpToken;
    }

    /**
     * Sets the username of the user.
     * @param username the username.
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Sets the private RSA key as a string.
     * @param privRsa the private RSA key as a string.
     */
    public void setPrivRsa(final String privRsa) {
        this.privRsa = privRsa.toCharArray();
    }

    /**
     * Sets the user's password.
     * @param password the user's password.
     */
    public void setPassword(final char[] password) {
        if (this.password != null) {
            clearPassword();
        }
        this.password = new char[password.length];
        System.arraycopy(password, 0, this.password, 0, password.length);
        Arrays.fill(password, '\0');
    }

    /**
     * Clears all sensitive user data such as password, keys, and tokens.
     */
    public void clearUserData() {
        clearPassword();
        clearSalt();
        clearIv();
        clearPbkdf2Hash();
        clearAesKey();
        clearAesKeyJson();
    }

    /**
     * Clears the private RSA key by setting all characters in the `privRsa` array to the null character ('\0').
     */
    private void clearPrivRsa() {
        if (this.privRsa != null) {
            Arrays.fill(this.privRsa, '\0');
        }
    }

    /**
     * Clears the AES key JSON string by setting all characters in the `aesKeyJson` array to the null character ('\0').
     */
    private void clearAesKeyJson() {
        if (this.aesKeyJson != null) {
            Arrays.fill(this.aesKeyJson, '\0');
        }
    }

    /**
     * Clears the password by setting all characters in the `password` array to the null character ('\0').
     */
    private void clearPassword() {
        if (this.password != null) {
            Arrays.fill(this.password, '\0');
        }
    }

    /**
     * Clears the salt by setting all bytes in the `salt` array to zero.
     */
    private void clearSalt() {
        if (this.salt != null) {
            Arrays.fill(this.salt, (byte) 0);
        }
    }

    /**
     * Clears the initialization vector (IV) by setting all bytes in the `iv` array to zero.
     */
    private void clearIv() {
        if (this.iv != null) {
            Arrays.fill(this.iv, (byte) 0);
        }
    }

    /**
     * Clears the PBKDF2 hash by setting all bytes in the `pbkdf2Hash` array to zero.
     */
    private void clearPbkdf2Hash() {
        if (this.pbkdf2Hash != null) {
            Arrays.fill(this.pbkdf2Hash, (byte) 0);
        }
    }

    /**
     * Clears the AES key by setting all bytes in the `aesKey` array to zero.
     */
    private void clearAesKey() {
        if (this.aesKey != null) {
            Arrays.fill(this.aesKey, (byte) 0);
        }
    }

    /**
     * Sets the encrypted access token.
     * @param encAccessToken the encrypted access token.
     */
    public void setEncAccessToken(final String encAccessToken) {
        this.encAccessToken = encAccessToken;
    }

    /**
     * Sets the access token.
     * @param accessToken the access token.
     */
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets the account number.
     * @return the account number.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Gets the encrypted private RSA key.
     * @return the encrypted private RSA key.
     */
    public String getEncPrivRsa() {
        return encPrivRsa;
    }

    /**
     * Gets the nonce.
     * @return the nonce.
     */
    public String getNonce() {
        return nonce;
    }

    /**
     * Gets the encrypted AES key.
     * @return the encrypted AES key.
     */
    public String getEncAesKey() {
        return encAesKey;
    }

    /**
     * Gets the AES key.
     * @return the AES key as a byte array.
     */
    public byte[] getAesKey() {
        return aesKey;
    }

    /**
     * Gets the PBKDF2 hash.
     * @return the PBKDF2 hash.
     */
    public byte[] getPbkdf2Hash() {
        return pbkdf2Hash;
    }

    /**
     * Gets the IV.
     * @return the IV as a byte array.
     */
    public byte[] getIv() {
        return iv;
    }

    /**
     * Gets the AES key as a JSON string.
     * @return the AES key as a JSON string.
     */
    public String getAesKeyJson() {
        return new String(aesKeyJson);
    }

    /**
     * Gets the salt.
     * @return the salt as a byte array.
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Gets the private RSA key.
     * @return the private RSA key as a string.
     */
    public String getPrivRsa() {
        return new String(privRsa);
    }

    /**
     * Gets the TOTP token.
     * @return the TOTP token.
     */
    public String getTotpToken() {
        return totpToken;
    }

    /**
     * Gets the user to modify.
     * @return the user to modify.
     */
    public String getUserToModify() {
        return userToModify;
    }

    /**
     * Gets the encrypted private RSA key in JSON format.
     * @return the encrypted private RSA key in JSON format.
     */
    public String getEncPrivRsaJson() {
        return encPrivRsaJson;
    }

    /**
     * Gets the username.
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password.
     * @return the password as a character array.
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * Gets the encrypted access token.
     * @return the encrypted access token.
     */
    public String getEncAccessToken() {
        return encAccessToken;
    }

    /**
     * Gets the access token.
     * @return the access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Checks if an access token is set.
     * @return true if an access token is set, false otherwise.
     */
    public boolean hasAccessToken() {
        return accessToken != null;
    }
}
