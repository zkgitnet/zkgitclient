package se.miun.dt133g.zkgitclient.commands.git;

import se.miun.dt133g.zkgitclient.commands.BaseCommand;
import se.miun.dt133g.zkgitclient.crypto.EncryptionHandler;
import se.miun.dt133g.zkgitclient.crypto.EncryptionFactory;
import se.miun.dt133g.zkgitclient.support.FileUtils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.util.Optional;

public abstract class BaseCommandGit extends BaseCommand {

    protected EncryptionHandler aesFileHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES_FILE);
    protected EncryptionHandler aesHandler = EncryptionFactory.getEncryptionHandler(AppConfig.CRYPTO_AES);
    protected FileUtils fileUtils = FileUtils.getInstance();

    protected Optional<String> performEncryption(final String input) {
        try {
            aesHandler.setIv(credentials.getIv());
            aesHandler.setAesKey(credentials.getAesKey());
            aesHandler.setInput(currentRepo.getRepoName());
            aesHandler.encrypt();
            String encFileName = utils.base64ToHex(aesHandler.getOutput());
            currentRepo.setEncFileName(encFileName);
            return Optional.of(encFileName);
        } catch (Exception e) {
            System.err.println("Encryption failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    protected void performFileEncryption(final String fileName) {
        try {
            aesFileHandler.setInput(fileName);
            aesFileHandler.setAesKey(credentials.getAesKey());
            aesFileHandler.setIv(credentials.getIv());
            aesFileHandler.encrypt();
        } catch (Exception e) {

        }
    }

    protected Optional<String> performDecryption() {
        try {
            aesFileHandler.setInput(currentRepo.getEncFileName());
            aesFileHandler.setAesKey(credentials.getAesKey());
            aesFileHandler.setIv(credentials.getIv());
            aesFileHandler.decrypt();
            System.out.println("Decrypion success");
            return Optional.of("{" + AppConfig.COMMAND_SUCCESS + "=,}");
        } catch (Exception e) {
            System.out.println("Decryption failed: " + e.getMessage());
            return Optional.empty();
        }
    }
}
