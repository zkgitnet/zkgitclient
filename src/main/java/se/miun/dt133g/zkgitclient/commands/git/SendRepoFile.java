package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public final class SendRepoFile extends BaseCommandGit implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private EncryptionHandler sha256FileHandler =
        EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_SHA_256_FILE);

    @Override public String execute() {
        return Optional.of(conn.getServerConnectivity())
            .filter(valid -> credentials.hasAccessToken())
            .map(valid -> {

                    performEncryption(currentRepo.getRepoName());
                    performFileEncryption(currentRepo.getRepoName());

                    String[] fileNames = fileUtils.splitFile(currentRepo.getEncFileName());

                    for (String fileName : fileNames) {
                        retryIfTransmissionFailure(fileName);
                    }

                    Map<String, String> postData = new HashMap<>();
                    postData.put(AppConfig.COMMAND_KEY,
                                 AppConfig.COMMAND_ASSEMBLY_REPO);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR,
                                 credentials.getAccountNumber());
                    postData.put(AppConfig.CREDENTIAL_USERNAME,
                                 credentials.getUsername());
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN,
                                 credentials.getAccessToken());
                    postData.put(AppConfig.NUM_CHUNKS,
                                 Integer.toString(fileNames.length));
                    postData.put(AppConfig.ENC_FILE_NAME,
                                 currentRepo.getEncFileName());
                    postData.put(AppConfig.REPO_SIGNATURE,
                                 currentRepo.getRepoSignature());

                    return prepareAndSendPostRequest(postData);
                })
            .orElseGet(() -> {
                    return createErrorResponse(AppConfig.ERROR_CONNECTION);
                });
    }

    private boolean retryIfTransmissionFailure(final String encFilePath) {

        sha256FileHandler.setInput(encFilePath);
        sha256FileHandler.encrypt();
        
        for (int i = 0; i < AppConfig.NUM_RETRIES; i++) {
            Map<String, String> postData = Map.of(
                                                  AppConfig.COMMAND_KEY,
                                                  AppConfig.COMMAND_TRANSFER_REPO,
                                                  AppConfig.CREDENTIAL_ACCOUNT_NR,
                                                  credentials.getAccountNumber(),
                                                  AppConfig.CREDENTIAL_USERNAME,
                                                  credentials.getUsername(),
                                                  AppConfig.CREDENTIAL_ACCESS_TOKEN,
                                                  credentials.getAccessToken(),
                                                  AppConfig.ENC_FILE_NAME,
                                                  new File(encFilePath).getName(),
                                                  AppConfig.FILE_SHA256_HASH,
                                                  sha256FileHandler.getOutput()
                                                  );

            String result = prepareAndSendFilePostRequest(Paths.get(encFilePath).toFile(), postData);

            if (result.contains(AppConfig.COMMAND_SUCCESS)) {
                return true;
            }

            if (result.contains("Connection failure")) {
                try {
                    Thread.sleep(AppConfig.ONE_SECOND);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }
}
