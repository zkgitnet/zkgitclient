package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.commands.Command;
import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

public final class GetRepoFile extends BaseCommandGit implements Command {

    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private GetRepoFileInfo getRepoFileInfo = new GetRepoFileInfo();

    @Override public String execute() {

        LOGGER.info("Preparing to get repo file from remote server");

        Map<String, String> infoResponse = extractResponseToMap(getRepoFileInfo.execute());
        LOGGER.finest(infoResponse.get(AppConfig.DB_REPO_HASH));
        currentRepo.setRepoSignature(infoResponse.get(AppConfig.DB_REPO_HASH));
        LOGGER.finest(infoResponse.get(AppConfig.DB_IV));
        credentials.setIv(infoResponse.get(AppConfig.DB_IV));
        
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
                    postData.put(AppConfig.ENC_FILE_NAME,
                                 encFileName);
                    postData.put(AppConfig.REPO_SIGNATURE,
                                 currentRepo.getRepoSignature());

                    String response = prepareAndSendGetPostRequest(postData);
                    LOGGER.finest("GetRepoFileResponse: " + response);
                    return processResponse(response);
                })
            .orElse(AppConfig.NONE);
    }

    private String processResponse(final String response) {
        if (response.contains(AppConfig.COMMAND_SUCCESS)) {
            return performDecryption().orElse(AppConfig.ERROR_KEY);
        } else {
            return "{" + AppConfig.ERROR_KEY
                + "="
                + AppConfig.STATUS_REPO_UPTODATE
                + ",}";
        }
    }
}
