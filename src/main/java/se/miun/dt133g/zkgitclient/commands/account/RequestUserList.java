package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;
import se.miun.dt133g.zkgitclient.support.Utils;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

public final class RequestUserList extends BaseCommandAccount implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private Utils utils = Utils.getInstance();

    @Override public String execute() {
        if (conn.getServerConnectivity() && credentials.hasAccessToken()) {
            return requestUserList();
        } else {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put(AppConfig.ERROR_KEY, AppConfig.ERROR_CONNECTION);
            return utils.mapToString(responseMap);
        }
    }

    private String requestUserList() {
        Map<String, String> postData = new HashMap<>();
        postData.put(AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_USER_LIST);
        postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber());
        postData.put(AppConfig.CREDENTIAL_USERNAME, credentials.getUsername());
        postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken());

        return conn.sendPostRequest(postData);
    }
}
