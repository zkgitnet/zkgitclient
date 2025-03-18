package se.miun.dt133g.zkgitclient;
 
import se.miun.dt133g.zkgitclient.menu.MainMenu;
import se.miun.dt133g.zkgitclient.menu.MenuTerminal;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.MenuItems;

import org.jline.reader.UserInterruptException;

import java.util.logging.Logger;
 
public final class ZkGit {

    private final static Logger LOGGER = ZkGitLogger.getLogger(ZkGit.class);
 
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
