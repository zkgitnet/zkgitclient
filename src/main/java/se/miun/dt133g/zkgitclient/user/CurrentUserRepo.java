package se.miun.dt133g.zkgitclient.user;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;

import java.util.logging.Logger;

public final class CurrentUserRepo {

    private static CurrentUserRepo INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private String encFileName;
    private String repoName;
    private String repoPath;
    private String repoSignature;
    private String iv;

    private CurrentUserRepo() { }

    public static CurrentUserRepo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CurrentUserRepo();
        }
        return INSTANCE;
    }

    public void setIv(final String iv) {
        LOGGER.finest("currentRepo (IV): " + iv);
        this.iv = iv;
    }

    public void setRepoPath(final String repoPath) {
        LOGGER.finest("currentRepo (RepoPath): " + repoPath);
        this.repoPath = repoPath;
    }

    public void setRepoName(final String repoName) {
        LOGGER.finest("currentRepo (RepoName): " + repoName);
        this.repoName = repoName;
    }

    public void setRepoSignature(final String repoSignature) {
        LOGGER.finest("currentRepo (Signature): " + repoSignature);
        this.repoSignature = repoSignature;
    }

    public void setEncFileName(final String encFileName) {
        LOGGER.finest("currentRepo (EncFileName): " + encFileName);
        this.encFileName = encFileName;
    }

    public String getIv() {
        return iv;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public String getRepoSignature() {
        return repoSignature;
    }

    public String getEncFileName() {
        return encFileName;
    }
}
