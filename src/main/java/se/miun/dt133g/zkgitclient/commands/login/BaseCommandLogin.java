package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.BaseCommand;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.menu.MenuTerminal;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Abstract base class for handling login-related commands, including password management and encryption.
 * This class supports reading user passwords securely, encrypting passwords with different encryption handlers,
 * and provides methods for managing login credentials.
 * It is intended to be extended by specific login-related command classes.
 * @author Leif Rogell
 */
public abstract class BaseCommandLogin extends BaseCommand {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    protected EncryptionHandler pbkdf2Handler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_PBKDF2);
    protected EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);
    protected EncryptionHandler rsaHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_RSA);

    /**
     * Reads a password input from the user with a prompt, validates it against a regular expression,
     * and processes the password if valid. Supports multiple attempts and error messages.
     * @param inputPrompt the prompt message to display to the user
     * @param errorMessage the message to display if the input doesn't match the regex
     * @param regexPattern the pattern that the input must match to be considered valid
     * @return the valid password if successfully entered, or an error message if invalid after all attempts
     */
    public String readPassword(final String inputPrompt,
                               final String errorMessage,
                               final String regexPattern) {
        try {
            for (int attempts = 0; attempts < AppConfig.MAX_NUM_INPUT_ATTEMPTS; attempts++) {
                try {
                    String password = Optional.ofNullable(MenuTerminal.INSTANCE.getLineReader()
                                                          .readLine(inputPrompt, '*'))
                        .map(String::trim)
                        .orElse(AppConfig.ZERO_STRING);

                    if (AppConfig.ZERO_STRING.equals(password)) {
                        return AppConfig.ZERO_STRING;
                    } else if (Pattern.matches(regexPattern, password)) {
                        return processValidPassword(password);
                    } else {
                        System.out.println(errorMessage);
                    }
                } catch (UserInterruptException | EndOfFileException e) {
                    return AppConfig.ZERO_STRING;
                }
            }
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            return "ERROR";
        }

        return "ERROR";
    }

    /**
     * Processes the valid password by setting it in the credentials and clearing the password buffer.
     * @param password the valid password to be processed
     * @return a success message indicating that the password has been successfully processed
     */
    private String processValidPassword(final String password) {
        credentials.setPassword(password.toCharArray());
        Arrays.fill(password.toCharArray(), AppConfig.NULL_TERMINATOR);
        return AppConfig.COMMAND_SUCCESS;
    }
}
