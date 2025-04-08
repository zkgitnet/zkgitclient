package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Handles the request for retrieving a list of repositories associated with the user's account.
 * Sends a request to the server containing the user's credentials and returns the response.
 * @author Leif Rogell
 */
public final class RequestRepoList extends BaseCommandAccount implements Command {

    private final String ERROR_MESSAGE = "Could not fetch repo list";

    /**
     * Executes the command to fetch the user's repository list.
     * Verifies server connectivity and valid access token before preparing and sending the request.
     * @return a JSON string containing the list of repositories or an error message if the request fails.
     */
    @Override
    public String execute() {
        return Optional.of(conn.getServerConnectivity())
            .filter(valid -> credentials.hasAccessToken())
            .map(valid -> {
                    Map<String, String> postData = new HashMap<>();
                    postData.put(AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_REPO_LIST);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber());
                    postData.put(AppConfig.CREDENTIAL_USERNAME, credentials.getUsername());
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken());
                    return conn.sendPostRequest(postData);
                })
            .orElseGet(() -> {
                    return createErrorResponse(AppConfig.ERROR_CONNECTION);
                });
    }
}
