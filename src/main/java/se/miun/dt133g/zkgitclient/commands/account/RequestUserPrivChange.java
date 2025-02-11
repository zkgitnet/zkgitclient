package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.menu.MainMenu;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;

public final class RequestUserPrivChange extends BaseCommandAccount implements Command {

    @Override public String execute() {
        return conn.getServerConnectivity() && credentials.hasAccessToken()
            ? handleUserPrivChange()
            : utils.mapToString(Map.of(AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION));
    }

    private String handleUserPrivChange() {
        return Optional.of(readUserInput(AppConfig.INFO_ENTER_USERNAME,
                              AppConfig.INFO_INVALID_USERNAME,
                              AppConfig.REGEX_USERNAME))
            .filter(userToModify -> !AppConfig.ERROR_KEY.equals(userToModify) && !AppConfig.COMMAND_EXIT.equals(userToModify))
            .map(userToModify -> {
                credentials.setUserToModify(userToModify);
                return MainMenu.INSTANCE.confirmChoice() ? requestAdminToUser() : AppConfig.NONE;
            })
            .orElse(AppConfig.COMMAND_EXIT);
    }

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
