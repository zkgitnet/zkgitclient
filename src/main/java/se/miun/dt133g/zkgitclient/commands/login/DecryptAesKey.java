package se.miun.dt133g.zkgitclient.commands.login;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.logging.Logger;

public final class DecryptAesKey extends BaseCommandLogin implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    @Override public String execute() {
        LOGGER.info("Decrypting AES Key");
        
        rsaHandler.setRsaKey(credentials.getPrivRsa());
        rsaHandler.setInput(credentials.getEncAesKey());
        rsaHandler.decrypt();
        credentials.setAesKey(rsaHandler.getOutput());

        LOGGER.finest(rsaHandler.getOutput());
        return rsaHandler.getOutput();
    }
}
