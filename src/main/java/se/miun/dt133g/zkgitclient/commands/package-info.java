/**
 * This package contains classes that manage and execute different commands
 * for the ZkGit client. These commands encompass various operations, such as
 * user login, logout, handling encryption keys, managing repositories, and
 * interacting with the server. The commands are encapsulated in the form of
 * command objects that are executed by the {@link CommandManager}.
 *
 * <p>Classes in this package typically extend the {@link BaseCommand} class
 * and implement the {@link Command} interface. The {@link CommandManager}
 * is responsible for managing the execution flow of commands and handling
 * responses from the server.</p>
 *
 * <p>Common functionalities across the commands include making HTTP requests
 * to the server, processing user inputs, handling encryption/decryption tasks,
 * and managing user credentials and session states.</p>
 */
package se.miun.dt133g.zkgitclient.commands;
