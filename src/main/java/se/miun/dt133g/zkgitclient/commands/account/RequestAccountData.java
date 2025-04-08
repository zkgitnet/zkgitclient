package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Handles the request for retrieving user account data from the server.
 * This command checks if a valid access token is available and then
 * sends a POST request to fetch account-related information.
 * @author Leif Rogell
 */
public final class RequestAccountData extends BaseCommandAccount implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private final String ERROR_MESSAGE = "Could not fetch account data";

    /**
     * Executes the command to fetch account data.
     * If a valid access token is present, prepares the request data and sends it
     * to the server. Otherwise, returns an error response.
     * @return A JSON string containing account data or an error message.
     */
    @Override
    public String execute() {
        return Optional.of(conn.getServerConnectivity())
            .filter(valid -> credentials.hasAccessToken())
            .map(valid -> {
                    Map<String, String> postData = new HashMap<>();
                    postData.put(AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_ACCOUNT_DATA);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber());
                    postData.put(AppConfig.CREDENTIAL_USERNAME, credentials.getUsername());
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken());
                    return prepareAndSendPostRequest(postData);
                })
            .orElseGet(() -> {
                    return createErrorResponse(ERROR_MESSAGE);
                });
    }
}
