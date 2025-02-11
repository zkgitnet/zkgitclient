package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.menu.MainMenu;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;

public final class RequestUserDeletion extends BaseCommandAccount implements Command {

    @Override public String execute() {
        return conn.getServerConnectivity() && credentials.hasAccessToken()
            ? handleUserDeletion()
            : utils.mapToString(Map.of(AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION));
    }

    private String handleUserDeletion() {
        return Optional.of(readUserInput(AppConfig.INFO_ENTER_USERNAME,
                              AppConfig.INFO_INVALID_USERNAME,
                              AppConfig.REGEX_USERNAME))
            .filter(userToDelete -> !AppConfig.ERROR_KEY.equals(userToDelete) && !AppConfig.COMMAND_EXIT.equals(userToDelete))
            .map(userToDelete -> {
                credentials.setUserToModify(userToDelete);
                return MainMenu.INSTANCE.confirmChoice() ? requestUserDeletion() : AppConfig.NONE;
            })
            .orElse(AppConfig.COMMAND_EXIT);
    }

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
