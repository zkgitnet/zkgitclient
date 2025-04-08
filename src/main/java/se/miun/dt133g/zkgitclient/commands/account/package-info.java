/**
 * This package contains account-related command implementations for the ZkGitClient application.
 *
 * <p>
 * These commands are responsible for handling account creation and management functionalities,
 * including the creation of new users, encryption of sensitive user data using AES, RSA, and PBKDF2 algorithms,
 * and secure communication with the backend server.
 * </p>
 *
 * <p>
 * Each command class implements the {@link se.miun.dt133g.zkgitclient.commands.Command} interface,
 * enabling consistent invocation patterns across all command types.
 * The {@link BaseCommandAccount} class provides common functionality and dependencies related to account-level
 * encryption handling and logging.
 * </p>
 */
package se.miun.dt133g.zkgitclient.commands.account;
