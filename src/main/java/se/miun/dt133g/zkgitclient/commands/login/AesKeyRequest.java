package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;

public final class AesKeyRequest extends BaseCommandLogin implements Command {

    @Override public String execute() {
        Map<String, String> postData = new HashMap<>();
        postData.put(AppConfig.COMMAND_KEY, AppConfig.COMMAND_REQUEST_AES_KEY);
        postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR, credentials.getAccountNumber());
        postData.put(AppConfig.CREDENTIAL_USERNAME, credentials.getUsername());
        postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN, credentials.getAccessToken());

        return conn.sendPostRequest(postData);
    }

}
