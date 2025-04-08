package se.miun.dt133g.zkgitclient;

import se.miun.dt133g.zkgitclient.menu.MainMenu;
import se.miun.dt133g.zkgitclient.menu.MenuTerminal;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.MenuItems;

import org.jline.reader.UserInterruptException;

import java.util.logging.Logger;

/**
 * The {@code ZkGit} class is the entry point for the ZK Git Client application.
 * It initializes and runs the main menu, handles user input, and manages the flow of the program.
 * @author Leif Rogell
 */
public final class ZkGit {

    private static final Logger LOGGER = ZkGitLogger.getLogger(ZkGit.class);

    /**
     * Private constructor to prevent instantiation.
     */
    private ZkGit() { }

    /**
     * The main method that starts the ZK Git Client, initializes the main menu,
     * and handles user input for navigating through the menu options.
     * @param args Command-line arguments (not used in this implementation).
     */
    public static void main(final String[] args) {

        LOGGER.info("Starting ZK Git Client...");

        try {
            MainMenu.INSTANCE.displayHeader();
            MainMenu.INSTANCE.displayStatus();

            while (true) {
                MainMenu.INSTANCE.displayMenu();

                String choice = null;
                try {
                    choice = MenuTerminal.INSTANCE.getLineReader().readLine();
                } catch (UserInterruptException e) {
                    MainMenu.INSTANCE.performAction(MenuItems.CHOICE_E);
                }

                MainMenu.INSTANCE.performAction(choice);
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }
}
