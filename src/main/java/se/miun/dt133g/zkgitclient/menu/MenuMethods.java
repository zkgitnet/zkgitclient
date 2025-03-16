package se.miun.dt133g.zkgitclient.menu;

import se.miun.dt133g.zkgitclient.connection.ConnectionManager;
import se.miun.dt133g.zkgitclient.commands.CommandManager;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.user.UserCredentials;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.FileUtils;
import se.miun.dt133g.zkgitclient.support.MenuItems;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Comparator;
import java.util.stream.Collectors;

public abstract class MenuMethods {

    protected UserCredentials credentials = UserCredentials.getInstance();
    protected FileUtils fileUtils = FileUtils.getInstance();
    protected Utils utils = Utils.getInstance();
    protected boolean login = false;

    public boolean confirmChoice() {
        System.out.println(AppConfig.NEW_LINE + MenuItems.INFO_UNDO);
        System.out.print(MenuItems.PROMPT_CONFIRM);
        return new Scanner(System.in).nextLine().trim().toUpperCase().startsWith("Y");
    }

    protected void checkLogin(boolean output) {
        try {
            Map<String, String> loginStatus =
                CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_LOGIN_STATUS);
            login = loginStatus.containsValue(AppConfig.STATUS_AUTHORIZATION_VALID);
            if (!login && output) {
                System.out.println(AppConfig.NEW_LINE
                                   + AppConfig.ERROR_KEY
                                   + AppConfig.COLON_SEPARATOR
                                   + AppConfig.SPACE_SEPARATOR
                                   + AppConfig.STATUS_NOT_LOGGED_IN);
            }
        } catch (NullPointerException e) {
                System.out.println("checkLogin error: " + e.getMessage());
        }
    }

    protected boolean displayConnectionStatus(String format) {
        boolean internetConnection =
            ConnectionManager.INSTANCE.getInternetConnectivity();
        boolean serverConnection =
            ConnectionManager.INSTANCE.getServerConnectivity();
        
        System.out.printf(format,
                          MenuItems.STATUS_CONNECTION,
                          internetConnection
                          ? MenuItems.STATUS_AVAILABLE
                          : MenuItems.STATUS_NOT_AVAILABLE);

        System.out.printf(format,
                          MenuItems.STATUS_ZKGIT_CONNECTION,
                          serverConnection
                          ? MenuItems.STATUS_AVAILABLE
                          : MenuItems.STATUS_NOT_AVAILABLE);            


        if (internetConnection) {
            String ipVersion =
                ConnectionManager.INSTANCE.getIpv6Connectivity()
                ? MenuItems.STATUS_IPV6
                : MenuItems.STATUS_IPV4;
            System.out.printf(format,
                              MenuItems.STATUS_IP_VERSION,
                              ipVersion);
        }

        return serverConnection;
    }

    protected boolean displaySocketStatus(String format) {
        int port = ConnectionManager.INSTANCE.getGitPort();
        boolean socket = port != 0;
        System.out.printf(format,
                          MenuItems.STATUS_GIT_SOCKET,
                          socket
                          ? Integer.toString(port)
                          : MenuItems.STATUS_NONE);
        return socket;
    }

    protected String convertBytesToMb(String bytes) {
        return BigDecimal.valueOf(Integer.parseInt(bytes))
            .divide(BigDecimal.valueOf(1_000_000), 2, RoundingMode.HALF_UP)
            .toString();
    }

    protected boolean canParseInt(String input) {
        return input.matches("-?\\d+");
    }

    protected void exportEncryptionKeys() {
        Scanner scanner = new Scanner(System.in);

        System.out.println(MenuItems.HEADER_EXPORT_KEYS);
        System.out.println(MenuItems.INFO_EXPORT_KEYS);

        String format = "%s%s%s%s";

        while (true) {
            System.out.printf("%s%s %s%n%s%s %s%n%s%s %s%n%s",
                              MenuItems.ITEM_ONE,
                              AppConfig.SPACE_SEPARATOR,
                              MenuItems.ITEM_EXPORT_RSA,
                              MenuItems.ITEM_TWO,
                              AppConfig.SPACE_SEPARATOR,
                              MenuItems.ITEM_EXPORT_AES,
                              MenuItems.ITEM_ZERO,
                              AppConfig.SPACE_SEPARATOR,
                              MenuItems.ITEM_RETURN,
                              MenuItems.ITEM_CHOICE);

            String choice = scanner.nextLine();
            switch (choice) {
            case MenuItems.CHOICE_ONE:
                fileUtils.saveStringToFile(
                                           credentials.getPrivRsa(),
                                           String.format(format,
                                                         credentials.getAccountNumber(),
                                                         AppConfig.UNDERSCORE,
                                                         credentials.getUsername(),
                                                         AppConfig.FILE_PRIV_RSA,
                                                         AppConfig.FILE_JSON)
                                           );
                break;
            case MenuItems.CHOICE_TWO:
                fileUtils.saveStringToFile(
                                           credentials.getAesKeyJson(),
                                           String.format(format,
                                                         credentials.getAccountNumber(),
                                                         AppConfig.UNDERSCORE,
                                                         credentials.getUsername(),
                                                         AppConfig.FILE_AES,
                                                         AppConfig.FILE_JSON)
                                           );
                break;
            case MenuItems.CHOICE_ZERO:
                return;
            default:
                System.out.println(MenuItems.STATUS_INVALID_CHOICE);
            }
        }
    }

    protected void displayAccountData() {
        System.out.println(MenuItems.HEADER_ACCOUNT_DATA);
        String format =
            String.format(MenuItems.FORMAT_ACCOUNT_DATA, MenuItems.COLUMN_WIDTH_ACCOUNT_DATA);
 
        checkLogin(true);
        if (!login) {
            return;
        }
 
        Map<String, String> data =
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_ACCOUNT_DATA);
 
        System.out.printf(format,
                          MenuItems.STATUS_ACCOUNT_NUMBER,
                          utils.formatWithSpace(credentials.getAccountNumber()));
 
        String expDate = data.get(AppConfig.DB_REPO_EXP_DATE);
        if (expDate != null) System.out.printf(format, MenuItems.STATUS_EXP_DATE, expDate);
 
        String totalUsers = data.get(AppConfig.DB_REPO_TOTAL_USERS);
        String maxUsers = data.get(AppConfig.DB_REPO_MAX_USERS);
        if (canParseInt(totalUsers) && canParseInt(maxUsers)) {
            System.out.printf(format,
                              MenuItems.STATUS_USERS,
                              totalUsers + MenuItems.OF_SEPARATOR
                              + maxUsers);
        }
 
        String totalSize = data.get(AppConfig.DB_REPO_TOTAL_SIZE);
        String maxStorage = data.get(AppConfig.DB_REPO_MAX_STORAGE);
        if (canParseInt(totalSize) && canParseInt(maxStorage)) {
            System.out.printf(format,
                              MenuItems.STATUS_STORAGE,
                              convertBytesToMb(totalSize)
                              + MenuItems.OF_SEPARATOR
                              + maxStorage
                              + " MB");
        }
 
        String totalRepos = data.get(AppConfig.DB_REPO_TOTAL_REPOS);
        if (canParseInt(totalRepos)) {
            System.out.printf(format,
                              MenuItems.STATUS_NUM_REPOS,
                              totalRepos);
        }
    }

    protected void getUserList() {
        checkLogin(true);

        if (!login) {
            return;
        }

        Map<String, String> userList =
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_USER_LIST);

        System.out.println("Received users list:");
        for (Map.Entry<String, String> entry : userList.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        
        if (userList.containsKey(AppConfig.ERROR_KEY)) {
            System.out.println(AppConfig.STATUS_NOT_ADMIN);
            return;
        }

        String format = String.format(MenuItems.FORMAT_USER_LIST,
                                      MenuItems.COLUMN_WIDTH_MENU,
                                      MenuItems.COLUMN_WIDTH_ACCOUNT_DATA);
        System.out.println(MenuItems.HEADER_USER_LIST);
        System.out.printf(format,
                          AppConfig.HASH,
                          MenuItems.TITLE_USERNAME,
                          MenuItems.TITLE_ROLE);
        System.out.println(MenuItems.TITLE_SEPARATOR);

        final int[] index = {1};
        userList.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(AppConfig.COMMAND_SUCCESS))
            .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(Integer::parseInt)))
            .forEach(entry -> {
                    try {
                        String[] entryData = entry.getValue().split(AppConfig.UNDERSCORE);
                        System.out.printf(format,
                                          index[0]++,
                                          entryData[0],
                                          entryData[1].equals("true") ? "Admin" : "User");
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println(e.getMessage());
                    }
                });
    }

    protected void getRepoList() {
        checkLogin(true);

        if (!login) {
            return;
        }

        String format = String.format(MenuItems.FORMAT_REPO_LIST,
                                      MenuItems.COLUMN_WIDTH_MENU,
                                      MenuItems.COLUMN_WIDTH_REPONAME,
                                      MenuItems.COLUMN_WIDTH_SIZE);
        EncryptionHandler aesHandler =
            EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);
        aesHandler.setAesKey(credentials.getAesKey());

        Map<String, String> repoList =
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_REPO_LIST);
        if (repoList.containsKey(AppConfig.ERROR_KEY)) {
            System.out.println(AppConfig.STATUS_NOT_ADMIN);
            return;
        }

        System.out.println(MenuItems.HEADER_REPO_LIST);
        System.out.printf(format,
                          AppConfig.HASH,
                          MenuItems.TITLE_REPONAME,
                          MenuItems.TITLE_SIZE,
                          MenuItems.TITLE_LAST_PUSH);
        System.out.println(MenuItems.TITLE_SEPARATOR);

        repoList.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(AppConfig.COMMAND_SUCCESS))
            .sorted(Map.Entry.comparingByKey(Comparator.comparingInt(Integer::parseInt)))
            .forEach(entry -> {
                    String[] repoValues = entry.getValue().split(AppConfig.UNDERSCORE);
                    aesHandler.setInput(repoValues[0]);
                    String input = repoValues[4].replaceAll("[\\[\\] ]", "");
                    String[] byteStrings = input.split(";");
                    byte[] byteArray = new byte[byteStrings.length];
                    try {
                        for (int i = 0; i < byteStrings.length; i++) {
                            byteArray[i] = Byte.parseByte(byteStrings[i]);
                        }
                        aesHandler.setIv(byteArray);
                        aesHandler.decrypt();
                        System.out.printf(format,
                                          entry.getKey(),
                                          aesHandler.getOutput().replace(AppConfig.ZIP_SUFFIX, AppConfig.NONE),
                                          convertBytesToMb(repoValues[1]),
                                          repoValues[3] + AppConfig.SPACE_SEPARATOR + AppConfig.FORWARD_BRACKET
                                          + repoValues[2] + AppConfig.BACKWARD_BRACKET);
                    } catch (NumberFormatException e) {
                        System.out.println(e.getMessage());
                    }
                });
    }
}
