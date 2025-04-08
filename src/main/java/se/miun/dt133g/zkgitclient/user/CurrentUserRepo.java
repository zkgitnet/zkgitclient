package se.miun.dt133g.zkgitclient.user;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;

import java.util.logging.Logger;

/**
 * Singleton class representing the current user's repository information,
 * including repository details and associated encryption data.
 * @author Leif Rogell
 */
public final class CurrentUserRepo {

    private static CurrentUserRepo INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private String encFileName;
    private String repoName;
    private String repoPath;
    private String repoSignature;
    private String iv;

    /**
     * Private constructor to prevent instantiation.
     */
    private CurrentUserRepo() { }

    /**
     * Returns the singleton instance of CurrentUserRepo.
     * @return the CurrentUserRepo instance.
     */
    public static CurrentUserRepo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CurrentUserRepo();
        }
        return INSTANCE;
    }

    /**
     * Sets the initialization vector (IV) for the current repository.
     * @param iv the initialization vector.
     */
    public void setIv(final String iv) {
        LOGGER.finest("currentRepo (IV): " + iv);
        this.iv = iv;
    }

    /**
     * Sets the path to the current repository.
     * @param repoPath the path to the repository.
     */
    public void setRepoPath(final String repoPath) {
        LOGGER.finest("currentRepo (RepoPath): " + repoPath);
        this.repoPath = repoPath;
    }

    /**
     * Sets the name of the current repository.
     * @param repoName the name of the repository.
     */
    public void setRepoName(final String repoName) {
        LOGGER.finest("currentRepo (RepoName): " + repoName);
        this.repoName = repoName;
    }

    /**
     * Sets the signature associated with the current repository.
     * @param repoSignature the signature of the repository.
     */
    public void setRepoSignature(final String repoSignature) {
        LOGGER.finest("currentRepo (Signature): " + repoSignature);
        this.repoSignature = repoSignature;
    }

    /**
     * Sets the encrypted file name for the current repository.
     * @param encFileName the name of the encrypted file.
     */
    public void setEncFileName(final String encFileName) {
        LOGGER.finest("currentRepo (EncFileName): " + encFileName);
        this.encFileName = encFileName;
    }

    /**
     * Gets the initialization vector (IV) for the current repository.
     * @return the initialization vector.
     */
    public String getIv() {
        return iv;
    }

    /**
     * Gets the name of the current repository.
     * @return the repository name.
     */
    public String getRepoName() {
        return repoName;
    }

    /**
     * Gets the path to the current repository.
     * @return the repository path.
     */
    public String getRepoPath() {
        return repoPath;
    }

    /**
     * Gets the signature associated with the current repository.
     * @return the repository signature.
     */
    public String getRepoSignature() {
        return repoSignature;
    }

    /**
     * Gets the name of the encrypted file associated with the current repository.
     * @return the encrypted file name.
     */
    public String getEncFileName() {
        return encFileName;
    }
}
