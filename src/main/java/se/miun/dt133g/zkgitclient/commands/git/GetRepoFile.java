package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Command to retrieve and process repository files from a remote server.
 * This command checks repository updates, retrieves the repository file,
 * decrypts and decompresses it, and then saves it locally.
 * @author Leif Rogell
 */
public final class GetRepoFile extends BaseCommandGit implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private GetRepoFileInfo getRepoFileInfo = new GetRepoFileInfo();

    /**
     * Executes the command to retrieve and process the repository file.
     * It checks for repository updates, retrieves the file, decrypts,
     * decompresses, and saves it locally.
     * @return a response indicating success or failure of the operation.
     */
    @Override
    public String execute() {

        LOGGER.info("Preparing to get repo file from remote server");

        Map<String, String> responseMap = new HashMap<>();
        Map<String, String> infoResponse = extractResponseToMap(getRepoFileInfo.execute());
        LOGGER.finest("Remote Signature: " + infoResponse.get(AppConfig.DB_REPO_HASH));
        LOGGER.finest("Current Signature: " + currentRepo.getRepoSignature());
        LOGGER.finest(infoResponse.get(AppConfig.DB_IV));

        try {
            if (currentRepo.getRepoSignature().contains(infoResponse.get(AppConfig.DB_REPO_HASH))) {
                responseMap.put(AppConfig.COMMAND_SUCCESS, AppConfig.STATUS_REPO_UPTODATE);
                return utils.mapToString(responseMap);
            } else if (infoResponse.get(AppConfig.DB_IV) == null) {
                ivHandler.encrypt();
                credentials.setIv(ivHandler.getOutput());
                responseMap.put(AppConfig.COMMAND_SUCCESS, AppConfig.STATUS_REPO_NEW);
                return utils.mapToString(responseMap);
            } else {
                currentRepo.setRepoSignature(infoResponse.get(AppConfig.DB_REPO_HASH));
                credentials.setIv(infoResponse.get(AppConfig.DB_IV));
            }
        } catch (NullPointerException e) {
            LOGGER.severe("Could not retrieve repo info from remote");
            return AppConfig.NONE;
        }

        sha256Handler.setInput(currentRepo.getRepoName()
                               .replace(AppConfig.ZIP_SUFFIX, AppConfig.NONE));
        sha256Handler.encrypt();

        return performEncryption(currentRepo.getRepoName())
            .map(encFileName -> {
                    Map<String, String> postData = new HashMap<>();
                    postData.put(AppConfig.COMMAND_KEY,
                                 AppConfig.COMMAND_REQUEST_REPO_FILE);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR,
                                 credentials.getAccountNumber());
                    postData.put(AppConfig.CREDENTIAL_USERNAME,
                                 credentials.getUsername());
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN,
                                 credentials.getAccessToken());
                    postData.put(AppConfig.REPO_NAME_HASH,
                                 sha256Handler.getOutput());
                    postData.put(AppConfig.REPO_SIGNATURE,
                                 currentRepo.getRepoSignature());

                    File file = Paths.get(System.getProperty(AppConfig.JAVA_TMP),
                                          sha256Handler.getOutput()).toFile();
                    InputStream fileStream = prepareAndSendGetPostRequest(postData);
                    if (fileStream == null) {
                        LOGGER.severe("Could not retrieve input stream");
                    }

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4 * AppConfig.ONE_KB];
                        int bytesRead;
                        while ((bytesRead = fileStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }
                    } catch (Exception e) {
                        LOGGER.severe("Could not write file: " + e.getMessage());
                    }

                    aesFileHandler.setInput(sha256Handler.getOutput());
                    aesFileHandler.setAesKey(credentials.getAesKey());
                    aesFileHandler.setIv(credentials.getIv());
                    aesFileHandler.decrypt();

                    File decryptedFile = new File(System.getProperty(AppConfig.JAVA_TMP), "test_repo2");
                    try (FileInputStream decryptedInputStream = new FileInputStream(decryptedFile)) {
                        LOGGER.finest("Starting decompression of decrypted file.");

                        fileUtils.unzipDirectoryStream(decryptedInputStream);

                        LOGGER.finest("Decompression complete.");
                    } catch (IOException e) {
                        LOGGER.severe("Failed to read decrypted file or unzip: " + e.getMessage());
                    }

                    //performFileDecryption(file, AppConfig.JAVA_TMP + currentRepo.getRepoName());

                    responseMap.put(AppConfig.COMMAND_SUCCESS, AppConfig.NONE);
                    return utils.mapToString(responseMap);
                })
            .orElse(AppConfig.NONE);
    }
}
