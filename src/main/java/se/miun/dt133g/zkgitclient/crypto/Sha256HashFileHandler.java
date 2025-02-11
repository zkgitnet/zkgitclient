package se.miun.dt133g.zkgitclient.crypto;

import se.miun.dt133g.zkgitclient.support.Utils;
import se.miun.dt133g.zkgitclient.support.AppConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Sha256HashFileHandler implements EncryptionHandler {

    private static Sha256HashFileHandler INSTANCE;
    private Utils utils = Utils.getInstance();

    private String input;
    private String output;
    private String rsaKey;
    private byte[] aesKey;
    private byte[] iv;

    private Sha256HashFileHandler() { }

    public static Sha256HashFileHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sha256HashFileHandler();
        }
        return INSTANCE;
    }

    @Override public void encrypt() {
        output = generateSha256HexString(input);
    }

    @Override public void decrypt() { }

    private String generateSha256HexString(final String input) {
        try {
            File file = new File(input);
            byte[] fileBytes = Files.readAllBytes(file.toPath());  // Read the file bytes
            MessageDigest md = MessageDigest.getInstance(AppConfig.CRYPTO_SHA_256);
            byte[] digest = md.digest(fileBytes);  // Compute the SHA-256 hash of the file content
            return utils.bytesToHex(digest);
        } catch (NoSuchAlgorithmException | IOException e) {
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
