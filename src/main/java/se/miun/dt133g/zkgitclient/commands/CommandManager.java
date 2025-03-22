package se.miun.dt133g.zkgitclient.commands;

import se.miun.dt133g.zkgitclient.connection.ConnectionManager;
import se.miun.dt133g.zkgitclient.commands.login.LoginRequest;
import se.miun.dt133g.zkgitclient.commands.login.LogoutRequest;
import se.miun.dt133g.zkgitclient.commands.login.SendTotp;
import se.miun.dt133g.zkgitclient.commands.login.DecryptAccessToken;
import se.miun.dt133g.zkgitclient.commands.login.AesKeyRequest;
import se.miun.dt133g.zkgitclient.commands.login.DecryptAesKey;
import se.miun.dt133g.zkgitclient.commands.login.GetLoginStatus;
import se.miun.dt133g.zkgitclient.commands.git.SendRepoFile;
import se.miun.dt133g.zkgitclient.commands.git.GetRepoFile;
import se.miun.dt133g.zkgitclient.commands.git.GetRepoFileInfo;
import se.miun.dt133g.zkgitclient.commands.files.CleanTmpFiles;
import se.miun.dt133g.zkgitclient.commands.account.RequestAccountData;
import se.miun.dt133g.zkgitclient.commands.account.RequestUserList;
import se.miun.dt133g.zkgitclient.commands.account.RequestRepoList;
import se.miun.dt133g.zkgitclient.commands.account.RequestNewUser;
import se.miun.dt133g.zkgitclient.commands.account.RequestUserDeletion;
import se.miun.dt133g.zkgitclient.commands.account.RequestUserPrivChange;
import se.miun.dt133g.zkgitclient.commands.account.RequestRepoDeletion;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public final class CommandManager extends BaseCommand {

    public static final CommandManager INSTANCE = new CommandManager();
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private final Map<String, Command> commandMap = new HashMap<>();

    private CommandManager() {
        commandMap.put(AppConfig.COMMAND_REQUEST_LOGIN, new LoginRequest());
        commandMap.put(AppConfig.COMMAND_REQUEST_LOGOUT, new LogoutRequest());
        commandMap.put(AppConfig.COMMAND_VALIDATE_TOTP, new SendTotp());
        commandMap.put(AppConfig.COMMAND_DECRYPT_ACCESS_TOKEN, new DecryptAccessToken());
        commandMap.put(AppConfig.COMMAND_REQUEST_AES_KEY, new AesKeyRequest());
        commandMap.put(AppConfig.COMMAND_DECRYPT_AES_KEY, new DecryptAesKey());
        commandMap.put(AppConfig.COMMAND_LOGIN_STATUS, new GetLoginStatus());
        commandMap.put(AppConfig.COMMAND_SEND_REPO_FILE, new SendRepoFile());
        commandMap.put(AppConfig.COMMAND_REQUEST_REPO_FILE, new GetRepoFile());
        commandMap.put(AppConfig.COMMAND_REQUEST_ACCOUNT_DATA, new RequestAccountData());
        commandMap.put(AppConfig.COMMAND_REQUEST_USER_LIST, new RequestUserList());
        commandMap.put(AppConfig.COMMAND_REQUEST_REPO_LIST, new RequestRepoList());
        commandMap.put(AppConfig.COMMAND_CLEAN, new CleanTmpFiles());
        commandMap.put(AppConfig.COMMAND_GENERATE_USER, new RequestNewUser());
        commandMap.put(AppConfig.COMMAND_REQUEST_USER_DELETE, new RequestUserDeletion());
        commandMap.put(AppConfig.COMMAND_REQUEST_USER_PRIV_CHANGE, new RequestUserPrivChange());
        commandMap.put(AppConfig.COMMAND_REQUEST_REPO_DELETE, new RequestRepoDeletion());
    }

    public Map<String, String> executeCommand(final String commandName) {
        Command command = commandMap.get(commandName);
        Map<String, String> latestResponseMap = new HashMap<>();

        LOGGER.fine("Executing command: " + commandName);

        while (command != null) {
            String response = command.execute();
            if (AppConfig.COMMAND_EXIT.equals(response)) {
                break;
            }

            latestResponseMap = extractResponseToMap(response);

            for (Map.Entry<String, String> entry : latestResponseMap.entrySet()) {
                if (latestResponseMap.containsKey(AppConfig.ERROR_KEY)) {
                    LOGGER.warning("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                } else if (latestResponseMap.containsKey(AppConfig.COMMAND_SUCCESS)) {
                    LOGGER.fine("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                } else {
                    LOGGER.finest("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }
            }

            if (latestResponseMap.containsKey(AppConfig.COMMAND_SUCCESS) ||
                (latestResponseMap.containsKey(AppConfig.ERROR_KEY)
                 && !latestResponseMap.containsValue("Connection failure"))) {
                ConnectionManager.INSTANCE.setServerConnectivity(true);
            }

            if (latestResponseMap.containsKey(AppConfig.ERROR_KEY)) {
                String errorMessage = latestResponseMap.get(AppConfig.ERROR_KEY);
                if (!errorMessage.contains("No user")
                    && !errorMessage.contains("CommandManager")
                    && !errorMessage.contains("No valid login")) {
                    LOGGER.warning(AppConfig.NEW_LINE + AppConfig.ERROR_KEY
                                   + AppConfig.COLON_SEPARATOR + " " + errorMessage);
                    break;
                }
            }

            command = Optional.ofNullable(latestResponseMap.get(AppConfig.COMMAND_KEY))
                .map(commandMap::get)
                .orElse(null);

            if (command != null) {
                extractUserCredentials(latestResponseMap);
            }
        }

        return latestResponseMap;
    }
}
