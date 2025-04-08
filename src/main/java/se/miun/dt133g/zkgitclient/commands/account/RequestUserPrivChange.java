package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.menu.MainMenu;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Command class that handles the privilege change of a user (e.g., demoting from admin to regular user).
 * This class checks server connectivity and access credentials, prompts for a username,
 * and sends a request to update the user’s privilege level.
 * @author Leif Rogell
 */
public final class RequestUserPrivChange extends BaseCommandAccount implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the command to initiate a user privilege change.
     * Validates server connectivity and access credentials before proceeding.
     * @return JSON response from the server or an error message.
     */
    @Override
    public String execute() {
        return conn.getServerConnectivity() && credentials.hasAccessToken()
            ? handleUserPrivChange()
            : utils.mapToString(Map.of(AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION));
    }

    /**
     * Prompts the admin to input the username of the user whose privileges should be changed.
     * Confirms the action before sending the request.
     * @return Server response or cancellation message.
     */
    private String handleUserPrivChange() {
        return Optional.of(readUserInput(AppConfig.INFO_ENTER_USERNAME,
                              AppConfig.INFO_INVALID_USERNAME,
                              AppConfig.REGEX_USERNAME))
            .filter(userToModify -> !AppConfig.ERROR_KEY.equals(userToModify)
                    && !AppConfig.COMMAND_EXIT.equals(userToModify))
            .map(userToModify -> {
                credentials.setUserToModify(userToModify);
                return MainMenu.INSTANCE.confirmChoice() ? requestAdminToUser() : AppConfig.NONE;
            })
            .orElse(AppConfig.COMMAND_EXIT);
    }

    /**
     * Prepares and sends the request to change the privilege level of the selected user.
     * @return JSON response from the server.
     */
    private String requestAdminToUser() {
        Map<String, String> postData = Map.of(
            AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_USER_PRIV_CHANGE,
            AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber(),
            AppConfig.CREDENTIAL_USERNAME, credentials.getUsername(),
            AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken(),
            AppConfig.CREDENTIAL_USER_MODIFY, credentials.getUserToModify()
        );
        return prepareAndSendPostRequest(postData);
    }
}
