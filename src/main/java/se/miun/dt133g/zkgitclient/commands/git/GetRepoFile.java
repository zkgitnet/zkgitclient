package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public final class GetRepoFile extends BaseCommandGit implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private GetRepoFileInfo getRepoFileInfo = new GetRepoFileInfo();

    @Override public String execute() {

        LOGGER.info("Preparing to get repo file from remote server");

        Map<String, String> responseMap = new HashMap<>();
        Map<String, String> infoResponse = extractResponseToMap(getRepoFileInfo.execute());
        LOGGER.finest("Remote Signature: " + infoResponse.get(AppConfig.DB_REPO_HASH));
        LOGGER.finest("Current Signature: " + currentRepo.getRepoSignature());
        LOGGER.finest(infoResponse.get(AppConfig.DB_IV));
        credentials.setIv(infoResponse.get(AppConfig.DB_IV));

        if (currentRepo.getRepoSignature().contains(infoResponse.get(AppConfig.DB_REPO_HASH))) {
            responseMap.put(AppConfig.COMMAND_SUCCESS, AppConfig.STATUS_REPO_UPTODATE);
            return utils.mapToString(responseMap);
        } else {
            currentRepo.setRepoSignature(infoResponse.get(AppConfig.DB_REPO_HASH));
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
                        byte[] buffer = new byte[4096]; // Buffer to store chunks of data
                        int bytesRead;
                        while ((bytesRead = fileStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead); // Write the bytes to the file
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

                        // Pass the decrypted file stream to unzipDirectoryStream
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
