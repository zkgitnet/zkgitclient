/**
 * This package contains classes responsible for handling the main menu and menu-related functionalities
 * for the ZkGitClient application. It provides an interactive menu interface for users to perform various
 * actions related to the application, such as managing user accounts, repositories, and connection settings.
 *
 * <p>
 * The main class in this package is {@link MainMenu}, which displays the menu options, processes user input,
 * and triggers actions based on the user's choice. It interacts with
 * other components such as the {@link CommandManager},
 * {@link ConnectionManager}, and {@link UserCredentials} to execute commands and retrieve necessary data.
 * </p>
 *
 * <p>
 * The {@link MenuMethods} class serves as a base class for common methods shared across menu-related actions,
 * such as checking login status, displaying connection status, exporting encryption keys, and interacting with
 * user and repository lists.
 * </p>
 */
package se.miun.dt133g.zkgitclient.menu;
