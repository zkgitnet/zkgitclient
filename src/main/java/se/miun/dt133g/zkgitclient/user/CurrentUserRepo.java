package se.miun.dt133g.zkgitclient.user;

public final class CurrentUserRepo {

    private static CurrentUserRepo INSTANCE;
    private String encFileName;
    private String repoName;
    private String repoSignature;

    private CurrentUserRepo() { }

    public static CurrentUserRepo getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CurrentUserRepo();
        }
        return INSTANCE;
    }

    public void setRepoName(final String repoName) {
        this.repoName = repoName;
    }

    public void setRepoSignature(final String repoSignature) {
        this.repoSignature = repoSignature;
    }

    public void setEncFileName(final String encFileName) {
        this.encFileName = encFileName;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getRepoSignature() {
        return repoSignature;
    }

    public String getEncFileName() {
        return encFileName;
    }
}
