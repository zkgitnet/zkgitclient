package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.commands.CommandManager;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public final class LoginRequest extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    @Override public String execute() {

        if (conn.getServerConnectivity()) {

            LOGGER.info("Initiating login process");
            Map<String, String> loginStatus =
                CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_LOGIN_STATUS);

            return !loginStatus.containsValue(AppConfig.STATUS_AUTHORIZATION_VALID)
                ? handleLoginProcess() : AppConfig.STATUS_LOGGED_IN;

        } else {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put(AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION);
            return utils.mapToString(responseMap);
        }
    }

    private String handleLoginProcess() {
        return Optional.of(readUserInput(AppConfig.INFO_ENTER_ACCOUNT_NUMBER,
                              AppConfig.INFO_INVALID_ACCOUNT_NUMBER_INPUT,
                              AppConfig.REGEX_ACCOUNT_NUMBER))
                       .filter(accountNumber -> !AppConfig.ERROR_KEY.equals(accountNumber))
                       .map(accountNumber -> {
                           credentials.setAccountNumber(accountNumber);
                           return readUserInput(AppConfig.INFO_ENTER_USERNAME,
                              AppConfig.INFO_INVALID_USERNAME,
                              AppConfig.REGEX_USERNAME);
                       })
                       .filter(username -> !AppConfig.ERROR_KEY.equals(username))
                       .map(username -> {
                               credentials.setUsername(username);
                               Map<String, String> postData = Map.of(AppConfig.COMMAND_KEY,
                                                                     AppConfig.COMMAND_GET_TOTP,
                                                                     AppConfig.CREDENTIAL_ACCOUNT_NR,
                                                                     credentials.getAccountNumber(),
                                                                     AppConfig.CREDENTIAL_USERNAME,
                                                                     credentials.getUsername());
                           return prepareAndSendPostRequest(postData);
                       })
                       .orElse(AppConfig.COMMAND_EXIT);
    }
}
