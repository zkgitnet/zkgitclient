/**
 * Provides the classes and commands related to login functionality within the ZkGitClient application.
 * <p>
 * This package includes various commands to handle user login, manage authentication credentials,
 * and interact with the remote server. It also contains encryption handling for sensitive data
 * like passwords and AES keys, ensuring secure communication with the server.
 * </p>
 * <p>
 * The classes implement command-line operations to:
 * <ul>
 *   <li>Request an encrypted AES key from a remote server</li>
 *   <li>Handle login-related functionalities such as password entry</li>
 *   <li>Use encryption techniques (PBKDF2, AES, RSA) for secure data management</li>
 * </ul>
 * </p>
 */
package se.miun.dt133g.zkgitclient.commands.login;
