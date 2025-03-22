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

                    sha256Handler.setInput(currentRepo.getRepoName());
                    sha256Handler.encrypt();

                    Map<String, String> postData = new HashMap<>();
                    postData.put(AppConfig.COMMAND_KEY,
                                 AppConfig.COMMAND_ASSEMBLY_REPO);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR,
                                 credentials.getAccountNumber());
                    postData.put(AppConfig.CREDENTIAL_USERNAME,
                                 credentials.getUsername());
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN,
                                 credentials.getAccessToken());
                    /*postData.put(AppConfig.NUM_CHUNKS,
                      Integer.toString(fileNames.length));*/
                    postData.put(AppConfig.ENC_FILE_NAME,
                                 sha256Handler.getOutput());
                    postData.put(AppConfig.REPO_SIGNATURE,
                                 currentRepo.getRepoSignature());

                    
                    return performFileEncryption(currentRepo.getRepoPath(), postData,
                                                 sha256Handler.getOutput());
                })
            .orElseGet(() -> {
                    return createErrorResponse(AppConfig.ERROR_CONNECTION);
                });
    }
}
