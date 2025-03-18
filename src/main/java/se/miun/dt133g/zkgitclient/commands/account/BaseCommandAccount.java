package se.miun.dt133g.zkgitclient.commands.account;

import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.commands.BaseCommand;
import se.miun.dt133g.zkgitclient.commands.login.BaseCommandLogin;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.logging.Logger;

public abstract class BaseCommandAccount extends BaseCommandLogin {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    protected EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);
    
}
