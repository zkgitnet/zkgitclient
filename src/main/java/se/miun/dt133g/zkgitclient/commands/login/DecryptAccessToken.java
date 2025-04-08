package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Command to decrypt the access token used for authentication.
 * This class handles the decryption of the access token by using the user's credentials and encryption keys.
 * It sends a post request for the AES key upon successful decryption.
 * @author Leif Rogell
 */
public final class DecryptAccessToken extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the decryption of the access token.
     * It reads the password, performs necessary decryption using PBKDF2, AES, and RSA encryption methods,
     * and sends a post request to retrieve the AES key if the decryption is successful.
     * @return the result of the post request or an exit command if decryption fails.
     */
    @Override
    public String execute() {

        LOGGER.info("Decrypting access token");

        return Optional.ofNullable(readPassword(AppConfig.INFO_ENTER_PASSWORD,
                                                AppConfig.INFO_INVALID_PASSWORD,
                                                AppConfig.REGEX_PASSWORD))
            .filter(password -> !AppConfig.ERROR_KEY.equals(password))
            .map(password -> {
                    pbkdf2Handler.encrypt();
                    aesHandler.setAesKey(credentials.getPbkdf2Hash());
                    aesHandler.setIv(credentials.getIv());
                    aesHandler.setInput(credentials.getEncPrivRsa());
                    aesHandler.decrypt();

                    credentials.setPrivRsa(aesHandler.getOutput());
                    rsaHandler.setRsaKey(aesHandler.getOutput());
                    rsaHandler.setInput(credentials.getEncAccessToken());
                    rsaHandler.decrypt();

                    String decryptedAccessToken = rsaHandler.getOutput();

                    if (decryptedAccessToken.length() == AppConfig.CRYPTO_TOKEN_LENGTH) {

                        credentials.setAccessToken(decryptedAccessToken);

                        Map<String, String> postData = Map.of(AppConfig.COMMAND_KEY,
                                                              AppConfig.COMMAND_REQUEST_AES_KEY,
                                                              AppConfig.CREDENTIAL_ACCOUNT_NR,
                                                              credentials.getAccountNumber(),
                                                              AppConfig.CREDENTIAL_USERNAME,
                                                              credentials.getUsername(),
                                                              AppConfig.CREDENTIAL_ACCESS_TOKEN,
                                                              credentials.getAccessToken());
                        return prepareAndSendPostRequest(postData);
                    } else {
                        LOGGER.severe(AppConfig.ERROR_KEY
                                      + AppConfig.COLON_SEPARATOR
                                      + AppConfig.SPACE_SEPARATOR
                                      + AppConfig.ERROR_INVALID_PASSWORD);
                        return AppConfig.NONE;
                    }
                })
            .orElse(AppConfig.COMMAND_EXIT);
    }
}
