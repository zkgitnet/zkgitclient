package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Command to request an encrypted AES key from a remote server.
 * This class constructs the necessary data and sends a request to
 * retrieve the AES key needed for encryption operations.
 * @author Leif Rogell
 */
public final class AesKeyRequest extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the command to request an encrypted AES key from the server.
     * It prepares the required credentials and sends a POST request to
     * retrieve the AES key.
     * @return the server's response to the AES key request.
     */
    @Override
    public String execute() {
        Map<String, String> postData = new HashMap<>();
        LOGGER.info("Requesting encrypted AesKey from remote server");

        postData.put(AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_AES_KEY);
        postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber());
        postData.put(AppConfig.CREDENTIAL_USERNAME, credentials.getUsername());
        postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken());

        return conn.sendPostRequest(postData);
    }
}
