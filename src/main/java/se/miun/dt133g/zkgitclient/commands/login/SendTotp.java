package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;

public final class SendTotp extends BaseCommandLogin implements Command {

    @Override public String execute() {
        return Optional.ofNullable(readUserInput(AppConfig.INFO_ENTER_TOTP, AppConfig.INFO_INVALID_TOTP_INPUT, AppConfig.REGEX_TOTP))
                       .filter(token -> !AppConfig.ERROR_KEY.equals(token))
                       .map(token -> {
                           credentials.setTotpToken(token);
                           Map<String, String> postData = Map.of(
                               AppConfig.COMMAND_KEY, AppConfig.COMMAND_VALIDATE_TOTP,
                               AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber(),
                               AppConfig.CREDENTIAL_USERNAME, credentials.getUsername(),
                               AppConfig.CREDENTIAL_TOTP_TOKEN, token
                           );
                           return prepareAndSendPostRequest(postData);
                       })
                       .orElse(AppConfig.COMMAND_EXIT);
    }
}
