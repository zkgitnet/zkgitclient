/**
 * Provides the core functionality for the ZK Git Client application.
 *
 * The {@code zkgitclient} package includes the main client interface,
 * commands for interacting with the server, menu handling, and utilities
 * for encryption and connection management. It allows users to perform
 * Git-related operations securely by leveraging encryption, user
 * authentication, and connection management features.
 *
 * The main class in this package, {@link ZkGit}, serves as the entry
 * point for the application and runs the interactive terminal menu.
 * Through the menu, users can execute various commands such as logging
 * in, managing repositories, and handling user accounts.
 *
 * Key components include:
 * <ul>
 *   <li>{@link se.miun.dt133g.zkgitclient.commands} -
 * A collection of command classes for performing specific tasks.</li>
 *   <li>{@link se.miun.dt133g.zkgitclient.menu} - The user interface
 * components to display menus and handle user input.</li>
 *   <li>{@link se.miun.dt133g.zkgitclient.connection} - Manages network
 * communication between the client and the server.</li>
 *   <li>{@link se.miun.dt133g.zkgitclient.crypto} - Provides encryption and
 * decryption mechanisms for securing communication.</li>
 *   <li>{@link se.miun.dt133g.zkgitclient.user} - Handles user credentials
 * and user session information.</li>
 *   <li>{@link se.miun.dt133g.zkgitclient.logger} - Custom logging functionality
 * for debugging and error reporting.</li>
 * </ul>
 *
 * This software is designed to facilitate secure Git-based operations in a terminal interface, with an emphasis
 * on encryption and user authentication.
 *
 * @see se.miun.dt133g.zkgitclient.ZkGit
 * @see se.miun.dt133g.zkgitclient.commands.CommandManager
 * @see se.miun.dt133g.zkgitclient.menu.MainMenu
 * @see se.miun.dt133g.zkgitclient.connection.ConnectionManager
 * @see se.miun.dt133g.zkgitclient.crypto.EncryptionHandler
 */
package se.miun.dt133g.zkgitclient;
