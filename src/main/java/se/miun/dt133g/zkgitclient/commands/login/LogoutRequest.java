package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.commands.CommandManager;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Command to handle the logout process.
 * This class checks the current login status and sends a request to log the user out
 * if the user is logged in. After a successful logout, the user's credentials are cleared.
 * @author Leif Rogell
 */
public final class LogoutRequest extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the logout process.
     * It checks if the user is logged in, and if so, sends a logout request.
     * Upon successful logout, user credentials are cleared and a status message is printed.
     * @return the server response to the logout request or a status indicating the user is not logged in.
     */
    @Override
    public String execute() {

        LOGGER.info("Initiating logout process");
        Map<String, String> loginStatus = CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_LOGIN_STATUS);

        return Optional.of(loginStatus)
            .filter(status -> status.containsValue(AppConfig.STATUS_AUTHORIZATION_VALID))
            .map(status -> {
                    Map<String, String> postData = new HashMap<>();
                    postData.put(AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_LOGOUT);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber());
                    postData.put(AppConfig.CREDENTIAL_USERNAME, credentials.getUsername());
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken());
                    String response = prepareAndSendPostRequest(postData);
                    if (response.contains(AppConfig.COMMAND_SUCCESS)) {
                        credentials.clearUserData();
                        System.out.println(AppConfig.NEW_LINE + AppConfig.STATUS_NOT_LOGGED_IN);
                    }
                    return response;
                })
            .orElseGet(() -> {
                    System.out.println(AppConfig.STATUS_NOT_LOGGED_IN);
                    return AppConfig.STATUS_NOT_LOGGED_IN;
                });
    }
}
