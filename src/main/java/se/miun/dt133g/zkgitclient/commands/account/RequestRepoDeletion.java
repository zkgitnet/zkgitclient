package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.menu.MainMenu;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Handles the process of requesting a repository deletion from the server.
 * Prompts the user for a repository name, confirms their intent,
 * encrypts the repository name, and sends a deletion request.
 * @author Leif Rogell
 */
public final class RequestRepoDeletion extends BaseCommandAccount implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the repository deletion process.
     * Verifies server connectivity and user authentication before proceeding.
     * @return the server response or a connection error message.
     */
    @Override
    public String execute() {
        return conn.getServerConnectivity() && credentials.hasAccessToken()
            ? handleUserDeletion()
            : utils.mapToString(Map.of(AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION));
    }

    /**
     * Handles user interaction for repository deletion.
     * Prompts the user to enter a repository name and confirms the deletion.
     * @return the server response, or a cancellation/exit signal.
     */
    private String handleUserDeletion() {
        return Optional.of(readUserInput(AppConfig.INFO_ENTER_REPONAME,
                              AppConfig.INFO_INVALID_USERNAME,
                              AppConfig.REGEX_REPONAME))
            .filter(repoToDelete -> !AppConfig.ERROR_KEY.equals(repoToDelete)
                    && !AppConfig.COMMAND_EXIT.equals(repoToDelete))
            .map(repoToDelete -> {
                currentRepo.setRepoName(repoToDelete);
                return MainMenu.INSTANCE.confirmChoice() ? requestRepoDeletion() : AppConfig.NONE;
            })
            .orElse(AppConfig.COMMAND_EXIT);
    }

    /**
     * Prepares and sends the deletion request to the server.
     * Encrypts the repository name before sending the request.
     * @return the server response after the deletion request is sent.
     */
    private String requestRepoDeletion() {

        aesHandler.setInput(currentRepo.getRepoName());
        aesHandler.encrypt();

        Map<String, String> postData = Map.of(
            AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_REPO_DELETE,
            AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber(),
            AppConfig.CREDENTIAL_USERNAME, credentials.getUsername(),
            AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken(),
            AppConfig.ENC_FILE_NAME, aesHandler.getOutput()
        );
        return prepareAndSendPostRequest(postData);
    }
}
