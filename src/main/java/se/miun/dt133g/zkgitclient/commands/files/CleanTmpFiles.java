package se.miun.dt133g.zkgitclient.commands.files;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.support.AppConfig;

public final class CleanTmpFiles extends BaseCommandFiles implements Command {

    @Override public String execute() {

        aesHandler.setInput(currentRepo.getRepoName() + AppConfig.ZIP_SUFFIX);
        aesHandler.encrypt();
        currentRepo.setEncFileName(utils.base64ToHex(aesHandler.getOutput()));

        fileUtils.cleanTmpFiles();

        return AppConfig.NONE;
    }
}
