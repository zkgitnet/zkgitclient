package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Command to retrieve the current login status from the remote server.
 * It validates the access token and returns the server's response regarding the login status.
 * @author Leif Rogell
 */
public final class GetLoginStatus extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the command to retrieve the current login status.
     * It checks if the access token is available and valid before sending a request to the server.
     * @return the server's response regarding the login status, or an error message if invalid.
     */
    @Override
    public String execute() {

        Map<String, String> postData = new HashMap<>();
        LOGGER.info("Retrieving current login status");

        return Optional.ofNullable(conn.getServerConnectivity())
            .filter(valid -> credentials.hasAccessToken())
            .map(valid -> {
                    postData.put(AppConfig.COMMAND_KEY,
                                 AppConfig.COMMAND_VALIDATE_ACCESS_TOKEN);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR,
                                 Optional.ofNullable(credentials.getAccountNumber()).orElse(""));
                    postData.put(AppConfig.CREDENTIAL_USERNAME,
                                 Optional.ofNullable(credentials.getUsername()).orElse(""));
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN,
                                 Optional.ofNullable(credentials.getAccessToken()).orElse(""));
                    return Optional.ofNullable(conn.sendPostRequest(postData))
                        .orElse(AppConfig.ERROR_KEY);

                })
            .orElseGet(() -> {
                    postData.put(AppConfig.COMMAND_KEY, AppConfig.NONE);
                    return Optional.ofNullable(conn.sendPostRequest(postData))
                        .orElse(AppConfig.ERROR_KEY);

                });
    }
}
