package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.commands.login.BaseCommandLogin;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.crypto.GenerateEncryptionKeys;
import se.miun.dt133g.zkgitclient.support.AppConfig;
import se.miun.dt133g.zkgitclient.support.Utils;

import org.json.JSONObject;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.nio.charset.StandardCharsets;

public final class RequestNewUser extends BaseCommandAccount implements Command {

    private GenerateEncryptionKeys genEncKeys = new GenerateEncryptionKeys();
    private Utils utils = Utils.getInstance();
    private EncryptionHandler pbkdf2Handler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_PBKDF2);
    private EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);
    private EncryptionHandler rsaHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_RSA);

    private final String ERROR_MESSAGE = "Could not create a new user";
    private String privRsaKey;
    private String pubRsaKey;
    private String totpSecret;
    private String newPassword;
    private int[] salt;
    private int[] iv;

    @Override
    public String execute() {
        return Optional.of(conn.getServerConnectivity())
            .filter(valid -> credentials.hasAccessToken())
            .map(valid -> handleUserCreation())
            .orElseGet(() -> createErrorResponse(ERROR_MESSAGE));
    }

    private String handleUserCreation() {
        try {
            generateKeys();

            String encAesKey = encryptAesKey(credentials.getAesKeyJson());
            newPassword = readPassword(AppConfig.INFO_ENTER_PASSWORD, AppConfig.INFO_INVALID_PASSWORD, AppConfig.REGEX_PASSWORD);
            System.out.println("New Password: " + newPassword);
            String encPrivRsa = encryptPrivateKey(newPassword);
            String postResponse = sendCreateUserRequest(encAesKey, encPrivRsa);
            Map<String, String> postResponseMap = extractResponseToMap(postResponse);

            if (postResponseMap.containsKey(AppConfig.COMMAND_SUCCESS)) {
                System.out.println("\nNew Username: " + postResponseMap.get(AppConfig.CREDENTIAL_NEW_USERNAME));
                System.out.println("TOTP Secret: " + utils.formatWithSpace(postResponseMap.get(AppConfig.CREDENTIAL_NEW_TOTP_SECRET)));
                System.out.println("\nHand this information together with the initial password to the new user.\n");
            }

            return postResponse;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(AppConfig.ERROR_GENERATE_RSA_KEYS + e.getMessage());
            return createErrorResponse(AppConfig.ERROR_GENERATE_RSA_KEYS);
        }
    }

    private void generateKeys() throws Exception {
        System.out.println(AppConfig.STATUS_GENERATING_KEYS);
        privRsaKey = genEncKeys.generatePrivateRsaJwk();
        System.out.println("newPrivRsaKey: " + privRsaKey);
        
        pubRsaKey = genEncKeys.generatePublicRsaJwk();
        System.out.println("newPubRsaKey: " + pubRsaKey);
        
        salt = genEncKeys.generateSalt();
        iv = genEncKeys.generateIv();
        totpSecret = genEncKeys.generateTotpSecret();
        System.out.println("newTotpSecret: " + totpSecret);
    }

    private String encryptAesKey(final String aesKeyJson) {
        rsaHandler.setRsaKey(pubRsaKey);
        rsaHandler.setInput(aesKeyJson);
        rsaHandler.encrypt();
        return rsaHandler.getOutput();
    }

    private String encryptPrivateKey(String newPassword) {
        String saltString = utils.formatIntArray("salt", salt);
        String ivString = utils.formatIntArray("iv", iv);

        JSONObject saltJsonObject = new JSONObject("{" + saltString + "}");
        JSONObject ivJsonObject = new JSONObject("{" + ivString + "}");

        credentials.setSalt(saltJsonObject.getJSONArray("salt"));
        //credentials.setPassword("testtesttest1".toCharArray());
        //credentials.setPassword(); 
        pbkdf2Handler.encrypt();
        byte[] passwordHash = credentials.getPbkdf2Hash();

        aesHandler.setInput(privRsaKey);
        aesHandler.setAesKey(passwordHash);
        credentials.setIv(ivJsonObject.getJSONArray("iv"));
        aesHandler.setIv(credentials.getIv());
        aesHandler.encrypt();

        return saltString + ";" + ivString + ";\"ciphertext\":\"" + aesHandler.getOutput() + "\"";
    }

    private String sendCreateUserRequest(final String encAesKey, final String encPrivRsa) {
        System.out.println(AppConfig.STATUS_CREATING_USER);
        Map<String, String> postData = new HashMap<>();
        postData.put(AppConfig.COMMAND_KEY, AppConfig.COMMAND_GENERATE_USER);
        postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber());
        postData.put(AppConfig.CREDENTIAL_USERNAME, credentials.getUsername());
        postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken());
        postData.put(AppConfig.CREDENTIAL_NEW_ENC_PRIV_RSA, base64UrlEncode(encPrivRsa));
        postData.put(AppConfig.CREDENTIAL_NEW_ENC_AES, base64UrlEncode(encAesKey));
        postData.put(AppConfig.CREDENTIAL_NEW_PUB_RSA, base64UrlEncode(pubRsaKey));
        postData.put(AppConfig.CREDENTIAL_NEW_TOTP_SECRET, totpSecret);

        return conn.sendPostRequest(postData);
    }

    private String base64UrlEncode(final String input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
