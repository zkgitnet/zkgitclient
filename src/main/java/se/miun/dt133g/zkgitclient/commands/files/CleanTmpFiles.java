package se.miun.dt133g.zkgitclient.commands.files;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.logging.Logger;

public final class CleanTmpFiles extends BaseCommandFiles implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    @Override public String execute() {

        try {
            aesHandler.setInput(currentRepo.getRepoName() + AppConfig.ZIP_SUFFIX);
            aesHandler.encrypt();
            currentRepo.setEncFileName(utils.base64ToHex(aesHandler.getOutput()));
            
            fileUtils.cleanTmpFiles();
            return "{" + AppConfig.COMMAND_SUCCESS
                + "="
                + AppConfig.NONE
                + ",}";
            
        } catch (Exception e) {
            return "{" + AppConfig.ERROR_KEY
                + "="
                + e.getMessage()
                + ",}";
        }
    }
}
