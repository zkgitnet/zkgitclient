package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Command to send a repository file to a remote server after encrypting it.
 * This class encrypts the repository file name and performs the transfer
 * to the server with the necessary credentials and metadata.
 * @author Leif Rogell
 */
public final class SendRepoFile extends BaseCommandGit implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private EncryptionHandler sha256FileHandler =
        EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_SHA_256_FILE);

    /**
     * Executes the command to send the repository file to the remote server.
     * The method encrypts the repository file name, prepares the necessary
     * metadata, and calls the encryption and file transfer functions to
     * securely send the file.
     * @return a response indicating success or failure of the operation.
     */
    @Override
    public String execute() {
        return Optional.of(conn.getServerConnectivity())
            .filter(valid -> credentials.hasAccessToken())
            .map(valid -> {

                    sha256Handler.setInput(currentRepo.getRepoName());
                    sha256Handler.encrypt();

                    LOGGER.finest(currentRepo.getRepoSignature());

                    Map<String, String> postData = new HashMap<>();
                    postData.put(AppConfig.COMMAND_KEY,
                                 AppConfig.COMMAND_TRANSFER_REPO);
                    postData.put(AppConfig.CREDENTIAL_ACCOUNT_NR,
                                 credentials.getAccountNumber());
                    postData.put(AppConfig.CREDENTIAL_USERNAME,
                                 credentials.getUsername());
                    postData.put(AppConfig.CREDENTIAL_ACCESS_TOKEN,
                                 credentials.getAccessToken());
                    postData.put(AppConfig.ENC_FILE_NAME,
                                 sha256Handler.getOutput());
                    postData.put(AppConfig.REPO_SIGNATURE,
                                 currentRepo.getRepoSignature());
                    postData.put(AppConfig.DB_IV,
                                 Arrays.toString(utils.byteArrayToIntArray(credentials.getIv())));

                    return performFileEncryption(System.getProperty(AppConfig.JAVA_TMP)
                                                 + "/" + AppConfig.TMP_PREFIX
                                                 + currentRepo.getRepoName(),
                                                 postData,
                                                 sha256Handler.getOutput());
                })
            .orElseGet(() -> {
                    return createErrorResponse(AppConfig.ERROR_CONNECTION);
                });
    }
}
