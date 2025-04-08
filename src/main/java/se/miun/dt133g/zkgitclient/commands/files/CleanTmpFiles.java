package se.miun.dt133g.zkgitclient.commands.files;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.logging.Logger;

/**
 * Command for cleaning temporary files in the ZkGit client.
 * This command performs the cleanup of temporary files associated with the current repository. It also
 * encrypts the repository's file name before initiating the cleanup process.
 * @author Leif Rogell
 */
public final class CleanTmpFiles extends BaseCommandFiles implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());

    /**
     * Executes the command to clean temporary files.
     * This method encrypts the repository name using AES, sets the encrypted file name, and then cleans
     * the temporary files using the {@link FileUtils#cleanTmpFiles()} method. If the operation is successful,
     * it returns a success response. If an error occurs, an error response with the exception message is returned.
     * @return a JSON-like string indicating success or failure of the operation.
     */
    @Override
    public String execute() {

        try {
            aesHandler.setInput(currentRepo.getRepoName() + AppConfig.ZIP_SUFFIX);
            aesHandler.encrypt();
            currentRepo.setEncFileName(utils.base64ToHex(aesHandler.getOutput()));

            fileUtils.cleanTmpFiles();
            return "{" + AppConfig.COMMAND_SUCCESS + "="
                + AppConfig.NONE + ",}";

        } catch (Exception e) {
            return "{" + AppConfig.ERROR_KEY + "="
                + e.getMessage() + ",}";
        }
    }
}
