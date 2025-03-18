package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.logger.ZkGitLogger;
import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public final class Sha256HashHandler implements EncryptionHandler {

    private static Sha256HashHandler INSTANCE;
    private final Logger LOGGER = ZkGitLogger.getLogger(this.getClass());
    private Utils utils = Utils.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    private Sha256HashHandler() { }

    public static Sha256HashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sha256HashHandler();
        }
        return INSTANCE;
    }

    @Override public void encrypt() {
        output = generateSha256HexString(input);
    }

    @Override public void decrypt() { }

    private String generateSha256HexString(final String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(AppConfig.CRYPTO_SHA_256);
            byte[] digest = md.digest(input.getBytes());
            return utils.bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void setInput(final String input) {
        this.input = input;
    }

    @Override public void setAesKey(final byte[] aesKey) {
        this.aesKey = aesKey;
    }

    @Override public void setRsaKey(final String rsaKey) {
        this.rsaKey = rsaKey;
    }

    @Override public void setIv(final byte[] iv) {
        this.iv = iv;
    }

    @Override public String getOutput() {
        return output;
    }
}
