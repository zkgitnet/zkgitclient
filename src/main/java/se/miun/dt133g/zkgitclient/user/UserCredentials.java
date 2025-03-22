package se.miun.dt133g.zkgitclient.user;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Arrays;
import java.util.logging.Logger;

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

    private UserCredentials() { }

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

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public void setUserToModify(final String userToModify) {
        this.userToModify = userToModify;
    }

    public void setEncPrivRsaJson(final String encPrivRsaJson) {
        this.encPrivRsaJson = AppConfig.FORWARD_CURLY_BRACKET + encPrivRsaJson.replace(AppConfig.SEMICOLON_SEPARATOR, AppConfig.COMMA_SEPARATOR) + AppConfig.BACKWARD_CURLY_BRACKET;
        JSONObject jsonObject = new JSONObject(this.encPrivRsaJson);
        JSONArray saltArray = jsonObject.getJSONArray(AppConfig.DB_SALT);
        JSONArray ivArray = jsonObject.getJSONArray(AppConfig.DB_IV);

        salt = convertJsonArrayToByteArray(saltArray);
        iv = convertJsonArrayToByteArray(ivArray);

        encPrivRsa = jsonObject.getString(AppConfig.DB_ENC_PRIV_RSA_KEY);
    }

    public byte[] convertJsonArrayToByteArray(final JSONArray jsonArray) {
        byte[] byteArray = new byte[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            byteArray[i] = (byte) jsonArray.getInt(i);
            jsonArray.put(i, 0);
        }
        return byteArray;
    }

    public void setSalt(final JSONArray jsonArray) {
        this.salt = convertJsonArrayToByteArray(jsonArray);
    }

    public void setIv(final JSONArray jsonArray) {
        this.iv = convertJsonArrayToByteArray(jsonArray);
    }

    public void setIv(final String ivArray) {
        JSONArray jsonArray = new JSONArray(ivArray.replace(";",","));
        setIv(jsonArray);
    }

    public void setEncAesKey(final String encAesKey) {
        this.encAesKey = encAesKey;
    }

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

    public void setPbkdf2Hash(final byte[] pbkdf2Hash) {
        this.pbkdf2Hash = pbkdf2Hash;
    }

    public void setTotpToken(final String totpToken) {
        this.totpToken = totpToken;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPrivRsa(final String privRsa) {
        this.privRsa = privRsa.toCharArray();
    }

    public void setPassword(final char[] password) {
        if (this.password != null) {
            clearPassword();
        }
        this.password = new char[password.length];
        System.arraycopy(password, 0, this.password, 0, password.length);
        Arrays.fill(password, '\0');
    }

    public void clearUserData() {
        clearPassword();
        clearSalt();
        clearIv();
        clearPbkdf2Hash();
        clearAesKey();
        clearAesKeyJson();
    }

    private void clearPrivRsa() {
        if (this.privRsa != null) {
            Arrays.fill(this.privRsa, '\0');
        }
    }

    private void clearAesKeyJson() {
        if (this.aesKeyJson != null) {
            Arrays.fill(this.aesKeyJson, '\0');
        }
    }

    private void clearPassword() {
        if (this.password != null) {
            Arrays.fill(this.password, '\0');
        }
    }

    private void clearSalt() {
        if (this.salt != null) {
            Arrays.fill(this.salt, (byte) 0);
        }
    }

    private void clearIv() {
        if (this.iv != null) {
            Arrays.fill(this.iv, (byte) 0);
        }
    }

    private void clearPbkdf2Hash() {
        if (this.pbkdf2Hash != null) {
            Arrays.fill(this.pbkdf2Hash, (byte) 0);
        }
    }

    private void clearAesKey() {
        if (this.aesKey != null) {
            Arrays.fill(this.aesKey, (byte) 0);
        }
    }


    public void setEncAccessToken(final String encAccessToken) {
        this.encAccessToken = encAccessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getEncPrivRsa() {
        return encPrivRsa;
    }

    public String getNonce() {
        return nonce;
    }

    public String getEncAesKey() {
        return encAesKey;
    }

    public byte[] getAesKey() {
        return aesKey;
    }

    public byte[] getPbkdf2Hash() {
        return pbkdf2Hash;
    }

    public byte[] getIv() {
        return iv;
    }

    public String getAesKeyJson() {
        return new String(aesKeyJson);
    }

    public byte[] getSalt() {
        return salt;
    }

    public String getPrivRsa() {
        return new String(privRsa);
    }

    public String getTotpToken() {
        return totpToken;
    }

    public String getUserToModify() {
        return userToModify;
    }

    public String getEncPrivRsaJson() {
        return encPrivRsaJson;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public String getEncAccessToken() {
        return encAccessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }
}
