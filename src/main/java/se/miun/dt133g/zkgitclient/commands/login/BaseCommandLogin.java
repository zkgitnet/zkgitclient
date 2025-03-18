package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.BaseCommand;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.menu.MenuTerminal;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.logging.Logger;

public abstract class BaseCommandLogin extends BaseCommand {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    protected EncryptionHandler pbkdf2Handler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_PBKDF2);
    protected EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);
    protected EncryptionHandler rsaHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_RSA);

    public String readPassword(String inputPrompt, String errorMessage, String regexPattern) {
        try {
            for (int attempts = 0; attempts < AppConfig.MAX_NUM_INPUT_ATTEMPTS; attempts++) {
                try {
                    String password = Optional.ofNullable(
                                                          MenuTerminal.INSTANCE.getLineReader().readLine(inputPrompt, '*'))
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
    
    private String processValidPassword(final String password) {
        credentials.setPassword(password.toCharArray());
        Arrays.fill(password.toCharArray(), AppConfig.NULL_TERMINATOR);
        return AppConfig.COMMAND_SUCCESS;
    }
}
