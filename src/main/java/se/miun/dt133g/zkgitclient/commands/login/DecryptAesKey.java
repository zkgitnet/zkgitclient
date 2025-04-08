package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.logging.Logger;

/**
 * Command to decrypt the AES key using the user's private RSA key.
 * This class handles the decryption of the AES key required for encryption operations.
 * @author Leif Rogell
 */
public final class DecryptAesKey extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the decryption of the AES key using the RSA decryption method.
     * The private RSA key is used to decrypt the encrypted AES key, and the AES key is set to the credentials.
     * @return the decrypted AES key.
     */
    @Override
    public String execute() {
        LOGGER.info("Decrypting AES Key");

        rsaHandler.setRsaKey(credentials.getPrivRsa());
        rsaHandler.setInput(credentials.getEncAesKey());
        rsaHandler.decrypt();
        credentials.setAesKey(rsaHandler.getOutput());

        LOGGER.finest(rsaHandler.getOutput());
        return rsaHandler.getOutput();
    }
}
