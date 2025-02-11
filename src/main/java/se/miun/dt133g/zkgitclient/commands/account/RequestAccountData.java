package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public final class RequestAccountData extends BaseCommandAccount implements Command {

    private final String ERROR_MESSAGE = "Could not fetch account data";

    @Override public String execute() {
        return Optional.of(conn.getServerConnectivity())
            .filter(valid -> credentials.hasAccessToken())
            .map(valid -> {
                    Map<String, String> postData = new HashMap<>();
                    postData.put(AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_ACCOUNT_DATA);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber());
                    postData.put(AppConfig.CREDENTIAL_USERNAME, credentials.getUsername());
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken());
                    return prepareAndSendPostRequest(postData);
                })
            .orElseGet(() -> {
                    return createErrorResponse(ERROR_MESSAGE);
                });
    }
}
