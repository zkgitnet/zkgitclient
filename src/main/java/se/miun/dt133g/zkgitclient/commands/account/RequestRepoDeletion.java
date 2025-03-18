package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.menu.MainMenu;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public final class RequestRepoDeletion extends BaseCommandAccount implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    @Override public String execute() {
        return conn.getServerConnectivity() && credentials.hasAccessToken()
            ? handleUserDeletion()
            : utils.mapToString(Map.of(AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION));
    }

    private String handleUserDeletion() {
        return Optional.of(readUserInput(AppConfig.INFO_ENTER_REPONAME,
                              AppConfig.INFO_INVALID_USERNAME,
                              AppConfig.REGEX_REPONAME))
            .filter(repoToDelete -> !AppConfig.ERROR_KEY.equals(repoToDelete) && !AppConfig.COMMAND_EXIT.equals(repoToDelete))
            .map(repoToDelete -> {
                currentRepo.setRepoName(repoToDelete);
                return MainMenu.INSTANCE.confirmChoice() ? requestRepoDeletion() : AppConfig.NONE;
            })
            .orElse(AppConfig.COMMAND_EXIT);
    }

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
