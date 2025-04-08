package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.menu.MainMenu;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Command class responsible for initiating a request to delete a user.
 * Prompts for the username to delete, confirms the action, and sends the deletion request to the server.
 * @author Leif Rogell
 */
public final class RequestUserDeletion extends BaseCommandAccount implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the user deletion command.
     * Checks server connectivity and access token before proceeding with the user deletion process.
     * @return JSON string representing success or failure of the request.
     */
    @Override
    public String execute() {
        return conn.getServerConnectivity() && credentials.hasAccessToken()
            ? handleUserDeletion()
            : utils.mapToString(Map.of(AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION));
    }

    /**
     * Handles the process of reading and validating the username to be deleted.
     * Prompts the admin for a username, validates it, and confirms the deletion.
     * @return result of the user deletion request or a command exit code.
     */
    private String handleUserDeletion() {
        return Optional.of(readUserInput(AppConfig.INFO_ENTER_USERNAME,
                              AppConfig.INFO_INVALID_USERNAME,
                              AppConfig.REGEX_USERNAME))
            .filter(userToDelete -> !AppConfig.ERROR_KEY.equals(userToDelete)
                    && !AppConfig.COMMAND_EXIT.equals(userToDelete))
            .map(userToDelete -> {
                credentials.setUserToModify(userToDelete);
                return MainMenu.INSTANCE.confirmChoice() ? requestUserDeletion() : AppConfig.NONE;
            })
            .orElse(AppConfig.COMMAND_EXIT);
    }

    /**
     * Sends the HTTP POST request to delete the specified user.
     * @return response string from the server indicating the result of the deletion request.
     */
    private String requestUserDeletion() {
        Map<String, String> postData = Map.of(
            AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_USER_DELETE,
            AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber(),
            AppConfig.CREDENTIAL_USERNAME, credentials.getUsername(),
            AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken(),
            AppConfig.CREDENTIAL_USER_MODIFY, credentials.getUserToModify()
        );
        return prepareAndSendPostRequest(postData);
    }
}
