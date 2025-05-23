package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.commands.login.BaseCommandLogin;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.logging.Logger;

/**
 * Abstract base class for account-related commands.
 * Extends {@link BaseCommandLogin} and provides shared logic and encryption handler
 * setup for commands dealing with user accounts.
 * @author Leif Rogell
 */
public abstract class BaseCommandAccount extends BaseCommandLogin {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    protected EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);

}
