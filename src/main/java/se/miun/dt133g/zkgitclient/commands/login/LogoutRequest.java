package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.commands.CommandManager;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public final class LogoutRequest extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    @Override public String execute() {

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
