package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public final class GetRepoFileInfo extends BaseCommandGit implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private final String ERROR_MESSAGE = "{" + AppConfig.ERROR_KEY
        + "=" + AppConfig.STATUS_REPO_UPTODATE + ",}";

    @Override public String execute() {

        LOGGER.info("Preparing to retrieve repo file information from remote server");
        
        return performEncryption(currentRepo.getRepoName())
            .map(encFileName -> {
                    Map<String, String> postData = new HashMap<>();

                    sha256Handler.setInput(currentRepo.getRepoName()
                                           .replace(AppConfig.ZIP_SUFFIX, AppConfig.NONE));
                    sha256Handler.encrypt();
                    
                    postData.put(AppConfig.COMMAND_KEY,
                                 AppConfig.COMMAND_REQUEST_REPO_INFO);
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

                    String response = conn.sendPostRequest(postData);
                    LOGGER.finest("RepoName: " + currentRepo.getRepoName() + ", Hash: "
                                  + sha256Handler.getOutput());
                    LOGGER.finest("GetRepoFileInfoResponse: " + response);
                    return response;
                })
            .orElse(ERROR_MESSAGE);
    }
}
