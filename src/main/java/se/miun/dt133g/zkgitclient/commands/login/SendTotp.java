package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Command to handle the verification of a Time-based One-Time Password (TOTP).
 * This class prompts the user to enter a TOTP token, validates it, and sends it
 * to the server for verification.
 * @author Leif Rogell
 */
public final class SendTotp extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the TOTP verification process.
     * It prompts the user to input a TOTP token, validates the input, and sends it
     * to the server for verification.
     * @return the server response to the TOTP verification request or an exit command if the input is invalid.
     */
    @Override
    public String execute() {

        LOGGER.info("Verifying TOTP Token");

        return Optional.ofNullable(readUserInput(AppConfig.INFO_ENTER_TOTP,
                                                 AppConfig.INFO_INVALID_TOTP_INPUT,
                                                 AppConfig.REGEX_TOTP))
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
