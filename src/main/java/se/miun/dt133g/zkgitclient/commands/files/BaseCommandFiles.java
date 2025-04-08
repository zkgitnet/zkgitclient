package se.miun.dt133g.zkgitclient.commands.files;

import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.commands.BaseCommand;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.FileUtils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.logging.Logger;

/**
 * Abstract base class for file-related commands in the ZkGit client.
 * Provides shared utilities for file operations and AES encryption used across file commands.
 * @author Leif Rogell
 */
public abstract class BaseCommandFiles extends BaseCommand {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    protected FileUtils fileUtils = FileUtils.getInstance();
    protected EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);

}
