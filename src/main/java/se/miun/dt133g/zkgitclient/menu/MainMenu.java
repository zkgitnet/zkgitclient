package se.miun.dt133g.zkgitclient.menu;

import se.miun.dt133g.zkgitclient.connection.ConnectionManager;
import se.miun.dt133g.zkgitclient.commands.CommandManager;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;
import se.miun.dt133g.zkgitclient.support.MenuItems;
import se.miun.dt133g.zkgitclient.support.FileUtils;

import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Logger;

public class MainMenu extends MenuMethods {

    public static final MainMenu INSTANCE = new MainMenu();
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    private boolean connected = false;
    private boolean socket = false;

    private MainMenu() { }

    public void displayHeader() {
        System.out.println(MenuItems.HEADER_ZKGIT);
    }

    public void performAction(String choice) {
        switch (choice.replaceAll("\\s+", AppConfig.NONE).toUpperCase()) {
        case MenuItems.CHOICE_ONE -> handleUserCommands(choice);
        case MenuItems.CHOICE_TWO -> handleUserCommands(choice);
        case MenuItems.CHOICE_THREE -> displayStatus();
        case MenuItems.CHOICE_FOUR -> displayAccountData();
        case MenuItems.CHOICE_FIVE -> ConnectionManager.INSTANCE.setGitPort();
        case MenuItems.CHOICE_SIX -> getUserList();
        case MenuItems.CHOICE_SEVEN -> handleUserCommands(choice);
        case MenuItems.CHOICE_EIGHT -> handleUserCommands(choice);
        case MenuItems.CHOICE_NINE -> System.out.println("Method not included in test suite");
        case MenuItems.CHOICE_TEN -> handleUserCommands(choice);
        case MenuItems.CHOICE_ELEVEN -> System.out.println("Method not included in test suite");
        case MenuItems.CHOICE_TWELVE -> System.out.println("Method not included in test suite");
        case MenuItems.CHOICE_THIRTEEN -> getRepoList();
        case MenuItems.CHOICE_FOURTEEN -> handleUserCommands(choice);
        case MenuItems.CHOICE_FIFTEEN -> exportEncryptionKeys();
        case MenuItems.CHOICE_ZERO -> System.out.println(MenuItems.STATUS_BACKGROUND);
        case MenuItems.CHOICE_E -> exitApplication();
        default -> System.out.println(MenuItems.STATUS_INVALID_CHOICE);
        }
    }

    private void handleUserCommands(String choice) {
        if (choice.equals(MenuItems.CHOICE_SEVEN)) {
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_GENERATE_USER);
        } else if (choice.equals(MenuItems.CHOICE_EIGHT)) {
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_USER_PRIV_CHANGE);
        } else if (choice.equals(MenuItems.CHOICE_TEN)) {
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_USER_DELETE);
        } else if (choice.equals(MenuItems.CHOICE_ONE)) {
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_LOGIN);
            displayStatus();
        } else if (choice.equals(MenuItems.CHOICE_TWO)) {
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_LOGOUT);
            displayStatus();
        } else if (choice.equals(MenuItems.CHOICE_NINE)) {
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_CHANGE_PASSWORD);
        } else if (choice.equals(MenuItems.CHOICE_FOURTEEN)) {
            CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_REPO_DELETE);
        }
    }

    public void displayStatus() {
        System.out.println(MenuItems.HEADER_STATUS);
        String format = String.format(MenuItems.FORMAT_DISPLAY_STATUS,
                                      MenuItems.COLUMN_WIDTH_STATUS);

        checkLogin(false);
        connected = displayConnectionStatus(format);
        socket = displaySocketStatus(format);

        if (!login) {
            System.out.printf(format,
                              MenuItems.STATUS_LOGIN,
                              MenuItems.STATUS_NO);
        } else {
            System.out.printf(format,
                              MenuItems.STATUS_LOGIN,
                              MenuItems.STATUS_YES);
            System.out.printf(format,
                              MenuItems.STATUS_ACCOUNT_NUMBER,
                              utils.formatWithSpace(credentials.getAccountNumber()));
            System.out.printf(format,
                              MenuItems.STATUS_USERNAME,
                              credentials.getUsername());
        }
    }

    public void displayMenu() {
        System.out.println(MenuItems.HEADER_MENU);
        String format = String.format(MenuItems.FORMAT_DISPLAY_MENU, MenuItems.COLUMN_WIDTH_MENU);

        List<String> menuNumbers = Arrays.asList(MenuItems.ITEM_ONE,
                                                 MenuItems.ITEM_TWO,
                                                 MenuItems.ITEM_THREE,
                                                 MenuItems.ITEM_FOUR,
                                                 MenuItems.ITEM_FIVE,
                                                 MenuItems.ITEM_SIX,
                                                 MenuItems.ITEM_SEVEN,
                                                 MenuItems.ITEM_EIGHT,
                                                 MenuItems.ITEM_NINE,
                                                 MenuItems.ITEM_TEN,
                                                 MenuItems.ITEM_ELEVEN,
                                                 MenuItems.ITEM_TWELVE,
                                                 MenuItems.ITEM_THIRTEEN,
                                                 MenuItems.ITEM_FOURTEEN,
                                                 MenuItems.ITEM_FIFTEEN,
                                                 MenuItems.ITEM_ZERO,
                                                 MenuItems.ITEM_E);

        List<String> menuCommands = Arrays.asList(MenuItems.ITEM_LOGIN,
                                                  MenuItems.ITEM_LOGOUT,
                                                  MenuItems.ITEM_STATUS,
                                                  MenuItems.ITEM_ACCOUNT_DATA,
                                                  MenuItems.ITEM_CHANGE_PORT,
                                                  MenuItems.ITEM_LIST_USER,
                                                  MenuItems.ITEM_ADD_USER,
                                                  MenuItems.ITEM_MODIFY_USER,
                                                  MenuItems.ITEM_CHANGE_PASSWORD,
                                                  MenuItems.ITEM_DELETE_USER,
                                                  MenuItems.ITEM_LIST_PAYMENTS,
                                                  MenuItems.ITEM_EXPORT_INVOICE,
                                                  MenuItems.ITEM_LIST_REPOS,
                                                  MenuItems.ITEM_DELETE_REPO,
                                                  MenuItems.ITEM_EXPORT_KEYS,
                                                  MenuItems.ITEM_CLOSE,
                                                  MenuItems.ITEM_STOP_EXIT);

        List<Integer> rowSeparation = Arrays.asList(2, 5, 10, 12, 14, 15);

        for (int i = 0; i < menuNumbers.size(); i++) {
            if (rowSeparation.contains(i)) System.out.println();
            System.out.printf(format, menuNumbers.get(i), menuCommands.get(i));
        }

        System.out.print(AppConfig.NEW_LINE + MenuItems.PROMPT);
    }
    
    private void exitApplication() {
        CommandManager.INSTANCE.executeCommand(AppConfig.COMMAND_REQUEST_LOGOUT);
        fileUtils.cleanTmpFiles();
        credentials.clearUserData();
        System.out.println(MenuItems.STATUS_EXIT);
        LOGGER.info(MenuItems.STATUS_EXIT);
        System.exit(0);
    }
}
